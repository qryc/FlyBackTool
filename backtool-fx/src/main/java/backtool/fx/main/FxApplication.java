package backtool.fx.main;

import backtool.fx.pane.*;
import backtool.service.LocalConfigService;
import fly4j.common.util.StringConst;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import backtool.service.FxConst;
import backtool.fx.compnant.FFX;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created by qryc on 2020/3/7.
 */
public class FxApplication extends Application {

    //整体面板
    private static BorderPane rootPane;


    public FxApplication() {
        FxConst.initBean();
    }

    @Override
    public void start(Stage primaryStage) {
        rootPane = new BorderPane();
        //添加按钮组和展示结果
        rootPane.setStyle("-fx-font-size:  16px");

        var topMenuBar = new PbMenuBar();
        var leftBar = buildToolBar(topMenuBar);
        rootPane.setTop(topMenuBar);
        rootPane.setLeft(leftBar);
        this.setCenter(new VersionGroup.DirVersionCheckPane());
        rootPane.setPadding(new Insets(0, 0, 0, 5));
        var scene = new Scene(rootPane, FFX.width, FFX.height);
        primaryStage.setMaximized(false);
        primaryStage.setTitle("文件备份小助手" + FxConst.version);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void setCenter(Node content) {
        FxApplication.rootPane.setCenter(new ScrollPane(content));
//        FxApplication.rootPane.setCenter(content);
    }


    protected class PbMenuBar extends MenuBar {
        public PbMenuBar() {
            this.setStyle("-fx-font-size:  20px");

            var repeatFileMenu = new Menu("删除重复文件");
            repeatFileMenu.getItems().addAll(
                    new MyMenuItem("清空空文件夹", event -> FxApplication.setCenter(new DeleteEmptyDirPane()), true),
                    new MyMenuItem("对比两个文件夹差异", event -> FxApplication.setCenter(new FileGroup.DirComparePane()), true),
                    new MyMenuItem("删除文件夹中的重复文件", event -> FxApplication.setCenter(new FileGroup.DirDoubleCheckPane()), true),
                    new MyMenuItem("删除两个文件夹的重复文件", event -> FxApplication.setCenter(new FileGroup.DirDoubleKillPane()), true)

            );

            var fileMenu = new Menu("资产清单");
            fileMenu.getItems().addAll(
                    new MyMenuItem("输出资产清单到页面", event -> FxApplication.setCenter(new VersionGroup.FileMd5Pane()), true),
                    new MyMenuItem("检查资产清单", event -> FxApplication.setCenter(new VersionGroup.DirVersionCheckPane()), true)
            );


            var searchMenu = new Menu("文件搜索");
            searchMenu.getItems().addAll(
                    new MyMenuItem("搜索大文件", event -> FxApplication.setCenter(new FileGroup.BigFilePane()), true),
                    new MyMenuItem("搜索文件名", event -> FxApplication.setCenter(new FileGroup.FindFilePane()), true)
            );


            /**
             * beta-文件压缩菜单
             */
            var betaMenu = new Menu("实验室功能");
            betaMenu.getItems().addAll(
                    new MyMenuItem("资料打包", event -> FxApplication.setCenter(new BetaGroup.ZipPane()), false),
                    new MyMenuItem("资料解包", event -> FxApplication.setCenter(new BetaGroup.UnzipPane()), false),
                    new MyMenuItem("定时下载", event -> FxApplication.setCenter(new BetaGroup.DownloadPane()), false),
                    new MyMenuItem("加密", event -> FxApplication.setCenter(new BetaGroup.CryptPane()), false),
                    new MyMenuItem("解密", event -> FxApplication.setCenter(new BetaGroup.DecryptPane()), false),
                    new MyMenuItem("查看加密图片", event -> FxApplication.setCenter(new BetaGroup.DeryptImagPane()), false)
            );


            /**
             * 系统设置菜单
             */
            //有状态外部创建，防止影响上次执行消息

            var systemMenu = new Menu("系统设置");
            systemMenu.getItems().addAll(
                    new MyMenuItem("配置文件", event -> FxApplication.setCenter(new SystemPaneGroup.LocalConfigBackPane()), false),
                    new MyMenuItem("查看系统版本", event -> viewSystemInfo(), false),
                    new MyMenuItem("查看系统日志", event -> FxApplication.setCenter(new SystemPaneGroup.LogPane()), false),
                    new SeparatorMenuItem(),
                    new MyMenuItem("Exit", actionEvent -> Platform.exit(), false));


            this.getMenus().addAll(repeatFileMenu, fileMenu, searchMenu, systemMenu);
            try {
                if (LocalConfigService.getLocalBackConfig().isBetaOpen()) {
                    this.getMenus().add(betaMenu);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        private void viewSystemInfo() {
            var info = new StringBuilder();
            info.append("version:").append(FxConst.version).append(StringConst.LF)
                    .append("author:qryc").append(StringConst.LF);
//            info.append("user.home").append(System.getProperty("user.home")).append(StringConst.LF);
//            info.append("user.dir").append(System.getProperty("user.dir")).append(StringConst.LF);
            FFX.alert(info.toString());
        }


        protected class MyMenuItem extends MenuItem {
            //为了生成常用按钮
            protected Consumer<ActionEvent> consumer;
            protected boolean addToolBar;

            public MyMenuItem(String text, Consumer<ActionEvent> consumer) {
                super(text);
                this.setText(text);
                this.consumer = consumer;
                this.setOnAction(event -> {
                    try {
                        consumer.accept(event);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(FxConst.exceptionLog).error("Exception:", e);
                        FFX.outputPane.rePrint(e.getMessage());
                    }
                });
            }

            public MyMenuItem(String text, Consumer<ActionEvent> consumer, boolean addToolBar) {

                this(text, consumer);
                this.addToolBar = addToolBar;
            }
        }
    }

    //根据菜单生成快捷按钮
    public VBox buildToolBar(PbMenuBar pbMenuBar) {
        var leftBar = new VBox();
        leftBar.setSpacing(20);
        leftBar.setPadding(new Insets(20, 5, 5, 5));
        //等长对齐
        final AtomicInteger maxLength = new AtomicInteger(-1);
        pbMenuBar.getMenus().forEach(menu -> {
            menu.getItems().forEach(menuItem -> {
                if (menuItem instanceof PbMenuBar.MyMenuItem) {
                    var myMenuItem = (PbMenuBar.MyMenuItem) menuItem;
                    if (myMenuItem.addToolBar) {
                        if (menuItem.getText().length() > maxLength.get()) {
                            maxLength.set(menuItem.getText().length());
                        }
                    }

                }
            });
        });
        pbMenuBar.getMenus().forEach(menu -> {
            menu.getItems().forEach(menuItem -> {
                if (menuItem instanceof PbMenuBar.MyMenuItem) {
                    var myMenuItem = (PbMenuBar.MyMenuItem) menuItem;
                    if (myMenuItem.addToolBar) {
                        //等长对齐
                        var text = myMenuItem.getText();

                        if (text.length() < maxLength.get()) {
                            int all = maxLength.get() - text.length();
                            int left = all / 2;
                            int right = all - left;
                            text = "　".repeat(left) + text + "　".repeat(right);
                        }
                        var button = new FFX.FButton(text, e -> myMenuItem.consumer.accept(e));
                        leftBar.getChildren().add(button);
                    }
                }
            });
        });
        return leftBar;
    }

}

