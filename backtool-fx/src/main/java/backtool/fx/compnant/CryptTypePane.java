package backtool.fx.compnant;

import backtool.service.compare.DigestCalculate;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import backtool.fx.compnant.FFX;

public class CryptTypePane extends HBox {
    public ToggleGroup versionTypeGroup = new ToggleGroup();
    RadioButton lengthButton = new RadioButton("异或");

    RadioButton md5Button = new RadioButton("AES");

    public CryptTypePane(DigestCalculate.DigestType digestType) {
        this.setSpacing(FFX.spacing);
        lengthButton.setToggleGroup(versionTypeGroup);
        lengthButton.setUserData(DigestCalculate.DigestType.LEN);
        md5Button.setToggleGroup(versionTypeGroup);
        md5Button.setUserData(DigestCalculate.DigestType.MD5);


        if (DigestCalculate.DigestType.LEN.equals(digestType)) {
            lengthButton.setSelected(true);
        } else {
            md5Button.setSelected(true);
        }

        this.getChildren().addAll(lengthButton, md5Button);
    }

    public DigestCalculate.DigestType getSelectVersionType() {
        Toggle selectedToggle = versionTypeGroup.getSelectedToggle();
        DigestCalculate.DigestType userData = (DigestCalculate.DigestType) selectedToggle.getUserData();
        return userData;
    }
}
