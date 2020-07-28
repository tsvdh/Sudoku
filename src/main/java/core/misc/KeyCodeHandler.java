package core.misc;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyCodeHandler {

    public static KeyCode getCode(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();

        if (keyCode.isKeypadKey()) {
            keyCode = KeyCode.valueOf(keyCode.getName().split(" ")[1]);
        }

        return keyCode;
    }
}
