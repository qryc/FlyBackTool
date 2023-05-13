package backtool.service.search;

import fly4j.common.file.FileUtil;
import fly4j.common.util.ExceptionUtil;
import fly4j.common.util.StringConst;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用于查找大文件
 * Created by qryc on 2016/5/4.
 */
public class FileFinder {


    public static String findFile(String checkDirStr, String findName) throws IOException {
        StringBuilder info = new StringBuilder("文件查找结果：").append(StringUtils.LF);
        Set<String> fileNameSet = Arrays.stream(findName.split(",")).collect(Collectors.toSet());
        info.append(" findName:").append(fileNameSet).append(StringUtils.LF);

        info.append(checkDirStr).append(" check:").append(StringUtils.LF);
        FileUtil.walkAllFileAndDir(new File(checkDirStr), file -> {
            fileNameSet.forEach(name -> {
                if (file.getName().equals(name)) {
                    ExceptionUtil.wrapperRuntime(() -> info.append(file.getAbsolutePath() + " " + FileUtils.byteCountToDisplaySize(file.length())).append(StringConst.LF));
                }
            });
        });
        return info.toString();
    }


    public static String maxFile(String checkDir, int limit) {
        StringBuilder info = new StringBuilder("检查大文件结果：").append(StringUtils.LF);

        info.append(checkDir).append(" check:").append(StringUtils.LF);

        var files = FileUtils.listFiles(new File(checkDir), null, true);
        files.stream().filter(file -> !file.isDirectory()).sorted((f1, f2) -> (int) (f2.length() - f1.length())).limit(limit).forEach(file -> {
            info.append(file.getAbsolutePath()).append(" ")
                    .append(FileUtils.byteCountToDisplaySize(file.length())).append(StringConst.LF);
        });
        return info.toString();
    }

    public static void main(String[] args) throws Exception {
//        String checkDirStr = "";
//        String findName = "target,classes";
//        var result = findFile(checkDirStr, findName);
//        System.out.println(result);
        deleteShadowFiles(Path.of("/Volumes/KNOWLEDGE/workSpaceGit"), false);
    }

    public static String deleteShadowFiles(Path checkDirPath, boolean delete) throws IOException {
        StringBuilder info = new StringBuilder("删除MAC影子文件：").append(StringUtils.LF);
        Files.walk(checkDirPath).forEach(path -> {
            if (path.getFileName().toString().startsWith("._")) {
                if (delete) {
                    ExceptionUtil.wrapperRuntime(() -> Files.delete(path));
                    info.append("deleted:").append(path).append(StringUtils.LF);
                } else {
                    info.append("delete:").append(path).append(StringUtils.LF);
                }
            }
        });
        return info.toString();
    }

}
