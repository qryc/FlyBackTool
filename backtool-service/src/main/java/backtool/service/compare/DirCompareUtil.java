package backtool.service.compare;

import backtool.service.FxConst;
import fly4j.common.file.FileNameUtilHelper;
import fly4j.common.file.FileUtil;
import fly4j.common.file.FilenameUtil;
import fly4j.common.util.StringConst;
import fly4j.common.util.map.LinkedHashMapUtil;
import fly4j.common.util.map.MapUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DirCompareUtil {
    /**
     *
     * @param lefts 左边的重复文件
     * @param rights 右边的重复文件
     * @param md5 重复文件的MD5
     */
    public static record LeftRightSameObj(List<File> lefts, List<File> rights, String md5) {
        @Override
        public String toString() {
            return "LeftRightSameObj{" +
                    "lefts=" + lefts +
                    ", rights=" + rights +
                    '}';
        }
    }

    public static record OneSameObj(List<File> sames, String md5) {
    }

    public static record LeftAloneObj(List<File> sames, String md5) {
    }

    public static record RightAloneObj(List<File> sames, String md5) {
    }

    public static record LeftRightModifyObj(File left, File right) {
    }


    public static class CompareResult {

        ////不区分修改
        //左边右边相等的文件
        public List<LeftRightSameObj> leftRightSameObjs = new ArrayList<>();
        //左边独有的文件：左边存在，右边不存在
        public List<File> leftAloneFiles = new ArrayList<>();
        //左边独有的文件明细：MD5和文件列表，包含重复的和不重复的
        public List<LeftAloneObj> leftAloneObjs = new ArrayList<>();
        //左边独有的文件明细：MD5和文件列表，只包含重复的
        public List<LeftAloneObj> leftAloneSameObjs = new ArrayList<>();//文件夹内重复
        //右边独立的文件：右边存在，左边不存在
        public List<File> rightAloneFiles = new ArrayList<>();
        //右边独有的文件明细：MD5和文件列表，包含重复的和不重复的
        public List<RightAloneObj> rightAloneObjs = new ArrayList<>();
        //右边独有的文件明细：MD5和文件列表，只包含重复的
        public List<RightAloneObj> rightAloneSameObjs = new ArrayList<>();//文件夹内重复


        /**
         * 两个文件夹对比，区分修改
         **/
        //修改的文件
        public List<LeftRightModifyObj> leftRightModifyObjs = new ArrayList<>();
        //左边独有文件，不是修改
        public List<File> leftAloneNotModifyFiles = new ArrayList<>();
        //右边独有文件，不是修改
        public List<File> rightAloneNotModifyFiles = new ArrayList<>();

        //所有的差异，包含左边和右边的变更
        public List<File> getDiff() {
            List<File> list = new ArrayList<>();
            list.addAll(leftAloneFiles);
            list.addAll(rightAloneFiles);
            return list;
        }


    }


    public static List<LeftRightSameObj> compareTwoSame(String leftDirStr, String rightDirStr, Predicate<File> noNeedCalMd5FileFilter) {
        List<LeftRightSameObj> leftRightSameObjs = new ArrayList<>();
        /**第一步：通过长度进行第一轮筛选长度一致的可疑文件**/
        //Ready的md5
        Map<File, String> leftLenMap_file = DigestCalculate.getDirDigestFileMap(leftDirStr, DigestCalculate.DigestType.LEN, noNeedCalMd5FileFilter);
        //取得新文件夹的Md5
        Map<File, String> rightLenMap_file = DigestCalculate.getDirDigestFileMap(rightDirStr, DigestCalculate.DigestType.LEN, noNeedCalMd5FileFilter);


        //查找新文件夹中和老的文件夹长度一致的文件
        List<File> leftDoubleFilesFromLen = new ArrayList<>();
        List<File> rightDoubleFileFromLen = new ArrayList<>();
        LinkedHashMap<String, List<File>> leftGroupFileMap_len = LinkedHashMapUtil.convert2ValueMap(leftLenMap_file);
        rightLenMap_file.forEach((file, lenStr) -> {
            if (leftGroupFileMap_len.containsKey(lenStr)) {
                //长度相等的嫌疑统统加入
                leftDoubleFilesFromLen.addAll(leftGroupFileMap_len.get(lenStr));
                rightDoubleFileFromLen.add(file);
            }
        });

        //进一步删选MD5一致的文件
        //先生成新的反向
        LinkedHashMap<String, List<File>> leftRevertMap_md5 = DigestCalculate.getFilesMd5DoubleMap(leftDoubleFilesFromLen);
        LinkedHashMap<String, List<File>> rightRevertMap_md5 = DigestCalculate.getFilesMd5DoubleMap(rightDoubleFileFromLen);
        //遍历老的，如果新的有存在重复，则删除
        //key 要删除的，value，重复文件
        leftRevertMap_md5.forEach((leftMd5, leftFiles) -> {
            if (rightRevertMap_md5.containsKey(leftMd5)) {
                LeftRightSameObj leftRightSameObj = new LeftRightSameObj(leftFiles, rightRevertMap_md5.get(leftMd5), leftMd5);
                leftRightSameObjs.add(leftRightSameObj);
//                        //要删除的
            }
        });
        return leftRightSameObjs;
    }

    public static List<OneSameObj> compareOneDir(String leftDirStr, Predicate<File> noNeedCalMd5FileFilter) {
        List<OneSameObj> oneSameObjs = new ArrayList<>();

        /**第一步，使用Length初筛**/
        //取得文件长度的Map
        LinkedHashMap<File, String> fileLengthMapAll = DigestCalculate.getDirDigestFileMap(leftDirStr, DigestCalculate.DigestType.LEN, noNeedCalMd5FileFilter);
        //转换Map：Key:文件长度，value：长度相等的文件列表
        LinkedHashMap<String, List<File>> lengthGroupFileMap_len = LinkedHashMapUtil.convert2ValueMap(fileLengthMapAll);
        //从ValueMap中查找重复文件
        LinkedHashMap<String, List<File>> doubleFileMapByLen_len = LinkedHashMapUtil.filterLinkedHashMap(lengthGroupFileMap_len, e -> e.getValue().size() > 1);
        //简化结构，统一放入，不用按长度分堆 ，以前算法保持分堆，后续的二次检验在分堆中查找
        List<File> doubleFilesByLen = doubleFileMapByLen_len.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());


        /**第二步，二次MD5检验**/
        //长度相等的一堆数据，生成新的反相Map
        LinkedHashMap<String, List<File>> md5RevertMap_md5 = DigestCalculate.getFilesMd5DoubleMap(doubleFilesByLen);
        //删选MD5相等的
        LinkedHashMapUtil.filterLinkedHashMap(md5RevertMap_md5, e -> e.getValue().size() > 1).forEach((md5Str, files) -> {
           // if (files.get(0).length() > FileUtils.ONE_MB)
                oneSameObjs.add(new OneSameObj(files, md5Str));
        });
        return oneSameObjs;
    }

    /**
     * 对比两个文件夹的差异
     * @param leftDirStr 左边文件夹
     * @param rightDirStr 右边文件夹
     * @param noNeedCalMd5FileFilter 不需要对比的过滤器
     * @return 对比结果
     */
    public static CompareResult compareTwoFull(String leftDirStr, String rightDirStr, Predicate<File> noNeedCalMd5FileFilter) {
        CompareResult compreResult = new CompareResult();
        //取得上次的md5
        Map<File, String> leftMap_File = DigestCalculate.getDirDigestFileMap(leftDirStr, DigestCalculate.DigestType.MD5, noNeedCalMd5FileFilter);
        //取得文件夹的Md5
        Map<File, String> rightMap_File = DigestCalculate.getDirDigestFileMap(rightDirStr, DigestCalculate.DigestType.MD5, noNeedCalMd5FileFilter);

        LinkedHashMap<String, List<File>> leftGroupFileMap_md5 = LinkedHashMapUtil.convert2ValueMap(leftMap_File);
        LinkedHashMap<String, List<File>> rightGroupFileMap_md5 = LinkedHashMapUtil.convert2ValueMap(rightMap_File);


        /**
         * 区分是否相同
         */
        //遍历历史的，查找删除的Key
        leftGroupFileMap_md5.forEach((leftMd5, leftFiles) -> {
            List<File> rightFiles = rightGroupFileMap_md5.get(leftMd5);
            if (null == rightFiles) {//左边独立
                compreResult.leftAloneFiles.addAll(leftFiles);
                compreResult.leftAloneObjs.add(new LeftAloneObj(leftFiles, leftMd5));
                if (leftFiles.size() > 1) {//独立的重复文件
                    compreResult.leftAloneSameObjs.add(new LeftAloneObj(leftFiles, leftMd5));
                }
            } else {//两边相等
                compreResult.leftRightSameObjs.add(new LeftRightSameObj(leftFiles, rightFiles, leftMd5));
            }
        });

        //右边独立
        rightGroupFileMap_md5.forEach((rightMd5, rightFiles) -> {
            List<File> leftFiles = leftGroupFileMap_md5.get(rightMd5);
            if (null == leftFiles) {//右边独立
                compreResult.rightAloneFiles.addAll(rightFiles);
                compreResult.rightAloneObjs.add(new RightAloneObj(rightFiles, rightMd5));
                if (rightFiles.size() > 1) {//独立的重复文件
                    compreResult.rightAloneSameObjs.add(new RightAloneObj(rightFiles, rightMd5));
                }
            }
        });
        /**
         * 区分修改
         */
        //左右独立有可能是同样的路径，逻辑上应该看做修改
        Set<String> modifyPaths = new HashSet<>();
        Map<String, File> leftFileMap_path = new HashMap<>();
        compreResult.leftAloneFiles.forEach(leftFile -> {
            String leftPath = FilenameUtil.getSubPathUnix(leftFile.getAbsolutePath(), leftDirStr);
            leftFileMap_path.put(leftPath, leftFile);
        });
        compreResult.rightAloneFiles.forEach(rightFile -> {
            String rightPath = FilenameUtil.getSubPathUnix(rightFile.getAbsolutePath(), rightDirStr);
            if (leftFileMap_path.containsKey(rightPath)) {
                //修改
                compreResult.leftRightModifyObjs.add(new LeftRightModifyObj(leftFileMap_path.get(rightPath), rightFile));
                modifyPaths.add(rightPath);
            }
        });
        compreResult.leftAloneNotModifyFiles = compreResult.leftAloneFiles.stream().filter(leftFile -> {
            String leftPath = FilenameUtil.getSubPathUnix(leftFile.getAbsolutePath(), leftDirStr);
            return !modifyPaths.contains(leftPath);
        }).collect(Collectors.toList());
        compreResult.rightAloneNotModifyFiles = compreResult.rightAloneFiles.stream().filter(rightFile -> {
            String rightPath = FilenameUtil.getSubPathUnix(rightFile.getAbsolutePath(), rightDirStr);
            return !modifyPaths.contains(rightPath);
        }).collect(Collectors.toList());
        //左右相等，有可能路径改变，逻辑上应该看做移动，此处不做细分，认为是相等的。
        return compreResult;
    }

}
