package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.ColorTable;
import utils.Grid;
import utils.IndependentGridSolver;
import utils.LinkedGridSolver;
import utils.OverrideException;
import utils.SettingsHandler;
import utils.Square;

import utils.SettingsPossibilities.Speed;
import utils.SettingsPossibilities.Mode;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import static gui.ButtonFactory.makeButton;

public class Sudoku extends Application implements Observer {

    private Grid grid;
    private List<GridElement> gridElements;
    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;
    private String lastMove;
    private BorderPane borderPane;
    private Mode oldMode;
    private boolean filled;
    private boolean painted;

    private Button clearButton;
    private Button fillInButton;
    private Button solveButton;
    private Button settingsButton;
    private Button pauseButton;
    private Button paintButton;
    private Button unPaintButton;
    private ColorButtons colorButtons;

    private EventHandler<KeyEvent> keyEventHandler;

    private EventHandler<ActionEvent> paintActionEventHandler;
    private EventHandler<ActionEvent> cancelPaintEventHandler;

    private EventHandler<ActionEvent> fillInActionEventHandler;
    private EventHandler<ActionEvent> cancelFillInEventHandler;

    private EventHandler<ActionEvent> clearActionEventHandler;
    private EventHandler<ActionEvent> unPaintActionEventHandler;

    //private Integer[][] given;

    private static SettingsHandler settingsHandler;

    public static SettingsHandler getSettingsHandler() {
        return settingsHandler;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        settingsHandler = new SettingsHandler();

        Mode mode = getSettingsHandler().getMode();
        this.grid = new Grid(mode);
        this.oldMode = mode;
        this.gridElements = new LinkedList<>();
        this.lastMove = "none";
        this.painted = false;
        this.filled = false;


        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 50, 10 ,50));
        borderPane.setStyle("-fx-background-color: white");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        //gridPane.setStyle("-fx-background-color: black;");
        borderPane.setCenter(gridPane);

        buildGrid();

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


        HBox hBoxTop = new HBox();
        hBoxTop.setPadding(new Insets(10, 10, 50 ,10));
        hBoxTop.getChildren().addAll(fillInButton,
                                clearButton,
                                solveButton,
                                settingsButton);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setSpacing(40);

        borderPane.setTop(hBoxTop);

        setSpecificButtons();

        Scene scene = new Scene(borderPane);

        constructEventHandlers(scene, true);
        constructEventHandlers(scene, false);

        fillInButton.setOnAction(fillInActionEventHandler);
        paintButton.setOnAction(paintActionEventHandler);
        clearButton.setOnAction(clearActionEventHandler);
        unPaintButton.setOnAction(unPaintActionEventHandler);

        settingsButton.setOnAction(event -> {
            Settings settings = new Settings();
            settings.addObserver(this);
            settings.build();
        });

        setSolveButtonAction();

        stage.setScene(scene);
        stage.setTitle("Sudoku solver");
        stage.getIcons().add(new Image("/images/icon.png"));

        hBoxTop.requestFocus();

        for (Node node : hBoxTop.getChildren()) {
            node.setOnMousePressed(event -> hBoxTop.requestFocus());
        }

        stage.show();
    }

    private void setSpecificButtons() {
        VBox vBoxBottom = new VBox();
        vBoxBottom.setPadding(new Insets(50, 10, 40, 10));
        vBoxBottom.setAlignment(Pos.CENTER);
        vBoxBottom.setSpacing(40);

        borderPane.setBottom(vBoxBottom);

        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(0, 0, 10, 0));
        topHBox.setAlignment(Pos.CENTER);
        topHBox.setSpacing(40);

        HBox bottomHBox = new HBox();
        topHBox.setPadding(new Insets(10, 0, 0, 0));
        bottomHBox.setAlignment(Pos.CENTER);
        bottomHBox.setSpacing(14);

        vBoxBottom.getChildren().addAll(topHBox, bottomHBox);


        Speed speed = getSettingsHandler().getSpeed();
        Mode mode = getSettingsHandler().getMode();

        if (speed == Speed.SLOW) {
            topHBox.getChildren().addAll(pauseButton);
        }

        if (mode == Mode.JIGSAW) {
            topHBox.getChildren().addAll(paintButton, unPaintButton);
            bottomHBox.getChildren().addAll(colorButtons.getButtons());

            if (painted) {
                paintButton.setDisable(true);
                unPaintButton.setDisable(false);
            }
            else {
                paintButton.setDisable(false);
                unPaintButton.setDisable(true);
            }
        }

        else {
            Button filler = makeButton("Invisible");
            filler.setVisible(false);
            bottomHBox.getChildren().add(filler);
        }

        for (Node node : topHBox.getChildren()) {
            node.setOnMousePressed(event -> topHBox.requestFocus());
        }
    }

    private void buildGrid() {
        GridPane gridPane = (GridPane) borderPane.getCenter();
        for (int y = 1; y <= 17; y += 2) {
            for (int x = 1; x <= 17; x += 2) {
                Square square = new Square(null, x / 2 + 1, y / 2 + 1);
                grid.addSquare(square);

                GridElement gridElement = new GridElement(square);

                gridPane.add(gridElement, x , y);
                gridElements.add(gridElement);
            }
        }

        addLines();

        colorGrid();
    }

    private void rebuildGrid() {
        Mode newMode = getSettingsHandler().getMode();
        if (!newMode.equals(oldMode)) {

            this.grid = new Grid(newMode);
            for (GridElement gridElement : gridElements) {
                Square square = gridElement.getSquare();
                grid.addSquare(square);
            }

            colorGrid();
        }

        addLines();
    }

    private void addLines() {
        GridPane gridPane = (GridPane) borderPane.getCenter();
        float width = 0.5f;
        if (getSettingsHandler().getMode() == Mode.JIGSAW) {
            width = 0;
        }
        String style = "-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: black;"
                + "-fx-border-width: " + width + "px;";

        for (int y = 1; y <= 17; y += 2) {
            for (int x = 0; x <= 18; x += 6) {
                Button verticalLine = (Button) getFromGridPane(x, y);

                if (verticalLine == null) {
                    verticalLine = new Button();
                    verticalLine.setPrefSize(0, GridElement.size);
                    verticalLine.setFont(new Font(0));

                    gridPane.add(verticalLine, x, y);
                }

                verticalLine.setStyle(style);
            }
        }
        for (int x = 1; x <= 17; x += 2) {
            for (int y = 0; y <= 18; y += 6) {
                Button horizontalLine = (Button) getFromGridPane(x, y);

                if (horizontalLine == null) {
                    horizontalLine = new Button();
                    horizontalLine.setPrefSize(GridElement.size, 0);
                    horizontalLine.setFont(new Font(0));

                    gridPane.add(horizontalLine, x, y);
                }

                horizontalLine.setStyle(style);
            }
        }
    }

    private Node getFromGridPane(int column, int row) {
        GridPane gridPane = (GridPane) borderPane.getCenter();

        for (Node element : gridPane.getChildren()) {
            if (GridPane.getRowIndex(element) == row && GridPane.getColumnIndex(element) == column) {
                return element;
            }
        }

        return null;
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
            finishFillingIn(scene, fill);
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
                colorButtons.decreaseCount(number);

                currentElement.setBackgroundColor("white");
            }

            lastMove = "previous";

        } else {
            currentElement.setBorderColor("lightgreen");
        }
    }

    private void setKeyAction(Scene scene, boolean fill) {
        elementIterator = gridElements.listIterator();
        goToNextElement();

        keyEventHandler = event -> {

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

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    private void clearSquares(boolean fill) {
        for (GridElement gridElement : gridElements) {
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
            colorGrid();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String status = (String) arg;

        if (o instanceof LinkedGridSolver) {
            if (status.equals("magic")) {
                highLightFirstPair();
                return;
            }

            List<GridElement> list = getGridElements((LinkedGridSolver) o);

            GridElement currentElement = list.get(0);
            GridElement previousElement = null;
            if (list.size() > 1) {
                previousElement = list.get(1);
            }

            if (status.equals("done")) {
                clearButton.setDisable(false);
                unPaintButton.setDisable(false);
                pauseButton.setDisable(true);
                currentElement.revertBorderColor();
            }
            if (status.equals("working")) {
                if (previousElement != null) {
                    previousElement.revertBorderColor();
                }
                currentElement.setBorderColor("yellow");
            }
        }

        if (o instanceof Settings) {
            setSolveButtonAction();

            rebuildGrid();
            setSpecificButtons();

            oldMode = getSettingsHandler().getMode();
        }
    }

    private List<GridElement> getGridElements(LinkedGridSolver gridSolver) {
        List<GridElement> list = new LinkedList<>();

        Square previousSquare = gridSolver.getPreviousSquare();
        Square currentSquare = gridSolver.getCurrentSquare();

        for (GridElement gridElement : gridElements) {
            if (gridElement.getSquare().equals(currentSquare)) {
                list.add(gridElement);
                break;
            }
        }
        for (GridElement gridElement : gridElements) {
            if (gridElement.getSquare().equals(previousSquare)) {
                list.add(gridElement);
                break;
            }
        }

        return list;
    }

    private void highLightFirstPair() {
        for (GridElement gridElement : gridElements) {
            Square square = gridElement.getSquare();
            if (square.isPair()) {
                gridElement.setBorderColor("red");
                break;
            }
        }
    }

    private void setSolveButtonAction() {
        Speed speed = getSettingsHandler().getSpeed();

        if (speed == Speed.QUICK) {
            solveButton.setOnAction(event -> {

                solveButton.setDisable(true);
                settingsButton.setDisable(true);
                IndependentGridSolver gridSolver = new IndependentGridSolver(this.grid);
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
                gridSolver.addObserver(this);

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

    private void finishFillingIn(Scene scene, boolean fill) {
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

            Mode mode = getSettingsHandler().getMode();
            if (mode == Mode.JIGSAW) {

                this.grid = new Grid(mode);
                for (GridElement gridElement : gridElements) {
                    String color = gridElement.getBackgroundColor();
                    int index = ColorTable.getInstance().get(color);

                    Square square = gridElement.getSquare();

                    grid.addSquare(square, index);
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
        for (GridElement gridElement : gridElements) {
            if (gridElement.getSquare().hasNoValue()) {
                gridElement.update(null, true);
            }
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
            }

            setKeyAction(scene, fill);
            flipButton(fill);
        };

        EventHandler<ActionEvent> cancelEventHandler = event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                clearSquares(fill);

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
                }
                else {
                    paintButton.setDisable(false);
                    unPaintButton.setDisable(true);
                    painted = false;
                    colorButtons.resetAll();
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

    private void colorGrid() {
        for (GridElement gridElement : gridElements) {
            Square square = gridElement.getSquare();

            if (grid.diagonalModeEnabledAndOnDiagonal(square)) {
                gridElement.setBackgroundColor("lightgrey");
            }
            else {
                gridElement.setBackgroundColor("white");
            }
        }

        Mode mode = getSettingsHandler().getMode();
        painted = !(mode == Mode.JIGSAW);
    }
}
