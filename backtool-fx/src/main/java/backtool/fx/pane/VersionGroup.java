package backtool.fx.pane;

import backtool.controler.FlyBackService;
import backtool.fx.compnant.FFX;
import backtool.service.DirVersionGen;
import backtool.service.FxConst;
import backtool.service.compare.DirCompareService;
import fly4j.common.util.DateUtil;
import fly4j.common.util.StringConst;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by qryc on 2021/10/1
 */
public class VersionGroup {

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class FileMd5Pane extends FFX.FGridPane {
        //选择对比文件夹
        private FFX.FileInputFx fileInputFx = new FFX.FileInputFx(FFX.inputLength);
        private FFX.DirInputFx dirInputFx = new FFX.DirInputFx(FFX.inputLength);

        public FileMd5Pane() {
            FFX.outputPane.rePrint("执行结果");
            this.addRow(fileInputFx);
            this.addRow(dirInputFx);
            this.addRow(new FFX.FButton("计算MD5资产清单", event -> calMd5()));
            this.addRow(FFX.outputPane);
        }

        private void calMd5() {


            FFX.outputPane.rePrint("执行结果");

            FFX.outputPane.rePrint(DirCompareService.calMd5(fileInputFx.getSelectFile(), dirInputFx.getSelectDir(), StringUtils.LF));
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DirVersionCheckPane extends FFX.FGridPane {
        private FFX.DirInputFx checkDirInput = new FFX.DirInputFx(FFX.inputLength);
        private TextArea textArea = new TextArea();

        public DirVersionCheckPane() {
            FFX.outputPane.rePrint("执行结果");
            textArea.setMaxHeight(100);
            this.addRow(new Label("选择要检查的文件夹:"), checkDirInput);
            this.addRow(new Label("手工输入资产清单:"), textArea);
            //        this.addRow(new Label("选择版本文件类型："), versionTypePane);
            this.addRow(new FFX.FButton("检查资产清单", event -> check()));
            this.addMergeRow(FFX.outputPane, 2);

        }

        //    private void loadPre() {
        //        LocalConfig localConfig = LocalConfigService.getLocalBackConfig();
        //        checkDirInput.setSelectDir(localConfig.getDirVersionCheckSourceDir());
        //        md5FileInput.setSelectFile(localConfig.getDirVersionCheckMd5File());
        //    }

        private void check() {
            if (StringUtils.isBlank(textArea.getText())) {
                FFX.outputPane.rePrint("请选输入清单");
                return;
            }
            if (StringUtils.isBlank(checkDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请选择检查文件");
                return;
            }
            FFX.outputPane.rePrint("已经开始检查摘要文件：" + DateUtil.getCurrDateStr());
            //不开启线程，上面的语句输出不会展现在页面
            new Thread(() -> {
                //            saveConfig();


                try {


                    String inputStr = textArea.getText();
                    String checkDir = checkDirInput.getSelectDir();
                    String resultBuilder = DirCompareService.checkDirChangeByStr(checkDir, inputStr, StringConst.LF);
                    FFX.outputPane.rePrint(resultBuilder);

                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }


            }).start();
        }

        //    private void saveConfig() {
        //        LocalConfigService.saveLocalBackConfig(localConfig -> {
        //            localConfig.setDirVersionCheckSourceDir(checkDirInput.getSelectDir());
        //            localConfig.setDirVersionCheckMd5File(md5FileInput.getSelectFile());
        //        });
        //    }


    }
}
