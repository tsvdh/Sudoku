package gui.buttons;

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

    public boolean isFull(int index) {
        return getButton(index).getCount() >= 9;
    }

    public String getColor(int index) {
        return getButton(index).getColor();
    }
}
