package backtool.fx.pane;

import backtool.fx.compnant.FFX;
import backtool.service.FxConst;
import backtool.service.compare.DirCompareUtil;
import fly4j.common.file.FileUtil;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DeleteEmptyDirPane extends VBox {
    //选择对比文件夹
    private FFX.DirInputFx compareDirSelectPan = new FFX.DirInputFx(FFX.inputLength);

    private FFX.FGridPane imgGridPane = new FFX.FGridPane();
    private VBox vBoxOutputPane = FFX.getVBox();
    private TextField countFiled = FFX.getNumberInputTextField(100);
    private Set<String> versionDirs = Set.of(".svn", ".git");

    public DeleteEmptyDirPane() {
        FFX.outputPane.rePrint("执行结果");
        imgGridPane.addRow(new Label("选择文件夹"), compareDirSelectPan);
        imgGridPane.addRow(new Label("显示操作文件数量"), countFiled);
        var buttons = FFX.getHBox(
                new FFX.FButton("查看空文件夹", event -> {
                    findEmptyDirs();
                }),
                new FFX.FButton("删除空文件夹", event -> {
                    clear();
                    if (StringUtils.isBlank(compareDirSelectPan.getSelectDir())) {
                        FFX.outputPane.rePrint("未选择");
                        return;
                    }
                    FFX.outputPane.rePrint("执行结果");
                    FileUtil.walkAllDirIgnoreHiddenDir(Path.of(compareDirSelectPan.getSelectDir()).toFile(), file -> {
                        if (FileUtil.isEmptyDirIgnoreSpecial(file)) {
                            FileUtil.deleteEmptyDirIgnoreSpecial(file);
                            FFX.outputPane.appendPrint(file.getAbsolutePath() + " deleted");
                        }
                    });
                    FFX.outputPane.appendPrint("执行完成");
                }),
                new FFX.FButton("清理版本文件" + versionDirs, event -> {
                    clear();
                    if (StringUtils.isBlank(compareDirSelectPan.getSelectDir())) {
                        FFX.outputPane.rePrint("未选择");
                        return;
                    }
                    FFX.outputPane.rePrint("执行结果");
                    Files.walk(Path.of(compareDirSelectPan.getSelectDir())).forEach(path -> {
                        File file = path.toFile();
                        if (file.isDirectory() && versionDirs.contains(file.getName())) {
                            try {
                                FileUtils.deleteDirectory(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            FFX.outputPane.appendPrint(file.getAbsolutePath() + " deleted");
                        }
                    });
                    FFX.outputPane.appendPrint("执行完成");
                }),
                new Label("不清除.开头的文件夹,等同空文件：" + FileUtil.emptyFiles));
        imgGridPane.addMergeRow(buttons, 2);

        imgGridPane.addMergeRow(FFX.outputPane, 2);
        imgGridPane.setAlignment(Pos.TOP_LEFT);
        this.getChildren().add(imgGridPane);
        this.getChildren().add(vBoxOutputPane);
        vBoxOutputPane.setPadding(new javafx.geometry.Insets(10, 5, 5, 10));

    }

    private void findEmptyDirs() throws IOException {
        clear();
        if (StringUtils.isBlank(compareDirSelectPan.getSelectDir())) {
            FFX.outputPane.rePrint("未选择");
            return;
        }
        FFX.outputPane.rePrint("执行结果");
        FileUtil.walkAllDirIgnoreHiddenDir(Path.of(compareDirSelectPan.getSelectDir()).toFile(), file -> {
            System.out.println("sdfsdf"+file.getAbsolutePath());
            if (FileUtil.isEmptyDirIgnoreSpecial(file)) {
                FFX.outputPane.appendPrint(file.getAbsolutePath());
                vBoxOutputPane.getChildren().add(FFX.getHBox(
                        new FFX.FButton("删除", e -> {
                            FileUtil.deleteEmptyDirIgnoreSpecial(file);
                            findEmptyDirs();
                        }),
                        new FFX.FButton("打开", e -> FFX.openFile(file)),
                        new Label(file.getAbsolutePath())
                ));
            }
        });
        FFX.outputPane.appendPrint("执行完成");
    }


    private void clear() {
        FFX.outputPane.rePrint("执行结果");
        vBoxOutputPane.getChildren().clear();
    }

    private static final Set<String> emptyFiles = new HashSet<>();

    static {
        emptyFiles.add(".DS_Store");
    }

    public static void main(String[] args) throws Exception {
        Files.walk(Path.of("/Volumes/GUANPIC")).forEach(path -> {
            File file = path.toFile();
            if (file.isDirectory()) {
                if (file.listFiles().length < 3 && !FileUtil.isEmptyDirIgnoreSpecial(file) && file.listFiles()[0].isFile()) {
                    System.out.println(file.getAbsolutePath() + " " + file.listFiles().length);

                    System.out.println("--------------" + file.listFiles()[0].getAbsolutePath());
                    if (file.listFiles().length > 1)
                        System.out.println("--------------" + file.listFiles()[1].getAbsolutePath());
                }
            }

        });
    }

}