package backtool.service;

import fly4j.common.file.FileUtil;
import backtool.service.compare.DigestCalculate;
import fly4j.common.util.DateUtil;
import fly4j.common.util.FlyString;
import fly4j.common.os.OsUtil;
import fly4j.common.util.map.LinkedHashMapUtil;
import fly4j.common.util.map.MapUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Created by qryc on 2021/9/6
 */
public class DirVersionGen {

    public static record FileDigestModel(String name, String md5, String length) {
        @Override
        public String toString() {
            return new StringBuilder().append(name)
                    .append(",").append(md5)
                    .append(",").append(length).toString();
        }

        public static FileDigestModel of(File file) {
            return new FileDigestModel(file.getName(), FileUtil.getMD5(file), "" + file.length());
        }

        public static FileDigestModel of(String str) {
            String[] strs = str.trim().split(",");
            return new FileDigestModel(strs[0], strs[1], strs[2]);
        }
    }

    public static record VersionCheckResult(List<FileDigestModel> okFiles, List<FileDigestModel> deleteFiles) {

    }

//    public static String getDefaultVersionFileName(String beZipSourceDir) {
//        if (null == beZipSourceDir) {
//            beZipSourceDir = "";
//        }
//        return FlyString.getPlanText(beZipSourceDir) + DateUtil.getHourStr4Name(new Date()) + ".md5";
//    }
//
//    public static String saveDirVersionModel2File(String checkDirStr, Predicate<File> noNeedCalMd5FileFilter, Path md5StorePath) throws IOException {
//        String dirVersionStr = DirVersionGen.genDirVersionStr(checkDirStr,noNeedCalMd5FileFilter).toString();
//        Files.writeString(md5StorePath, dirVersionStr);
//        return dirVersionStr;
//    }

    public static List<FileDigestModel> genDirVersionModels(String checkDirStr, Predicate<File> noNeedCalMd5FileFilter) {
        List<FileDigestModel> list=new ArrayList<>();
        //文件版本信息
        AtomicLong count = new AtomicLong(0);
        File dirFile = new File(checkDirStr);
        FileUtil.walkAllFileIgnoreMacShadowFile(dirFile, noNeedCalMd5FileFilter, file -> {
            //生成md5
            count.incrementAndGet();
            //生成md5
            System.out.println("check file " + count + " :" + file.getAbsolutePath());
            list.add(FileDigestModel.of(file));
        });


        return list;
    }

    public static VersionCheckResult checkDirChangeByFile(String checkDirStr, File md5File, Predicate<File> noNeedCalMd5FileFilter) throws IOException {


        if (null == md5File) {
            throw new RuntimeException(" not have history file");
        }

        //取得上次的文件夹digest摘要信息
        List<String> historyStrs = Files.readAllLines(md5File.toPath());
        return checkDirChangeByStr(checkDirStr, historyStrs, noNeedCalMd5FileFilter);
    }

    public static VersionCheckResult checkDirChangeByStr(String checkDirStr, List<String> historyStrs, Predicate<File> noNeedCalMd5FileFilter) throws IOException {

        //取得上次的文件夹digest摘要信息
        List<FileDigestModel> fileDigestModels = new ArrayList<>();
        historyStrs.forEach(s -> {
            if (StringUtils.isNoneBlank(s) && !s.startsWith("//")) {
                fileDigestModels.add(FileDigestModel.of(s));
            }
        });
        //取得文件夹的当前的digest信息
        Map<String, String> currentMd5Map = DigestCalculate.getDirDigestMap(checkDirStr, DigestCalculate.DigestType.MD5, noNeedCalMd5FileFilter);
        //进一步删选MD5一致的文件
        //先生成新的反向
        LinkedHashMap<String, List<String>> currentMd5RevertMap_md5 = LinkedHashMapUtil.convert2ValueMap(currentMd5Map);

        //查找删除的
        List<FileDigestModel> okFiles = new ArrayList<>();
        List<FileDigestModel> deleteFiles = new ArrayList<>();
        fileDigestModels.forEach(digestModel -> {
            if (currentMd5RevertMap_md5.containsKey(digestModel.md5())) {
                okFiles.add(digestModel);
            } else {
                deleteFiles.add(digestModel);
            }
        });

        return new VersionCheckResult(okFiles, deleteFiles);

    }

}
