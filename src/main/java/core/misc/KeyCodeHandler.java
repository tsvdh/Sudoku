package core.misc;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.function.Predicate;

public class KeyCodeHandler {

    public static final Predicate<KeyCode> IS_INPUT = (keyCode) -> keyCode.isDigitKey() && keyCode != KeyCode.DIGIT0;
    public static final Predicate<KeyCode> IS_REMOVE = (keyCode) -> keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE;
    public static final Predicate<KeyCode> IS_FORWARD = (keyCode) -> keyCode.isDigitKey() || keyCode.isWhitespaceKey();

    private KeyCode keyCode;

    public KeyCode getCode() {
        return keyCode;
    }

    public boolean hasKeyCode() {
        return keyCode != null;
    }

    public void removeKeyCode() {
        this.keyCode = null;
    }

    public void removeKeyCode(KeyEvent keyEvent) {
        KeyCode keyCode = convertKeyEvent(keyEvent);

        if (this.keyCode == keyCode) {
            this.keyCode = null;
        }
    }

    public void setCode(KeyEvent keyEvent, Predicate<KeyCode> predicate) {
        KeyCode keyCode = convertKeyEvent(keyEvent);

        if (this.keyCode == null) {
            if (predicate.test(keyCode)) {
                this.keyCode = keyCode;
            } else {
                this.keyCode = null;
            }
        }
    }

    public static KeyCode convertKeyEvent(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();

        if (keyCode.isKeypadKey()) {
            keyCode = KeyCode.valueOf(keyCode.getName().split(" ")[1]);
        }

        return keyCode;
    }
}
