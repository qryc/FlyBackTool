package backtool.controler;

import backtool.service.FxConst;
import backtool.service.DirVersionGen;
import fly4j.common.file.FileUtil;
import backtool.service.compare.DirCompareUtil;
import fly4j.common.util.DateUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

/**
 * 应用服务
 * Created by qryc on 2021/9/27
 */
public class FlyBackService {





//    public static String genVersion(String dirVersionGenSourceDir, String dirVersionGenTargetDir, String versionFileName) {
//
//        var resultBuilder = new StringBuilder();
//        Path md5StorePath = Path.of(dirVersionGenTargetDir, versionFileName);
//        String versionStr = null;
//        try {
//            versionStr = DirVersionGen.saveDirVersionModel2File(dirVersionGenSourceDir, FxConst.fileAndDirPredicate, md5StorePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//        resultBuilder.append("genDirMd5VersionTag ok").append(StringUtils.LF);
//        resultBuilder.append("save to ").append(md5StorePath).append(StringUtils.LF);
//        resultBuilder.append(versionStr).append(StringUtils.LF);
//        return resultBuilder.toString();
//
//    }


}
