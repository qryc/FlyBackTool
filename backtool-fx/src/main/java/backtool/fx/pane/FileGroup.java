package backtool.fx.pane;

import backtool.fx.compnant.FFX;
import backtool.service.FxConst;
import backtool.service.compare.DirCompareService;
import backtool.service.compare.DirCompareUtil;
import backtool.service.search.FileFinder;
import fly4j.common.file.FileUtil;
import fly4j.common.util.StringConst;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qryc on 2021/10/1
 */
public class FileGroup {


    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DirDoubleCheckPane extends VBox {
        //选择对比文件夹
        private FFX.DirInputFx compareDirSelectPan = new FFX.DirInputFx(FFX.inputLength);

        private FFX.FGridPane imgGridPane = new FFX.FGridPane();
        private VBox vBoxOutputPane = FFX.getVBox();
        private TextField countFiled = FFX.getNumberInputTextField(100);

        public DirDoubleCheckPane() {
            FFX.outputPane.rePrint("执行结果");
            imgGridPane.addRow(new Label("选择文件夹"), compareDirSelectPan);
            imgGridPane.addRow(new Label("显示操作文件数量"), countFiled);
            var buttons = FFX.getHBox(
                    new FFX.FButton("查找重复文件", event -> find())
//                    new FFX.FButton("查看空文件夹", event -> {
//                        findEmptyDirs();
//                    })
            );
            imgGridPane.addMergeRow(buttons, 2);

            imgGridPane.addMergeRow(FFX.outputPane, 2);
            imgGridPane.setAlignment(Pos.TOP_LEFT);
            this.getChildren().add(imgGridPane);
            this.getChildren().add(vBoxOutputPane);
            vBoxOutputPane.setPadding(new javafx.geometry.Insets(10, 5, 5, 10));

        }


        private void find() {
            if (StringUtils.isBlank(compareDirSelectPan.getSelectDir())) {
                FFX.outputPane.rePrint("未选择");
                return;
            }
            if (!StringUtils.isNumeric(countFiled.getText())) {
                FFX.outputPane.rePrint("文件数量必须为数字");
                return;
            }
            int max = Integer.parseInt(countFiled.getText());
            clear();
            int baseIndex = compareDirSelectPan.getSelectDir().length() - 1;
            List<DirCompareUtil.OneSameObj> oneSameObjs = DirCompareUtil.compareOneDir(compareDirSelectPan.getSelectDir(), FxConst.fileAndDirPredicate);
            AtomicInteger count = new AtomicInteger(0);
            oneSameObjs.forEach((sameObj) -> {

                if (count.incrementAndGet() > max) {
                    return;
                }
                var files = sameObj.sames();
                HBox hBoxImage = FFX.getHBox();
                for (int i = 0; i < files.size(); i++) {
                    File file = files.get(i);
                    File repeatFile = i == 0 ? files.get(1) : files.get(0);
                    vBoxOutputPane.getChildren().add(FFX.getHBox(
                            new FFX.FButton("删除", event -> delete(file, repeatFile, event)),
                            new FFX.FButton("打开", event -> FFX.openFile(file)),
                            new Label(file.getAbsolutePath().substring(baseIndex + 2))
                    ));

                    if (FileUtil.isImg(file)) {
                        hBoxImage.getChildren().add(FFX.imageView(file, FFX.image_width, FFX.image_height));
                    }

                }
                vBoxOutputPane.getChildren().add(hBoxImage);
                vBoxOutputPane.getChildren().add(new Label(FxConst.SPLITE_LINE_100));
            });
            //构造结果
            StringBuilder resultBuilder = new StringBuilder();
            if (oneSameObjs.size() == 0) {
                resultBuilder.append("没有重复文件").append(StringUtils.LF);
            } else {
                resultBuilder.append("重复文件数量：").append(oneSameObjs.size()).append(StringUtils.LF);
            }
            oneSameObjs.forEach(sameObj -> {
                var files = sameObj.sames();
                resultBuilder.append("重复次数：").append(files.size()).append(StringUtils.LF);
                files.forEach(file -> resultBuilder.append(file.getAbsolutePath()).append(StringUtils.LF));
                resultBuilder.append(StringUtils.LF);
            });
            FFX.outputPane.appendPrint(resultBuilder.toString());
        }

        private void clear() {
            FFX.outputPane.rePrint("执行结果");
            vBoxOutputPane.getChildren().clear();
        }


        private void delete(File file, File repeatFile, ActionEvent event) {
            clear();
            ((FFX.FButton) event.getSource()).setDisable(true);
            FileUtil.deleteRepeatFile(file, repeatFile);
            find();
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DirDoubleKillPane extends VBox {
        private FFX.DirInputFx standardDirInput = new FFX.DirInputFx(FFX.inputLength);
        private FFX.DirInputFx doubleKillDirInput = new FFX.DirInputFx(FFX.inputLength);
        private FFX.FGridPane imgGridPane = new FFX.FGridPane();
        private VBox vBoxOutputPane = FFX.getVBox();

        public DirDoubleKillPane() {
            FFX.outputPane.rePrint("执行结果");
            imgGridPane.addRow(new Label("选择标准参考文件夹："), standardDirInput);
            imgGridPane.addRow(new Label("选择删除重复文件文件夹："), doubleKillDirInput);
            imgGridPane.addRow(new FFX.FButton("查看重复文件", event -> dirCompare()), new FFX.FButton("删除重复文件", event -> doubleKill()));
            imgGridPane.addMergeRow(FFX.outputPane, 2);
            vBoxOutputPane.setPadding(new javafx.geometry.Insets(10, 5, 5, 10));
            this.getChildren().add(imgGridPane);
            this.getChildren().add(vBoxOutputPane);
        }

        private void delete(File file, File repeatFile, ActionEvent event) {
            clear();
            ((FFX.FButton) event.getSource()).setDisable(true);
            FileUtil.deleteRepeatFile(file, repeatFile);
            dirCompare();
        }

        private void clear() {
            FFX.outputPane.rePrint("执行结果");
            vBoxOutputPane.getChildren().clear();
        }

        private void dirCompare() {
            if (StringUtils.isBlank(standardDirInput.getSelectDir()) || StringUtils.isBlank(doubleKillDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            clear();
            StringBuilder resultBuilder = compareTwoSame(standardDirInput.getSelectDir(), doubleKillDirInput.getSelectDir());
            FFX.outputPane.rePrint(resultBuilder.toString());
        }

        private StringBuilder compareTwoSame(String leftDirStr, String rightDirStr) {
            vBoxOutputPane.getChildren().clear();
            StringBuilder resultBuilder = new StringBuilder();
            List<DirCompareUtil.LeftRightSameObj> leftRightSameObjs = DirCompareUtil.compareTwoSame(leftDirStr,
                    rightDirStr, null);
            //输出结果
            resultBuilder.append("将要删除的重复文件:").append(leftRightSameObjs.size()).append(StringUtils.LF);
            leftRightSameObjs.forEach(sameObj -> {
                sameObj.lefts().forEach(repeatFile -> {
                    resultBuilder.append("existFile:").append(repeatFile.getAbsolutePath()).append(StringUtils.LF);
                });
                sameObj.rights().forEach(deleteFile -> {
                    resultBuilder.append("deleteFile:").append(deleteFile.getAbsolutePath()).append(StringUtils.LF);
                });

            });

            leftRightSameObjs.forEach(sameObj -> {
                HBox hBoxImage = FFX.getHBox();
                sameObj.lefts().forEach(repeatFile -> {
                    HBox hBox = FFX.getHBox();
                    hBox.getChildren().add(new FFX.FButton("打开", event -> FFX.openFile(repeatFile.getParentFile().getAbsolutePath())));
                    hBox.getChildren().add(new Label("保留：" + repeatFile.getAbsolutePath()));
                    vBoxOutputPane.getChildren().add(hBox);
                    if (FileUtil.isImg(repeatFile)) {
                        hBoxImage.getChildren().add(FFX.imageView(repeatFile, 200, 200));
                    }
                });
                sameObj.rights().forEach(deleteFile -> {
                    HBox hBox = FFX.getHBox();
                    hBox.getChildren().add(new FFX.FButton("打开", event -> FFX.openFile(deleteFile.getParentFile().getAbsolutePath())));
                    hBox.getChildren().add(new Label("删除：" + deleteFile.getAbsolutePath()));
                    hBox.getChildren().add(new FFX.FButton("删除", event -> delete(deleteFile, sameObj.lefts().get(0), event)));
                    vBoxOutputPane.getChildren().add(hBox);
                    if (FileUtil.isImg(deleteFile)) {
                        hBoxImage.getChildren().add(FFX.imageView(deleteFile, 200, 200));
                    }
                });
                if (hBoxImage.getChildren().size() > 0) {
                    vBoxOutputPane.getChildren().add(hBoxImage);
                    vBoxOutputPane.getChildren().add(new Label(FxConst.SPLITE_LINE_100));
                }

            });


            return resultBuilder;
        }

        private void doubleKill() {
            if (StringUtils.isBlank(standardDirInput.getSelectDir()) || StringUtils.isBlank(doubleKillDirInput.getSelectDir())) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            StringBuilder resultBuilder = doubleKillTowSame(standardDirInput.getSelectDir(), doubleKillDirInput.getSelectDir());
            //输出结果
            FFX.outputPane.rePrint(resultBuilder.toString());
        }

        private StringBuilder doubleKillTowSame(String leftDirStr, String rightDirStr) {
            StringBuilder resultBuilder = new StringBuilder();
            List<DirCompareUtil.LeftRightSameObj> leftRightSameObjs = DirCompareUtil.compareTwoSame(leftDirStr,
                    rightDirStr, null);
            resultBuilder.append("开始删除文件:").append(leftRightSameObjs.size()).append(StringUtils.LF);
            leftRightSameObjs.forEach(sameObj -> {
                sameObj.rights().forEach(deleteFile -> {
                    resultBuilder.append("deleteFile:").append(deleteFile.getAbsolutePath()).append(StringUtils.LF);
                    resultBuilder.append("existFile:").append(sameObj.lefts().get(0).getAbsolutePath()).append(StringUtils.LF);
                    FileUtil.deleteRepeatFile(deleteFile, sameObj.lefts().get(0));
                });
            });
            return resultBuilder;
        }


    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DirComparePane extends FFX.FGridPane {
        //选择对比文件夹
        private FFX.DirInputFx leftDirInput = new FFX.DirInputFx(FFX.inputLength);
        private FFX.DirInputFx rightDirInput = new FFX.DirInputFx(FFX.inputLength);

        public DirComparePane() {
            FFX.outputPane.rePrint("执行结果");
            this.addRow(new Label("左边："), leftDirInput);
            this.addRow(new Label("右边："), rightDirInput);
            this.addMergeRow(FFX.getHBox(
                    new FFX.FButton("检文件夹差异(显示相同)", event -> dirCompareAll()),
                    new FFX.FButton("检文件夹差异(隐藏相同)", event -> dirCompareHiddenSame()),
                    new FFX.FButton("检是左边否全包含右边", event -> dirCompareLeftContainRight()),
                    new Label("过滤：" + FxConst.fileAndDirPredicate)
            ), 2);
            this.addMergeRow(FFX.outputPane, 2);
        }

        private void dirCompareAll() {
            if (checkParamBlank()) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            FFX.outputPane.rePrint("执行结果");
            FFX.outputPane.appendPrint(DirCompareService.dirCompareAll(leftDirInput.getSelectDir(), rightDirInput.getSelectDir(), StringConst.LF));
        }


        private boolean checkParamBlank() {
            return StringUtils.isBlank(leftDirInput.getSelectDir()) || StringUtils.isBlank(rightDirInput.getSelectDir());
        }

        private void dirCompareHiddenSame() {
            if (checkParamBlank()) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            FFX.outputPane.rePrint("执行结果");

            StringBuilder resultBuilder = new StringBuilder();
            DirCompareUtil.CompareResult compareResult = DirCompareUtil.compareTwoFull(leftDirInput.getSelectDir(), rightDirInput.getSelectDir(), FxConst.fileAndDirPredicate);
            resultBuilder.append("左边独有文件：").append(StringConst.LF);
            compareResult.leftAloneFiles.forEach(file -> resultBuilder.append(file.getAbsolutePath()).append(StringConst.LF));

            resultBuilder.append("右边独有文件：").append(StringConst.LF);
            compareResult.rightAloneFiles.forEach(file -> resultBuilder.append(file.getAbsolutePath()).append(StringConst.LF));

            FFX.outputPane.appendPrint(resultBuilder.toString());
        }

        private void dirCompareLeftContainRight() {
            if (checkParamBlank()) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            FFX.outputPane.rePrint("执行结果");

            StringBuilder resultBuilder = new StringBuilder();
            DirCompareUtil.CompareResult compareResult = DirCompareUtil.compareTwoFull(leftDirInput.getSelectDir(), rightDirInput.getSelectDir(), FxConst.fileAndDirPredicate);

            resultBuilder.append("右边独有文件：").append(StringConst.LF);
            compareResult.rightAloneFiles.forEach(file -> resultBuilder.append(file.getAbsolutePath()).append(StringConst.LF));

            //输出结果
            FFX.outputPane.appendPrint(resultBuilder.toString());
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class FindFilePane extends FFX.FGridPane {
        private FFX.DirInputFx findBaseDirSelectPan = new FFX.DirInputFx(FFX.inputLength);

        private TextField namesFiled = new TextField();

        public FindFilePane() {
            FFX.outputPane.rePrint("执行结果");
            namesFiled.setText("target,classes");
            namesFiled.setMaxWidth(FFX.inputLength);
            this.addRow(new Label("查找根目录"), findBaseDirSelectPan);
            this.addRow(new Label("查找文件名称,号分割"), namesFiled);
            this.addRow(new FFX.FButton("查找文件", event -> findFile()));
            this.addMergeRow(FFX.outputPane, 2);
        }

        private void findFile() {
            if (StringUtils.isBlank(findBaseDirSelectPan.getSelectDir())) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            String checkDirStr = findBaseDirSelectPan.getSelectDir();
            String findName = namesFiled.getText();
            String result = null;
            try {
                result = FileFinder.findFile(checkDirStr, findName);
            } catch (IOException e) {
                FFX.outputPane.rePrint(e.getMessage());
                e.printStackTrace();
            }
            FFX.outputPane.rePrint(result);
        }


    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class BigFilePane extends FFX.FGridPane {
        private FFX.DirInputFx checkDirSelectPan = new FFX.DirInputFx(FFX.inputLength);

        private TextField countFiled = FFX.getNumberInputTextField(100);

        public BigFilePane() {
            FFX.outputPane.rePrint("执行结果");
            this.addRow(new Label("查找根目录"), checkDirSelectPan);
            this.addRow(new Label("显示文件数量"), countFiled);
            this.addRow(new FFX.FButton("检查大文件", event -> findBigFile()));
            this.addMergeRow(FFX.outputPane, 2);
        }

        private void findBigFile() {
            if (StringUtils.isBlank(checkDirSelectPan.getSelectDir())) {
                FFX.outputPane.rePrint("请选择");
                return;
            }
            if (!StringUtils.isNumeric(countFiled.getText())) {
                FFX.outputPane.rePrint("文件数量必须为数字");
                return;
            }
            FFX.outputPane.rePrint(FileFinder.maxFile(checkDirSelectPan.getSelectDir(), Integer.parseInt(countFiled.getText())));
        }

    }
}
