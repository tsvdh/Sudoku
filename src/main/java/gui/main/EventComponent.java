package gui.main;

import core.misc.ColorTable;
import core.misc.OverrideException;
import core.misc.SettingsHandler;
import core.misc.options.Mode;
import core.misc.options.Speed;
import core.solving.IndependentGridSolver;
import core.solving.LinkedGridSolver;
import core.structure.Grid;
import core.structure.Square;
import gui.buttons.ColorButtons;
import gui.popups.Confirmation;
import gui.popups.Message;
import gui.popups.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

import java.util.LinkedList;
import java.util.List;

import static gui.buttons.ButtonFactory.makeButton;

class EventComponent {

    private Sudoku parent;
    private Grid grid;

    private boolean filled;
    boolean painted;

    private Button clearButton;
    private Button fillInButton;
    private Button solveButton;
    private Button settingsButton;

    Button pauseButton;
    Button paintButton;
    Button unPaintButton;
    ColorButtons colorButtons;

    EventHandler<KeyEvent> keyEventHandler;

    private EventHandler<ActionEvent> paintActionEventHandler;
    private EventHandler<ActionEvent> cancelPaintEventHandler;

    private EventHandler<ActionEvent> fillInActionEventHandler;
    private EventHandler<ActionEvent> cancelFillInEventHandler;

    private EventHandler<ActionEvent> clearActionEventHandler;
    private EventHandler<ActionEvent> unPaintActionEventHandler;

    EventComponent(Sudoku parent, Scene scene) {
        if (parent.observerComponent == null) {
            throw new IllegalStateException("Parent Sudoku must have an ObserverComponent called 'observerComponent'.");
        }

        this.parent = parent;
        this.grid = parent.grid;
        this.painted = false;
        this.filled = false;

        clearButton = makeButton("Clear");
        fillInButton = makeButton("Fill in");
        solveButton = makeButton("Solve");
        settingsButton = makeButton("Settings");
        pauseButton = makeButton("Pause");
        paintButton = makeButton("Paint");
        unPaintButton = makeButton("Clear");
        colorButtons = new ColorButtons();

        clearButton.setDisable(true);
        unPaintButton.setDisable(true);
        solveButton.setDisable(true);
        pauseButton.setDisable(true);

        constructEventHandlers(scene, true);
        constructEventHandlers(scene, false);

        fillInButton.setOnAction(fillInActionEventHandler);
        paintButton.setOnAction(paintActionEventHandler);
        clearButton.setOnAction(clearActionEventHandler);
        unPaintButton.setOnAction(unPaintActionEventHandler);

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

    void finishFillingIn(Scene scene, boolean fill) {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        settingsButton.setDisable(false);

        if (fill) {
            fillInButton.setDisable(true);
            clearButton.setDisable(false);

            if (painted) {
                unPaintButton.setDisable(false);
            }
            else {
                paintButton.setDisable(false);
            }
            filled = true;
        }
        else {
            paintButton.setDisable(true);
            unPaintButton.setDisable(false);

            if (filled) {
                clearButton.setDisable(false);
            }
            else {
                fillInButton.setDisable(false);
            }
            painted = true;
        }

        flipButton(fill);

        if (filled && painted) {

            Mode mode = SettingsHandler.getInstance().getMode();
            if (mode == Mode.JIGSAW && !grid.isValid()) {

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
                new Message("The sudoku you entered is invalid!");
            }
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
                try {
                    gridElement.getSquare().setValue(null);
                } catch (OverrideException e) {
                    System.out.println(e.getMessage());
                }
            }

            gridElement.setBorderColor("black");
        }
        if (!fill) {
            parent.colorGrid();
        }
    }

    private void constructEventHandlers(Scene scene, boolean fill) {
        EventHandler<ActionEvent> startEventHandler = event -> {
            solveButton.setDisable(true);
            settingsButton.setDisable(true);
            clearButton.setDisable(true);
            unPaintButton.setDisable(true);

            if (fill) {
                paintButton.setDisable(true);
            }
            else {
                fillInButton.setDisable(true);
                colorButtons.enableAll();
            }

            parent.iterationComponent.setKeyAction(scene, fill);
            flipButton(fill);
        };

        EventHandler<ActionEvent> cancelEventHandler = event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                clearSquares(fill);

                if (!fill) {
                    colorButtons.resetAll();
                    colorButtons.disableAll();
                }

                scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

                paintButton.setDisable(false);
                fillInButton.setDisable(false);
                settingsButton.setDisable(false);

                flipButton(fill);
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
                    filled = false;
                    settingsButton.setDisable(false);
                }
                else {
                    paintButton.setDisable(false);
                    unPaintButton.setDisable(true);
                    painted = false;
                    colorButtons.resetAll();
                    colorButtons.disableAll();
                }
            }
        };

        if (fill) {
            fillInActionEventHandler = startEventHandler;
            cancelFillInEventHandler = cancelEventHandler;
            clearActionEventHandler = removeEventHandler;
        }
        else {
            paintActionEventHandler = startEventHandler;
            cancelPaintEventHandler = cancelEventHandler;
            unPaintActionEventHandler = removeEventHandler;
        }
    }

    private void flipButton(boolean fill) {
        if (fill) {
            EventHandler<ActionEvent> eventHandler = fillInButton.getOnAction();
            if (eventHandler.equals(fillInActionEventHandler)) {
                fillInButton.setText("Cancel");
                fillInButton.setOnAction(cancelFillInEventHandler);
            } else {
                fillInButton.setText("Fill in");
                fillInButton.setOnAction(fillInActionEventHandler);
            }
        }
        else {
            EventHandler<ActionEvent> eventHandler = paintButton.getOnAction();
            if (eventHandler.equals(paintActionEventHandler)) {
                paintButton.setText("Cancel");
                paintButton.setOnAction(cancelPaintEventHandler);
            } else {
                paintButton.setText("Paint");
                paintButton.setOnAction(paintActionEventHandler);
            }
        }
    }
}
