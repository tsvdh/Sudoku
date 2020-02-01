package gui;

import java.util.List;

class ColorButtons {

    private List<ColorButton> buttons;

    ColorButtons() {
        buttons = ButtonFactory.makeColorButtons();
    }

    List<ColorButton> getButtons() {
        return buttons;
    }

    private ColorButton getButton(int index) {
        return buttons.get(index - 1);
    }

    void resetAll() {
        for (ColorButton button : buttons) {
            button.reset();
        }
    }

    void incrementCount(int index) {
        getButton(index).incrementCount();
    }

    boolean isFull(int index) {
        return getButton(index).getCount() >= 9;
    }

    String getColor(int index) {
        return getButton(index).getColor();
    }
}
