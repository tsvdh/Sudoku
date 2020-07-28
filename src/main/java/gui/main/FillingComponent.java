package gui.main;

import core.misc.ColorTable;
import core.misc.SettingsHandler;
import gui.buttons.ColorButtons;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static core.misc.KeyCodeHandler.getCode;

class FillingComponent {


    // private Sudoku parent;
    private EventComponent eventComponent;
    private Scene scene;
    private List<GridElement> gridElements;

    private EventHandler<KeyEvent> numberKeyEventHandler;
    private EventHandler<KeyEvent> arrowKeyEventHandler;

    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;
    private String lastMove;

    FillingComponent(Sudoku parent) {
        // this.parent = parent;
        this.scene = parent.scene;
        this.gridElements = parent.gridElements;
        this.lastMove = "none";
    }

    void setEventComponent(EventComponent eventComponent) {
        this.eventComponent = eventComponent;
    }

    private void goToNextElement() {
        currentElement = elementIterator.next();
        currentElement.setBorderColor("lightgreen");
    }

    private void goToPreviousElement() {
        currentElement = elementIterator.previous();
        currentElement.setBorderColor("lightgreen");
    }

    private void goForward(boolean fill) {
        if (elementIterator.hasNext()) {
            if (lastMove.equals("previous")) {
                currentElement = elementIterator.next();
            }
            goToNextElement();
        } else {
            eventComponent.finishFillingIn(fill);
        }

        lastMove = "next";
    }

    private boolean fillCurrentElement(KeyCode keyCode, boolean fill) {
        int number = new Integer(keyCode.getName());
        if (number != 0) {

            if (fill) {
                currentElement.getSquare().setValue(null);
                currentElement.getSquare().setValue(number);
            }
            else {
                ColorButtons colorButtons = eventComponent.colorButtons;

                if (!colorButtons.isFull(number)) {

                    String oldColor = currentElement.getBackgroundColor();
                    if (!oldColor.equals("white")) {

                        int oldNumber = ColorTable.getInstance().get(oldColor);
                        colorButtons.decreaseCount(oldNumber);
                    }

                    String newColor = colorButtons.getColor(number);
                    colorButtons.incrementCount(number);

                    currentElement.setBackgroundColor(newColor);
                    return true;
                }
            }
        }

        return false;
    }

    private void fillSquare(KeyCode keyCode, boolean fill) {
        boolean goToNextPainting = false;

        if (!keyCode.isWhitespaceKey()) {
            goToNextPainting = fillCurrentElement(keyCode, fill);
        }

        boolean paintButNotNext = keyCode.isWhitespaceKey() || Integer.parseInt(keyCode.getName()) == 0 || !goToNextPainting;

        if (!paintButNotNext || fill) {
            goForward(fill);
        }
        else {
            currentElement.setBorderColor("lightgreen");
        }
    }

    private void goBackward(boolean fill) {
        if (elementIterator.hasPrevious()) {

            if (lastMove.equals("next")) {
                currentElement = elementIterator.previous();
            }
            goToPreviousElement();

            if (fill) {
                currentElement.getSquare().setValue(null);
            }
            else {
                String color = currentElement.getBackgroundColor();
                ColorTable table = ColorTable.getInstance();

                Integer number = table.get(color);
                eventComponent.colorButtons.decreaseCount(number);

                currentElement.setBackgroundColor("white");
            }

            lastMove = "previous";

        } else {
            currentElement.setBorderColor("lightgreen");
        }
    }

    private int getNewIndex(KeyCode keyCode) {
        int index = gridElements.indexOf(currentElement);
        int oldIndex = index;

        switch (keyCode) {
            case UP:
                index -= 9;
                break;
            case DOWN:
                index += 9;
                break;
            case LEFT:
                index -= 1;
                break;
            case RIGHT:
                index += 1;
                break;
            default:
                break;
        }

        if (index >= 0 && index < 81) {
            return index;
        }
        else {
            return oldIndex;
        }
    }

    private EventHandler<KeyEvent> getLegacyActions(boolean fill) {
        return event -> {

            currentElement.setBorderColor("black");

            KeyCode keyCode = getCode(event);

            if (keyCode.isDigitKey() || keyCode.isWhitespaceKey()) {
                fillSquare(keyCode, fill);
            }

            else if (keyCode.equals(KeyCode.BACK_SPACE) || keyCode.equals(KeyCode.DELETE)) {
                goBackward(fill);
            }

            else {
                currentElement.setBorderColor("lightgreen");
            }
        };
    }

    private List<EventHandler<KeyEvent>> getArrowActions(boolean fill) {
        ArrayList<EventHandler<KeyEvent>> list = new ArrayList<>();

        // numbers
        list.add(event -> {
            KeyCode keyCode = getCode(event);
            if (keyCode.isDigitKey()) {
                fillCurrentElement(keyCode, fill);
            }
        });

        // arrows
        list.add(event -> {
            KeyCode keyCode = getCode(event);
            if (keyCode.isArrowKey()) {
                currentElement.setBorderColor("black");
                int newIndex = getNewIndex(keyCode);
                currentElement = gridElements.get(newIndex);
                currentElement.setBorderColor("lightgreen");
            }
        });

        return list;
    }

    void setFillingAction(boolean fill) {
        switch (SettingsHandler.getInstance().getInputMethod()) {
            case LEGACY:
                elementIterator = gridElements.listIterator();
                goToNextElement();

                numberKeyEventHandler = getLegacyActions(fill);
                scene.addEventFilter(KeyEvent.KEY_PRESSED, numberKeyEventHandler);
                break;

            case ARROWS:
                currentElement = gridElements.get(0);
                currentElement.setBorderColor("lightgreen");

                List<EventHandler<KeyEvent>> eventHandlers = getArrowActions(fill);
                numberKeyEventHandler = eventHandlers.get(0);
                arrowKeyEventHandler = eventHandlers.get(1);

                scene.addEventFilter(KeyEvent.KEY_PRESSED, numberKeyEventHandler);
                scene.addEventFilter(KeyEvent.KEY_PRESSED, arrowKeyEventHandler);
                break;

            case MOUSE:

                break;
        }
    }

    void removeFillingAction() {
        switch (SettingsHandler.getInstance().getInputMethod()) {
            case LEGACY:
                scene.removeEventFilter(KeyEvent.KEY_PRESSED, numberKeyEventHandler);
                break;
            case ARROWS:
                scene.removeEventFilter(KeyEvent.KEY_PRESSED, numberKeyEventHandler);
                scene.removeEventFilter(KeyEvent.KEY_PRESSED, arrowKeyEventHandler);
                break;
            case MOUSE:

                break;
        }
    }
}
