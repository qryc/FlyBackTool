package backtool.service.compare;

import backtool.service.DirVersionGen;
import backtool.service.FxConst;
import fly4j.common.util.StringConst;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DirCompareService {
    public static String dirCompareAll(String left, String right, String split) {
        if (StringUtils.isBlank(left) || StringUtils.isBlank(right)) {
            return "请选择";
        }

        StringBuilder resultBuilder = new StringBuilder("执行结果");
        resultBuilder.append(split);
        DirCompareUtil.CompareResult compareResult = DirCompareUtil.compareTwoFull(left, right, FxConst.fileAndDirPredicate);
        resultBuilder.append("左边独有文件：").append(split);
        compareResult.leftAloneFiles.forEach(file -> resultBuilder.append(file.getAbsolutePath()).append(split));

        resultBuilder.append("右边独有文件：").append(split);
        compareResult.rightAloneFiles.forEach(file -> resultBuilder.append(file.getAbsolutePath()).append(split));

        resultBuilder.append("相同文件：").append(split);
        compareResult.leftRightSameObjs.forEach(sameObj -> resultBuilder.append(sameObj).append(split));
        resultBuilder.append(resultBuilder.toString());
        return resultBuilder.toString();
    }

    public static String calMd5(String fileStr, String dirStr, String split) {
        if (StringUtils.isBlank(fileStr) && StringUtils.isBlank(dirStr)) {
            return "未选择";
        }
        //构造结果
        var resultBuilder = new StringBuilder();

        //文件
        if (StringUtils.isNotBlank(fileStr)) {
            var file = new File(fileStr);
            resultBuilder.append(DirVersionGen.FileDigestModel.of(file)).append(split);
        }
        //文件夹
        if (StringUtils.isNotBlank(dirStr)) {
            List<DirVersionGen.FileDigestModel> list = DirVersionGen.genDirVersionModels(dirStr, FxConst.fileAndDirPredicate);
            resultBuilder.append("//执行清单:").append(list.size()).append(split);
            //文件版本信息
            list.forEach(model -> {
                //生成md5
                resultBuilder.append(model).append(split);
            });
        }
        return resultBuilder.toString();
    }

    public static String checkDirChangeByStr(String checkDir, String inputStr, String split) throws IOException {
        if (StringUtils.isBlank(checkDir) || StringUtils.isBlank(inputStr)) {
            return "param is null";
        }
        DirVersionGen.VersionCheckResult compareResult = null;
        if (StringUtils.isNoneBlank(inputStr)) {
            compareResult = DirVersionGen.checkDirChangeByStr(checkDir, inputStr.lines().collect(Collectors.toList()), FxConst.fileAndDirPredicate);
        }

        var resultBuilder = new StringBuilder();
        resultBuilder.append(StringConst.LF);
        if (compareResult.deleteFiles().size() == 0) {
            resultBuilder.append("没有丢失或修改的资产文件,资产总数:").append(compareResult.okFiles().size()).append(StringConst.LF);
        } else {
            resultBuilder.append("丢失或修改的资产文件：").append(compareResult.deleteFiles().size()).append(StringConst.LF);
            compareResult.deleteFiles().forEach(fileStr -> resultBuilder.append(fileStr).append(StringConst.LF));
            resultBuilder.append(StringConst.LF);
        }

        if (compareResult.okFiles().size() > 0) {
            resultBuilder.append("保留的正确资产文件：").append(compareResult.okFiles().size()).append(StringConst.LF);
            compareResult.okFiles().forEach(fileStr -> resultBuilder.append(fileStr).append(StringConst.LF));
        }

        resultBuilder.append(StringConst.LF);
        return resultBuilder.toString();

    }
}
