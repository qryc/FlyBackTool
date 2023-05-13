package backtool.service.zip;

import backtool.service.compare.DirCompareUtil;
import fly4j.common.domain.FlyResult;
import backtool.service.compare.DigestCalculate;
import fly4j.common.file.zip.Zip4jTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * alter by qryc in 2020/07/04
 * 不再先拷贝，删除不需要备份文件，再压缩，直接压缩，通过提前规划好需要备份和不需要备份文件
 */
public class DirZipService {
    static final Logger log = LoggerFactory.getLogger(DirZipService.class);

    public static FlyResult zipDirWithVerify(File sourceDir, File destZipFile, String password,
                                             Predicate<File> refusePredicate) throws IOException {
        FlyResult backResult = FlyResult.of(true);
        //执行备份 backFile
        Zip4jTool.zipDir(destZipFile, sourceDir, password);
        backResult.append("executeBack success srcFile(" + sourceDir).append(") zipe to (")
                .append(destZipFile.getAbsolutePath()).append(")")
                .append(StringUtils.LF);

        //执行Test
        File zipFile = destZipFile;
        var unzipDirName = "unzipT4"
                + zipFile.getName().replaceAll("\\.", "_");
        var unzipDestDirPath = Path.of(zipFile.getParent(), unzipDirName);
        Zip4jTool.unZip(zipFile, unzipDestDirPath.toFile(), password);
        backResult.append("executeUnzip  (").append(zipFile.getAbsolutePath())
                .append(")  to (").append(unzipDestDirPath).append(")").append(StringUtils.LF);
        //对比
        var checkPath = unzipDestDirPath.resolve(sourceDir.getName());
        DirCompareUtil.CompareResult compareResult = DirCompareUtil.compareTwoFull(sourceDir.getAbsolutePath(), checkPath.toString(), null);
        if (compareResult.getDiff().size() > 0) {
            backResult.appendFail("diff:" + compareResult.getDiff().stream().map(file -> file.getAbsolutePath()));
        }

        return backResult;
    }


    /**
     * eg: /export/mecode/* --> /export/back/**.zip
     * dir:文件夹
     * pathStr：文件路径
     */
    public static final String DEFAULT_VERSIONDATA_PATH = "versionData";


    public File getDefaultSourceMd5File(File sourceDir) {
        File dirFile = Path.of(sourceDir.getAbsolutePath(), DEFAULT_VERSIONDATA_PATH).toFile();
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        return dirFile;
    }

}
