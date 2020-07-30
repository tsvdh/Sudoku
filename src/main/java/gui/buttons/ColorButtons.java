package gui.buttons;

import javafx.scene.control.Button;

import java.util.List;

public class ColorButtons {

    private List<ColorButton> buttons;

    public ColorButtons() {
        buttons = ButtonFactory.makeColorButtons();
    }

    public List<ColorButton> getButtons() {
        return buttons;
    }

    private ColorButton getButton(int index) {
        return buttons.get(index - 1);
    }

    public void resetAll() {
        for (ColorButton button : buttons) {
            button.reset();
        }
    }

    public void incrementCount(int index) {
        getButton(index).incrementCount();
    }

    public void decreaseCount(int index) {
        getButton(index).decreaseCount();
    }

    public boolean isNotFull(int index) {
        return getButton(index).getCount() < 9;
    }

    public String getColor(int index) {
        return getButton(index).getColor();
    }

    public void enableAll() {
        for (Button button : buttons) {
            button.setDisable(false);
        }
    }

    public void disableAll() {
        for (Button button : buttons) {
            button.setDisable(true);
        }
    }

    public boolean allFull() {
        for (int i = 1; i <= 9; i++) {
            if (isNotFull(i)) {
                return false;
            }
        }
        return true;
    }
}
