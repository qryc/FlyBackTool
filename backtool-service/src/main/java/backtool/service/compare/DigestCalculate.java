package backtool.service.compare;

import fly4j.common.file.FileUtil;
import fly4j.common.file.FilenameUtil;
import fly4j.common.util.map.LinkedHashMapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class DigestCalculate {
    public enum DigestType {
        MD5, LEN
    }
    public static LinkedHashMap<String, List<File>> getFilesMd5DoubleMap(List<File> files) {
        LinkedHashMap<String, List<File>> md5RevertMap_md5 = new LinkedHashMap<>();
        files.forEach(file -> {
            String md5 = FileUtil.getMD5(file);
            List<File> md5Files = md5RevertMap_md5.computeIfAbsent(md5, key -> new ArrayList<>());
            md5Files.add(file);
        });
        return md5RevertMap_md5;
    }

    public static LinkedHashMap<String, String> getDirDigestMap(String checkBaseDirStr, DigestType digestType, Predicate<File> noNeedCalMd5FileFilter) {
        LinkedHashMap<File, String> digestFileMap = getDirDigestFileMap(checkBaseDirStr, digestType, noNeedCalMd5FileFilter);
        return LinkedHashMapUtil.alterKey(digestFileMap, file -> FilenameUtil.getSubPathUnix(file.getAbsolutePath(), checkBaseDirStr));
    }

    public static LinkedHashMap<File, String> getDirDigestFileMap(String checkBaseDirStr, DigestType digestType, Predicate<File> noNeedCalMd5FileFilter) {
        return getDirDigestFileMap(new File(checkBaseDirStr), digestType, noNeedCalMd5FileFilter);
    }

    public static LinkedHashMap<File, String> getDirDigestFileMap(File checkBaseDir, DigestType digestType, Predicate<File> noNeedCalMd5FileFilter) {
        LinkedHashMap<File, String> md5FileMap = new LinkedHashMap<>();
        AtomicLong count = new AtomicLong(0);
        FileUtil.walkAllFileIgnoreMacShadowFile(checkBaseDir.toPath().toFile(), noNeedCalMd5FileFilter, file -> {
            //生成md5
            count.incrementAndGet();
            System.out.println("check file " + count + " :" + file.getAbsolutePath());
            md5FileMap.put(file, getMd5(file, digestType));
        });
        return md5FileMap;
    }


    public static String getMd5(File file, DigestType versionType) {
        return DigestType.LEN.equals(versionType) ? "" + file.length() : FileUtil.getMD5(file);
    }



}
