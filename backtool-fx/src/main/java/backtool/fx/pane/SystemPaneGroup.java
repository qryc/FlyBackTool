package backtool.fx.pane;

import backtool.fx.compnant.FFX;
import backtool.service.LocalConfigService;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Created by qryc on 2021/10/1
 */
public class SystemPaneGroup {
    public static class LogPane extends FFX.FGridPane {
        //选择对比文件夹
        public LogPane() {
            this.addRow(new Label("执行日志"));
            this.addRow(FFX.logPane);
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class LocalConfigBackPane extends FFX.FGridPane {

        private FFX.RadioPane<Boolean> beteOpenInput = new FFX.RadioPane<>("开启实验室功能", "关闭", false, "开启", true);

        public LocalConfigBackPane() {
            try {
                var config = LocalConfigService.getLocalBackConfig();
                beteOpenInput.select(config.isBetaOpen());
                FFX.outputPane.rePrint("执行结果");
                this.addRow(new Label("配置文件："), new Label(LocalConfigService.configFilePath.toString()));
                this.addMergeRow(beteOpenInput, 2);
                this.addMergeRow(FFX.getHBox(new FFX.FButton("保存配置", event -> saveConfig())
                        , new FFX.FButton("清空配置", event -> resetConfig())
                        , new FFX.FButton("打开配置文件", event -> FFX.openFile(LocalConfigService.configDirPath.toString()))), 2);
                this.addMergeRow(FFX.outputPane, 2);
            } catch (Exception e) {
                FFX.outputPane.rePrint(e.getMessage());
            }

        }

        private void resetConfig() {
            try {
                LocalConfigService.resetLocalConfig();
            } catch (IOException e) {
                e.printStackTrace();
                FFX.outputPane.rePrint(e.getMessage());
            }
        }

        private void saveConfig() throws IOException {
            boolean betaOpen = beteOpenInput.getSelectValue();
            LocalConfigService.saveLocalBackConfig(localConfig -> localConfig.setBetaOpen(betaOpen));
            FFX.alert("配置已经保存成功");
        }

    }
}
