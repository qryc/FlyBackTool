package backtool.fx.pane;

import backtool.common.NetFileUtil;
import backtool.domain.LocalConfig;
import backtool.fx.common.ImageUtil;
import backtool.fx.compnant.FFX;
import backtool.service.FxConst;
import backtool.service.LocalConfigService;
import backtool.service.zip.DirZipService;
import fly4j.common.cache.FlyCache;
import fly4j.common.cache.impl.FlyCacheJVM;
import fly4j.common.crypto.XorUtil;
import fly4j.common.domain.FlyResult;
import fly4j.common.file.zip.Zip4jTool;
import fly4j.common.os.OsUtil;
import fly4j.common.util.DateUtil;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by qryc on 2021/10/1
 */
public class BetaGroup {
    /**
     * Created by qryc on 2020/3/7.
     */
    public static class ZipPane extends FFX.FGridPane {
        private FFX.DirInputFx fromDirInput = new FFX.DirInputFx(FFX.inputLength);
        private FFX.DirInputFx toDirInput = new FFX.DirInputFx(FFX.inputLength);
        private PasswordField passwordInput = new PasswordField();
        private TextField afterCopySleppTimeInput = new TextField();

        public ZipPane() {
            FFX.outputPane.rePrint("执行结果");
            passwordInput.setMaxWidth(FFX.shortInputWidth);
            afterCopySleppTimeInput.setMaxWidth(FFX.shortInputWidth);
            this.addRow(new Label("要压缩的文件夹："), fromDirInput);
            this.addRow(new Label("压缩文件存放目录："), toDirInput);
            this.addRow(new Label("密码："), passwordInput);
            this.addRow(new Label("拷贝文件后休息时间 秒"), afterCopySleppTimeInput);
            this.addMergeRow(FFX.getHBox(new FFX.FButton("执行资料打包", event -> zip()),
                    new FFX.FButton("打开压缩目录", event -> openFile())), 2);
            this.addMergeRow(FFX.outputPane, 2);
        }

        private void openFile() {
            if (StringUtils.isBlank(toDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            FFX.openFile(toDirInput.getSelectDir());
        }

        private void zip() throws IOException {
            if (StringUtils.isBlank(fromDirInput.getSelectDir()) || StringUtils.isBlank(toDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            FFX.outputPane.rePrint("已经开始打包：" + DateUtil.getCurrDateStr());

            //重置配置文件
            var name = OsUtil.getSimpleOsName()
                    + DateUtil.getHourStr4Name(new Date())
                    + Path.of(fromDirInput.getSelectDir()).getFileName().toString().replace('.', '_')
                    + ".zip";
            var destZipFilePath = Path.of(toDirInput.getSelectDir(), name);
            FlyResult flyResult = DirZipService.zipDirWithVerify(new File(fromDirInput.getSelectDir()), destZipFilePath.toFile(), passwordInput.getText(), FxConst.fileAndDirPredicate);

            FFX.outputPane.rePrint(flyResult.toString());
        }

    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class UnzipPane extends FFX.FGridPane {

        private FFX.FileInputFx unzipFile = new FFX.FileInputFx(FFX.inputLength);
        private FFX.DirInputFx destDir = new FFX.DirInputFx(FFX.inputLength);
        private PasswordField password = new PasswordField();


        public UnzipPane() {
            FFX.outputPane.rePrint("执行结果");
            password.setMaxWidth(FFX.shortInputWidth);
            this.addRow(new Label("选择解压文件:"), unzipFile);
            this.addRow(new Label("解压到:"), destDir);
            this.addRow(new Label("解压密码:"), password);
            var buttons = FFX.getHBox(new FFX.FButton("解压缩", event -> unzip()),
                    new FFX.FButton("打开压缩目录", event -> openFile()));
            this.addMergeRow(buttons, 2);
            this.addMergeRow(FFX.outputPane, 2);
        }

        private void openFile() {
            if (StringUtils.isBlank(destDir.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            FFX.openFile(destDir.getSelectDir());
        }

        private void unzip() {
            if (StringUtils.isBlank(unzipFile.getSelectFile()) || StringUtils.isBlank(destDir.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            Zip4jTool.unZip(new File(unzipFile.getSelectFile()), new File(destDir.getSelectDir()), password.getText());
            FFX.outputPane.rePrint(DateUtil.getCurrDateStr() + "解压成功");
        }

    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DownloadPane extends FFX.FGridPane {

        private TextField remoteUrlInput = new TextField();
        private FFX.DirInputFx localDirInput = new FFX.DirInputFx();

        public DownloadPane() {
            try {
                remoteUrlInput.setPrefWidth(FFX.inputLength);
                LocalConfig.DownConfig downConfig = LocalConfigService.getLocalBackConfig().getDownConfig();
                if (null != downConfig) {
                    this.remoteUrlInput.setText(downConfig.remoteUrl());
                    this.localDirInput.setSelectDir(downConfig.localDir());
                }
                this.addRow(new Label("远端同步设置:"), remoteUrlInput);
                this.addRow(new Label("本地Dri选择:"), localDirInput);
                this.addRow(new FFX.FButton("开启同步", event -> openSync()));
                this.addMergeRow(FFX.outputPane, 2);

            } catch (Exception e) {
                FFX.outputPane.rePrint(e.getMessage());
            }


        }


        private void openSync() throws IOException {
            if (StringUtils.isBlank(remoteUrlInput.getText()) || StringUtils.isBlank(localDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            var downConfig = new LocalConfig.DownConfig(remoteUrlInput.getText(), localDirInput.getSelectDir());
            new Thread(() -> DownLoadService.downFile(downConfig)).start();
            LocalConfigService.saveLocalBackConfig(localConfig -> {
                localConfig.setDownConfig(downConfig);
            });

        }


        public static class DownLoadService {
            private static FlyCache flyCache = new FlyCacheJVM();
            private static volatile boolean run = false;

            private static void downFile(LocalConfig.DownConfig downConfig) {
                if (run) {
                    return;
                }
                run = true;

                FFX.outputPane.appendPrint(DateUtil.getCurrDateStr() + "已经开启定时下载服务");
                String preFileName = null;
                while (true) {
                    //防止侦测频繁,如果两小时内侦测过，不会侦测
                    if (isLockDown()) {
                        sleep4FiveSeconds();
                        continue;
                    }

                    //如果和上次备份成功名称一样，继续等待
                    String fileName = NetFileUtil.getDownloadFileName(downConfig.remoteUrl());
                    if (StringUtils.isNotBlank(preFileName) && preFileName.equals(fileName)) {
                        lockDown4Hours();
                        sleep4FiveSeconds();
                        continue;
                    }

                    FFX.outputPane.appendPrint(DateUtil.getCurrDateStr() + " " + fileName + " 开始下载...");
                    boolean ok = NetFileUtil.downloadHttpUrl(downConfig.remoteUrl(), FilenameUtils.concat(downConfig.localDir(), fileName));
                    if (ok) {
                        FFX.outputPane.appendPrint(DateUtil.getCurrDateStr() + " " + fileName + " 下载完成");
                        preFileName = fileName;
                        lockDown4Hours();
                    } else {
                        FFX.outputPane.appendPrint(DateUtil.getCurrDateStr() + " " + fileName + " 下载失败Error");
                    }
                    sleep4FiveSeconds();

                }
            }

            private static boolean isLockDown() {
                return null != flyCache.get("backOk");
            }

            private static void lockDown4Hours() {
                flyCache.put("backOk", System.currentTimeMillis(), TimeUnit.HOURS.toMillis(2));
            }

            private static void sleep4FiveSeconds() {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class CryptPane extends FFX.FGridPane {
        private FFX.DirInputFx sourceDirInput = new FFX.DirInputFx(FFX.inputLength);
        private FFX.DirInputFx targetDirInput = new FFX.DirInputFx(FFX.inputLength);

        public CryptPane() {
            this.addRow(new Label("加密源目录:"), sourceDirInput);
            this.addRow(new Label("目标目录:"), targetDirInput);
            this.addRow(new FFX.FButton("加密", event -> crypt()));
            this.addMergeRow(FFX.outputPane, 2);

        }

        private void crypt() throws IOException {
            if (StringUtils.isBlank(sourceDirInput.getSelectDir()) || StringUtils.isBlank(targetDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            FFX.outputPane.rePrint("已经开始加密：" + DateUtil.getCurrDateStr());
            var resultBuilder = new StringBuilder();
            File sourceDir = new File(sourceDirInput.getSelectDir());
            for (File file : sourceDir.listFiles()) {
                Path targetPath = Path.of(targetDirInput.getSelectDir(), file.getName());
                XorUtil.xorFile2File(file.toPath(), targetPath, 123);
                resultBuilder.append(file.getAbsolutePath()).append(" 2 ").append(targetPath.toString()).append(StringUtils.LF);

            }
            FFX.outputPane.rePrint(resultBuilder.toString());

        }

    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DecryptPane extends FFX.FGridPane {
        private FFX.DirInputFx sourceDirInput = new FFX.DirInputFx(FFX.inputLength);
        private FFX.DirInputFx targetDirInput = new FFX.DirInputFx(FFX.inputLength);

        public DecryptPane() {
            this.addRow(new Label("加密源目录:"), sourceDirInput);
            this.addRow(new Label("目标目录:"), targetDirInput);
            this.addRow(new FFX.FButton("解密", event -> decrypt()));
            this.addMergeRow(FFX.outputPane, 2);

        }

        private void decrypt() throws IOException {
            if (StringUtils.isBlank(sourceDirInput.getSelectDir()) || StringUtils.isBlank(targetDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请输入");
                return;
            }
            FFX.outputPane.rePrint("已经开始检查摘要文件：" + DateUtil.getCurrDateStr());
            var resultBuilder = new StringBuilder();
            File sourceDir = new File(sourceDirInput.getSelectDir());
            for (File file : sourceDir.listFiles()) {
                Path targetPath = Path.of(targetDirInput.getSelectDir(), file.getName());
                XorUtil.xorFile2File(file.toPath(), targetPath, 123);
                resultBuilder.append(file.getAbsolutePath()).append(" 2 ").append(targetPath.toString()).append(StringUtils.LF);

            }
            FFX.outputPane.rePrint(resultBuilder.toString());

        }


    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DeryptImagPane extends FFX.FGridPane {
        private FFX.DirInputFx sourceDirInput = new FFX.DirInputFx(FFX.inputLength);

        public DeryptImagPane() {

            this.addRow(new Label("密文目录:"), sourceDirInput);
            this.addRow(new FFX.FButton("查看图片", event -> check()));
            this.addMergeRow(FFX.outputPane, 2);

        }

        private void check() {
            FFX.outputPane.rePrint("已经开始检查摘要文件：" + DateUtil.getCurrDateStr());
            var resultBuilder = new StringBuilder();
            File sourceDir = new File(sourceDirInput.getSelectDir());
            for (File file : sourceDir.listFiles()) {
                byte[] bytes = XorUtil.xorFile2Byte(file.toPath(), 123);
                this.addRow(ImageUtil.getImageView(bytes));
                resultBuilder.append(file.getAbsolutePath()).append(" 2 ").append(StringUtils.LF);
            }
            FFX.outputPane.rePrint(resultBuilder.toString());
        }

    }
}
