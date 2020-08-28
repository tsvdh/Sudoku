package sudoku.gui.main;

import sudoku.core.misc.ColorTable;
import common.SettingsHandler;
import common.options.InputMethod;
import common.options.Mode;
import common.options.Speed;
import sudoku.core.solving.IndependentGridSolver;
import sudoku.core.solving.LinkedGridSolver;
import sudoku.core.structure.Grid;
import sudoku.core.structure.Square;
import sudoku.gui.buttons.ColorButtons;
import common.popups.Confirmation;
import common.popups.Message;
import sudoku.gui.screens.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.util.LinkedList;
import java.util.List;

import static sudoku.gui.buttons.ButtonFactory.makeBigButton;

class EventComponent {

    private Sudoku parent;
    private Grid grid;
    private FillingComponent fillingComponent;

    private Button clearButton;
    private Button fillInButton;
    private Button solveButton;
    private Button settingsButton;

    Button pauseButton;
    Button paintButton;
    Button unPaintButton;
    ColorButtons colorButtons;

    private EventHandler<ActionEvent> paintActionEventHandler;
    private EventHandler<ActionEvent> cancelPaintEventHandler;

    private EventHandler<ActionEvent> fillInActionEventHandler;
    private EventHandler<ActionEvent> cancelFillInEventHandler;

    private EventHandler<ActionEvent> clearFillActionEventHandler;
    private EventHandler<ActionEvent> doneFillingActionEventHandler;

    private EventHandler<ActionEvent> clearPaintActionEventHandler;
    private EventHandler<ActionEvent> donePaintingActionEventHandler;


    EventComponent(Sudoku parent) {
        this.parent = parent;
        this.grid = parent.grid;
        this.fillingComponent = parent.fillingComponent;

        clearButton = makeBigButton("Clear");
        fillInButton = makeBigButton("Fill in");
        solveButton = makeBigButton("Solve");
        settingsButton = makeBigButton("Settings");
        pauseButton = makeBigButton("Pause");
        paintButton = makeBigButton("Paint");
        unPaintButton = makeBigButton("Clear");
        colorButtons = new ColorButtons();
    }

    void delayedConstruction() {
        // fillingComponent.setEventComponent(this);

        clearButton.setDisable(true);
        unPaintButton.setDisable(true);
        solveButton.setDisable(true);
        pauseButton.setDisable(true);

        constructEventHandlers(true);
        constructEventHandlers(false);

        fillInButton.setOnAction(fillInActionEventHandler);
        paintButton.setOnAction(paintActionEventHandler);
        clearButton.setOnAction(clearFillActionEventHandler);
        unPaintButton.setOnAction(clearPaintActionEventHandler);

        settingsButton.setOnAction(event -> {
            Settings settings = new Settings();
            settings.addObserver(parent.observerComponent);
            settings.build();
        });

        setSolveButtonAction();
    }

    List<Button> getTopButtons() {
        List<Button> buttons = new LinkedList<>();
        buttons.add(fillInButton);
        buttons.add(clearButton);
        buttons.add(solveButton);
        buttons.add(settingsButton);

        return buttons;
    }

    void setButtonsDoneSolving() {
        clearButton.setDisable(false);
        unPaintButton.setDisable(false);
        pauseButton.setDisable(true);
    }

    void setSolveButtonAction() {
        Speed speed = SettingsHandler.getInstance().getSpeed();

        if (speed == Speed.QUICK) {
            solveButton.setOnAction(event -> {

                solveButton.setDisable(true);
                settingsButton.setDisable(true);
                IndependentGridSolver gridSolver = new IndependentGridSolver(grid);
                showOptionsOfAllSquares();
                gridSolver.solve();
            });
        }

        if (speed == Speed.SLOW) {
            solveButton.setOnAction(event -> {

                solveButton.setDisable(true);
                fillInButton.setDisable(true);
                clearButton.setDisable(true);
                settingsButton.setDisable(true);
                paintButton.setDisable(true);
                unPaintButton.setDisable(true);
                pauseButton.setDisable(false);

                LinkedGridSolver gridSolver = new LinkedGridSolver(grid, pauseButton);
                gridSolver.addObserver(parent.observerComponent);

                setPauseButtonAction(gridSolver);

                showOptionsOfAllSquares();
                gridSolver.solve();
            });
        }
    }

    private void setPauseButtonAction(LinkedGridSolver gridSolver) {
        pauseButton.setOnAction(event -> {
            if (pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Play");
            } else {
                pauseButton.setText("Pause");
            }
            gridSolver.togglePause();
        });
    }

    void finishFillingIn(boolean fill) {
        fillingComponent.removeFillingAction();

        settingsButton.setDisable(false);

        if (fill) {
            fillInButton.setDisable(true);
            clearButton.setDisable(false);

            if (grid.isPainted()) {
                unPaintButton.setDisable(false);
            }
            else {
                paintButton.setDisable(false);
            }
            grid.setFilled(true);
        }
        else {
            paintButton.setDisable(true);
            unPaintButton.setDisable(false);

            if (grid.isFilled()) {
                clearButton.setDisable(false);
            }
            else {
                fillInButton.setDisable(false);
            }
            grid.setPainted(true);
        }

        flipFillButton(fill);
        flipClearButton(fill, false);

        Mode mode = SettingsHandler.getInstance().getMode();
        if (mode == Mode.JIGSAW && grid.isPainted() && grid.notSectionsFilled()) {

            for (GridElement gridElement : parent.gridElements) {
                String color = gridElement.getBackgroundColor();
                int index = ColorTable.getInstance().get(color);

                Square square = gridElement.getSquare();

                parent.grid.addSquare(square, index);
            }
        }

        if (grid.isValid()) {
            solveButton.setDisable(false);
        } else {
            new Message("The sudoku you entered is invalid!", true);
        }
    }

    private void showOptionsOfAllSquares() {
        for (GridElement gridElement : parent.gridElements) {
            if (gridElement.getSquare().hasNoValue()) {
                gridElement.update(null, true);
            }
        }
    }

    private void clearSquares(boolean fill) {
        for (GridElement gridElement : parent.gridElements) {
            if (fill) {
                gridElement.getSquare().setValue(null);
            }

            gridElement.setBorderColor("black");
        }
        if (!fill) {
            parent.colorGrid();
        }
    }

    private void removeIndicator() {
        for (GridElement gridElement : parent.gridElements) {
            gridElement.setBorderColor("black");
        }
    }

    private void constructEventHandlers(boolean fill) {
        EventHandler<ActionEvent> startEventHandler = event -> {
            solveButton.setDisable(true);
            settingsButton.setDisable(true);
            unPaintButton.setDisable(true);

            if (fill) {
                paintButton.setDisable(true);
            }
            else {
                fillInButton.setDisable(true);
                colorButtons.enableAll();
            }

            fillingComponent.setFillingAction(fill);
            flipFillButton(fill);
            // If fill is false(painting), disable clear button until all squares are painted.
            flipClearButton(fill, !fill);
        };

        EventHandler<ActionEvent> cancelEventHandler = event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                clearSquares(fill);

                if (!fill) {
                    colorButtons.resetAll();
                    colorButtons.disableAll();
                }

                paintButton.setDisable(false);
                fillInButton.setDisable(false);
                settingsButton.setDisable(false);

                fillingComponent.removeFillingAction();
                flipFillButton(fill);
                flipClearButton(fill, true);
            }
        };

        EventHandler<ActionEvent> removeEventHandler = event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {

                clearSquares(fill);
                solveButton.setDisable(true);

                if (fill) {
                    fillInButton.setDisable(false);
                    clearButton.setDisable(true);
                    grid.setFilled(false);
                    settingsButton.setDisable(false);
                }
                else {
                    paintButton.setDisable(false);
                    unPaintButton.setDisable(true);
                    grid.setPainted(false);
                    colorButtons.resetAll();
                    colorButtons.disableAll();
                }
            }
        };

        EventHandler<ActionEvent> doneEventHandler = (event) -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                removeIndicator();
                finishFillingIn(fill);
            }
        };

        if (fill) {
            fillInActionEventHandler = startEventHandler;
            cancelFillInEventHandler = cancelEventHandler;
            clearFillActionEventHandler = removeEventHandler;
            doneFillingActionEventHandler = doneEventHandler;
        }
        else {
            paintActionEventHandler = startEventHandler;
            cancelPaintEventHandler = cancelEventHandler;
            clearPaintActionEventHandler = removeEventHandler;
            donePaintingActionEventHandler = doneEventHandler;
        }
    }

    private void flipFillButton(boolean fill) {
        if (fill) {
            EventHandler<ActionEvent> eventHandler = fillInButton.getOnAction();
            if (eventHandler == fillInActionEventHandler) {
                fillInButton.setText("Cancel");
                fillInButton.setOnAction(cancelFillInEventHandler);
            } else {
                fillInButton.setText("Fill in");
                fillInButton.setOnAction(fillInActionEventHandler);
            }
        }
        else {
            EventHandler<ActionEvent> eventHandler = paintButton.getOnAction();
            if (eventHandler == paintActionEventHandler) {
                paintButton.setText("Cancel");
                paintButton.setOnAction(cancelPaintEventHandler);
            } else {
                paintButton.setText("Paint");
                paintButton.setOnAction(paintActionEventHandler);
            }
        }
    }

    private void flipClearButton(boolean fill, boolean disable) {
        if (SettingsHandler.getInstance().getInputMethod() == InputMethod.LEGACY) {
            return;
        }

        if (fill) {
            EventHandler<ActionEvent> eventHandler = clearButton.getOnAction();
            if (eventHandler == clearFillActionEventHandler) {
                clearButton.setText("Done");
                clearButton.setOnAction(doneFillingActionEventHandler);
            } else {
                clearButton.setText("Clear");
                clearButton.setOnAction(clearFillActionEventHandler);
            }
            clearButton.setDisable(disable);
        }
        else {
            EventHandler<ActionEvent> eventHandler = unPaintButton.getOnAction();
            if (eventHandler == clearPaintActionEventHandler) {
                unPaintButton.setText("Done");
                unPaintButton.setOnAction(donePaintingActionEventHandler);
            } else {
                unPaintButton.setText("Clear");
                unPaintButton.setOnAction(clearPaintActionEventHandler);
            }
            unPaintButton.setDisable(disable);
        }
    }
}
