package gui.main;

import core.misc.ColorTable;
import core.misc.OverrideException;
import gui.buttons.ColorButtons;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ListIterator;

class IterationComponent {

    private Sudoku parent;

    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;
    private String lastMove;

    IterationComponent(Sudoku parent) {
        this.parent = parent;
        this.lastMove = "none";
    }

    private void goToNextElement() {
        currentElement = elementIterator.next();
        currentElement.setBorderColor("lightgreen");
    }

    private void goToPreviousElement() {
        currentElement = elementIterator.previous();
        currentElement.setBorderColor("lightgreen");
    }

    private void goForward(Scene scene, boolean fill) {
        if (elementIterator.hasNext()) {
            if (lastMove.equals("previous")) {
                currentElement = elementIterator.next();
            }
            goToNextElement();
        } else {
            parent.eventComponent.finishFillingIn(scene, fill);
        }

        lastMove = "next";
    }

    private void fillSquare(KeyCode keyCode, boolean fill, Scene scene) {
        int number = -1;
        boolean goToNextPainting = false;

        if (!keyCode.isWhitespaceKey()) {
            number = new Integer(keyCode.getName());
            if (number != 0) {

                if (fill) {
                    try {
                        currentElement.getSquare().setValue(number);
                    } catch (OverrideException e) {
                        System.out.println(e.getMessage());
                    }

                } else {
                    ColorButtons colorButtons = parent.eventComponent.colorButtons;

                    if (!colorButtons.isFull(number)) {
                        colorButtons.incrementCount(number);
                        String color = colorButtons.getColor(number);
                        currentElement.setBackgroundColor(color);
                        goToNextPainting = true;
                    }
                }
            }
        }

        boolean paintButNotNext = keyCode.isWhitespaceKey() || number == 0 || !goToNextPainting;

        if (!paintButNotNext || fill) {
            goForward(scene, fill);
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
                try {
                    currentElement.getSquare().setValue(null);
                } catch (OverrideException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                String color = currentElement.getBackgroundColor();
                ColorTable table = ColorTable.getInstance();

                Integer number = table.get(color);
                parent.eventComponent.colorButtons.decreaseCount(number);

                currentElement.setBackgroundColor("white");
            }

            lastMove = "previous";

        } else {
            currentElement.setBorderColor("lightgreen");
        }
    }

    void setKeyAction(Scene scene, boolean fill) {
        elementIterator = parent.gridElements.listIterator();
        goToNextElement();

        parent.eventComponent.keyEventHandler = event -> {

            currentElement.setBorderColor("black");

            KeyCode keyCode = event.getCode();

            if (keyCode.isDigitKey() || keyCode.isWhitespaceKey()) {
                fillSquare(keyCode, fill, scene);
            }

            else if (keyCode.equals(KeyCode.BACK_SPACE) || keyCode.equals(KeyCode.DELETE)) {
                goBackward(fill);
            }

            else {
                currentElement.setBorderColor("lightgreen");
            }
        };

        scene.addEventFilter(KeyEvent.KEY_PRESSED, parent.eventComponent.keyEventHandler);
    }
}
