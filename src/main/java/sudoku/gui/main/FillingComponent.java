package sudoku.gui.main;

import sudoku.core.misc.ColorTable;
import sudoku.core.misc.KeyCodeHandler;
import common.SettingsHandler;
import sudoku.gui.buttons.ColorButtons;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static sudoku.core.misc.KeyCodeHandler.IS_FORWARD;
import static sudoku.core.misc.KeyCodeHandler.IS_INPUT;
import static sudoku.core.misc.KeyCodeHandler.IS_REMOVE;
import static sudoku.core.misc.KeyCodeHandler.convertKeyEvent;

class FillingComponent {

    // private Sudoku parent;
    private EventComponent eventComponent;
    private Scene scene;
    private List<GridElement> gridElements;

    private EventHandler<KeyEvent> numberKeyEventHandler;
    private EventHandler<KeyEvent> arrowKeyEventHandler;

    private EventHandler<KeyEvent> keyDownEventHandler;
    private EventHandler<KeyEvent> keyUpEventHandler;

    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;
    private String lastMove;

    private KeyCodeHandler keyCodeHandler;

    FillingComponent(Sudoku parent) {
        // this.parent = parent;
        this.scene = parent.scene;
        this.gridElements = parent.gridElements;
        this.lastMove = "none";
        this.keyCodeHandler = new KeyCodeHandler();
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

    private void clearCurrent(boolean fill) {
        if (fill) {
            currentElement.getSquare().setValue(null);
        }
        else {
            String oldColor = currentElement.getBackgroundColor();
            if (!oldColor.equals("white")) {

                int oldNumber = ColorTable.getInstance().get(oldColor);
                eventComponent.colorButtons.decreaseCount(oldNumber);
                currentElement.setBackgroundColor("white");
            }
        }
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

                if (colorButtons.isNotFull(number)) {

                    clearCurrent(false);

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

            clearCurrent(fill);

            lastMove = "previous";

        } else {
            currentElement.setBorderColor("lightgreen");
        }
    }

    private int getNewIndex(KeyCode keyCode) {
        int index = gridElements.indexOf(currentElement);

        switch (keyCode) {
            case UP:
                if (index >= 9)
                    index -= 9;
                break;
            case DOWN:
                if (index <= 71)
                    index += 9;
                break;
            case LEFT:
                if (index % 9 != 0)
                    index -= 1;
                break;
            case RIGHT:
                if ((index - 8) % 9 != 0)
                    index += 1;
                break;
            default:
                break;
        }

        return index;
    }

    private List<EventHandler<KeyEvent>> getKeyRememberingEventHandlers() {
        ArrayList<EventHandler<KeyEvent>> list = new ArrayList<>();

        list.add(event -> keyCodeHandler.setCode(event, IS_INPUT.or(IS_REMOVE)));

        list.add(event -> keyCodeHandler.removeKeyCode(event));

        return list;
    }

    private EventHandler<KeyEvent> getLegacyActions(boolean fill) {
        return event -> {

            currentElement.setBorderColor("black");

            KeyCode keyCode = convertKeyEvent(event);

            if (IS_FORWARD.test(keyCode)) {
                fillSquare(keyCode, fill);
            }

            else if (IS_REMOVE.test(keyCode)) {
                goBackward(fill);
            }

            else {
                currentElement.setBorderColor("lightgreen");
            }
        };
    }

    private List<EventHandler<KeyEvent>> getArrowActions(boolean fill) {
        ArrayList<EventHandler<KeyEvent>> list = new ArrayList<>();

        // input
        list.add(event -> {
            if (keyCodeHandler.hasKeyCode()) {
                KeyCode keyCode = keyCodeHandler.getCode();

                if (keyCode.isDigitKey()) {
                    fillCurrentElement(keyCode, fill);

                    if (eventComponent.colorButtons.allFull()) {
                        eventComponent.unPaintButton.setDisable(false);
                    }
                } else if (keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE) {
                    clearCurrent(fill);
                }
            }
        });

        // arrows
        list.add(event -> {
            KeyCode keyCode = convertKeyEvent(event);
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
        keyCodeHandler.removeKeyCode();

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

                List<EventHandler<KeyEvent>> arrowActions = getArrowActions(fill);
                numberKeyEventHandler = arrowActions.get(0);
                arrowKeyEventHandler = arrowActions.get(1);

                List<EventHandler<KeyEvent>> keyRememberingActions = getKeyRememberingEventHandlers();
                keyDownEventHandler = keyRememberingActions.get(0);
                keyUpEventHandler = keyRememberingActions.get(1);

                scene.addEventFilter(KeyEvent.KEY_PRESSED, keyDownEventHandler);
                scene.addEventFilter(KeyEvent.KEY_RELEASED, keyUpEventHandler);

                scene.addEventFilter(KeyEvent.KEY_PRESSED, arrowKeyEventHandler);
                scene.addEventFilter(KeyEvent.KEY_PRESSED, numberKeyEventHandler);
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
                scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyUpEventHandler);
                scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyDownEventHandler);

                scene.removeEventFilter(KeyEvent.KEY_PRESSED, numberKeyEventHandler);
                scene.removeEventFilter(KeyEvent.KEY_PRESSED, arrowKeyEventHandler);
                break;
            case MOUSE:

                break;
        }
    }
}
