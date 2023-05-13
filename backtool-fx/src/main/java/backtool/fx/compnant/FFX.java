package backtool.fx.compnant;

import backtool.service.compare.DigestCalculate;
import fly4j.common.util.StringConst;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import backtool.service.FxConst;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FFX {
    public static Insets DEFAULT_INSERT = new Insets(20, 5, 5, 5);
    public static double width;
    public static double height;
    //适用于文件输入框，长文本输入框等需要一定长度的输入
    public static double inputLength;
    //适用于输入数字等短输入场景
    public static double shortInputWidth = 150;
    public static double contentLength;
    public static double spacing = 10;
    public static final int image_width = 200;
    public static final int image_height = 200;
    //输出区域
    public static final FFX.OutputPane outputPane = new FFX.OutputPane("执行结果");
    public static final FFX.OutputPane logPane = new FFX.OutputPane("执行日志");


    static {
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        FFX.width = screenRectangle.getWidth();
        FFX.height = screenRectangle.getHeight();
        FFX.inputLength = FFX.width / 3;
        FFX.contentLength = FFX.width / 2;
        FFX.outputPane.setMinWidth(contentLength);
        FFX.logPane.setMinWidth(FFX.contentLength);
    }

    public static void openFile(String path) {
        openFile(new File(path));
    }

    public static void openFile(File file) {
        try {
            if (file.isFile())
                file = file.getParentFile();
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextField getNumberInputTextField(Integer count) {
        TextField countFiled = new TextField("" + count);
        countFiled.setMaxWidth(FFX.shortInputWidth);
        return countFiled;
    }

    public static ImageView imageView(File file, int w, int h) {
        javafx.scene.image.Image image = null;
        try {
            image = new Image(new FileInputStream(file), w, h, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        var tooltip = new Tooltip(file.getAbsolutePath());
        var imageView = new ImageView(image);
        Tooltip.install(imageView, tooltip);
//        tooltip.setGraphic(imageView);
        return imageView;
    }

    public static Label getWrapLabel(String text, int maxWidth) {
        Label label = new Label(text);
        label.setMaxWidth(maxWidth);
        label.setWrapText(true);
        return label;
    }

    public static HBox getHBox(Node... children) {
        HBox hBox = new HBox(children);
        hBox.setSpacing(FFX.spacing);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    public static VBox getVBox(Node... children) {
        VBox vBox = new VBox(children);
        vBox.setSpacing(FFX.spacing);
        return vBox;
    }

    public static class OutputPane extends TextArea {
        private static final Logger log = LoggerFactory.getLogger(OutputPane.class);
        private final List<String> msgs = new ArrayList<>();


        public OutputPane(String result) {
            this.setPrefWidth(contentLength);
            this.setPadding(DEFAULT_INSERT);
            this.setText(result);
            this.setMinHeight(400);
        }

        public OutputPane() {
            this("");
        }

        public void appendPrint(String result) {
            System.out.println(result);
            log.info(result);
            while (msgs.size() > 100) {
                msgs.remove(0);
            }
            msgs.add(result);
            StringBuilder builder = new StringBuilder();
            msgs.forEach(msg -> builder.append(msg).append(StringConst.LF));
            this.setText(builder.toString());
        }

        public void rePrint(String result) {
            msgs.clear();
            appendPrint(result);
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class DirInputFx extends HBox {
        public TextField textField = new TextField();

        public DirInputFx() {
            this(null);
        }

        public DirInputFx(double prefWidth) {
            this(null);
            textField.setPrefWidth(prefWidth);
        }

        public DirInputFx(String dirValue) {
            this.setSpacing(spacing);
            textField.setEditable(false);
            textField.setPromptText("请选择文件夹");
            if (StringUtils.isNotBlank(dirValue)) {
                textField.setText(dirValue);
            }
            this.getChildren().addAll(textField, new FFX.FButton("...", event -> select()), new FFX.FButton("置空", event -> textField.setText("")));
        }

        private void select() {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择文件夹");
            File directory = directoryChooser.showDialog(new Stage());
            if (directory != null) {
                textField.setText(directory.getAbsolutePath());
            }
        }

        public String getSelectDir() {
            return textField.getText();
        }

        public void setSelectDir(String dirValue) {
            textField.setText(dirValue);
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class FileInputFx extends HBox {
        private TextField textField = new TextField();

        public FileInputFx() {
            this(null);
        }

        public FileInputFx(double prefWidth) {
            this(null);
            textField.setPrefWidth(prefWidth);
        }

        public FileInputFx(String dirValue) {
            this.setSpacing(spacing);
            textField.setEditable(false);
            textField.setPromptText("请选择文件");
            if (StringUtils.isNotBlank(dirValue)) {
                textField.setText(dirValue);
            }
            this.getChildren().addAll(textField, new FFX.FButton("...", event -> select()), new FFX.FButton("置空", event -> textField.setText("")));
        }

        private void select() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("请选择文件");
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                textField.setText(file.getAbsolutePath());
            }
        }

        public String getSelectFile() {
            return textField.getText();
        }

        public void setSelectFile(String dirValue) {
            textField.setText(dirValue);
        }


    }

    /**
     * Created by qryc on 2020/3/15.
     */
    public static class DirSelectPan extends GridPane {
        private List<DirInputFx> selectDirFxs = new ArrayList<>();

        public DirSelectPan(int rowCount, double prefWidth) {
            this.setHgap(5);
            this.setVgap(5);
            this.addRow(0, new Label("请选择文件夹"));
            for (int i = 0; i < rowCount; i++) {
                selectDirFxs.add(new DirInputFx(prefWidth));
                //添加删除按钮
                Button delButton = new FFX.FButton("重置");
                final int index = i;
                delButton.setOnAction(event -> {
                    selectDirFxs.get(index).setSelectDir("");
                });
                this.addRow(i + 1, selectDirFxs.get(i), delButton);
            }
        }

        public void init(List<String> zipConfigs) {
            //全部内容置空，因为可能顺序不对，全删除，全设置
            for (int i = 0; i < selectDirFxs.size(); i++) {
                selectDirFxs.get(i).setSelectDir("");
            }
            //重置所有
            for (int i = 0; i < zipConfigs.size(); i++) {
                if (StringUtils.isNotBlank(zipConfigs.get(i))) {
                    selectDirFxs.get(i).setSelectDir(zipConfigs.get(i));
                }
            }
        }

        public List<String> getSelectDirs() {
            List<String> dirs = new ArrayList<>();
            for (int i = 0; i < selectDirFxs.size(); i++) {
                if (StringUtils.isNotBlank(selectDirFxs.get(i).getSelectDir())) {
                    dirs.add(selectDirFxs.get(i).getSelectDir());
                }
            }
            return dirs;
        }
    }

    public static class FGridPane extends GridPane {
        int index = 0;

        public FGridPane() {
            this.setAlignment(Pos.TOP_LEFT);
            this.setHgap(5);//控件间的垂直间隔
            this.setVgap(5);//控件间的水平间隔
            this.setPadding(new javafx.geometry.Insets(10, 5, 5, 10));
        }

        public void addRow(Node... children) {
            this.addRow(index++, children);
        }

        public void addMergeRow(Node child, int colspan) {
            this.add(child, 0, index++, colspan, 1);
        }

        public void clear() {
            this.getChildren().clear();
            index = 0;
        }
    }

    /**
     * Created by qryc on 2020/3/7.
     */
    public static class FButton extends Button {

        public FButton(String text, FButtonConsumer<ActionEvent> consumer) {
            super(text);
            this.setFont(Font.font(16));
            this.setOnAction(event -> {
                try {
                    consumer.accept(event);
                } catch (Exception e) {
                    LoggerFactory.getLogger(FxConst.exceptionLog).error("Exception:", e);
                    FFX.outputPane.rePrint(e.getMessage());
                }
            });
        }

        public FButton(String text) {
            super(text);
            this.setFont(Font.font(16));
        }

    }

    @FunctionalInterface
    public interface FButtonConsumer<T> {
        void accept(T t) throws Exception;
    }

    public static void alert(String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示框");
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    public static class RadioPane<T> extends HBox {
        ToggleGroup versionTypeGroup = new ToggleGroup();
        RadioButton rb1 = new RadioButton();

        RadioButton rb2 = new RadioButton();

        public RadioPane(String text, String t1, T v1, String t2, T v2) {
            this.setSpacing(FFX.spacing);
            rb1.setText(t1);
            rb1.setToggleGroup(versionTypeGroup);
            rb1.setUserData(v1);
            rb2.setText(t2);
            rb2.setToggleGroup(versionTypeGroup);
            rb2.setUserData(v2);


            this.getChildren().addAll(new Label(text), rb1, rb2);
        }

        public void select(T selectValue) {

            if (rb1.getUserData().equals(selectValue)) {
                rb1.setSelected(true);
            } else {
                rb2.setSelected(true);
            }

        }

        public T getSelectValue() {
            Toggle selectedToggle = versionTypeGroup.getSelectedToggle();
            T userData = (T) selectedToggle.getUserData();
            return userData;
        }
    }

}
