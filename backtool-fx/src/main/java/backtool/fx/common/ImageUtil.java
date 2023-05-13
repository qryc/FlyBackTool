package backtool.fx.common;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by qryc on 2021/8/25
 */
public class ImageUtil {
    public static ImageView getImageView(File file) {
        Image image = null;
        try {
            image = new Image(new FileInputStream(file), 500, 500, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new ImageView(image);
    }

    public static ImageView getImageView(byte[] bytes) {
        Image image = null;
        image = new Image(new ByteArrayInputStream(bytes));
        return new ImageView(image);
    }
}
