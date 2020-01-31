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
import utilities.ColorTable;
import utilities.Grid;
import utilities.IndependentGridSolver;
import utilities.LinkedGridSolver;
import utilities.OverrideException;
import utilities.SettingsHandler;
import utilities.Square;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import static gui.ButtonFactory.makeButton;
import static gui.ButtonFactory.makeColorButtons;
import static javafx.scene.input.KeyCode.DIGIT0;

public class Sudoku extends Application implements Observer {

    private Grid grid;
    private List<GridElement> gridElements;
    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;
    private String lastMove;
    private BorderPane borderPane;
    private String oldMode;
    private boolean filled;
    private boolean painted;

    private Button clearButton;
    private Button fillInButton;
    private Button solveButton;
    private Button settingsButton;
    private Button pauseButton;
    private Button paintButton;
    private Button unPaintButton;

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

        String mode = getSettingsHandler().getMode();
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

        /*given = new Integer[9][9];
        given[0] = new Integer[]{0, 4, 6, 0, 0, 5, 0, 0, 0};
        given[1] = new Integer[]{0, 2, 0, 3, 0, 0, 0, 0, 0};
        given[2] = new Integer[]{9, 0, 0, 0, 4, 0, 0, 0, 7};
        given[3] = new Integer[]{0, 0, 0, 0, 0, 7, 0, 0, 9};
        given[4] = new Integer[]{8, 0, 1, 0, 0, 0, 4, 0, 3};
        given[5] = new Integer[]{3, 0, 0, 6, 0, 0, 0, 0, 0};
        given[6] = new Integer[]{7, 0, 0, 0, 2, 0, 0, 0, 6};
        given[7] = new Integer[]{0, 0, 0, 0, 0, 6, 0, 5, 0};
        given[8] = new Integer[]{0, 0, 0, 9, 0, 0, 7, 3, 0};*/

        buildGrid();

        clearButton = makeButton("Clear");
        fillInButton = makeButton("Fill in");
        solveButton = makeButton("Solve");
        settingsButton = makeButton("Settings");
        pauseButton = makeButton("Pause");
        paintButton = makeButton("Paint");
        unPaintButton = makeButton("Clear");

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


        String speed = getSettingsHandler().getSpeed();
        String mode = getSettingsHandler().getMode();

        if (!speed.equals("slow") && !mode.equals("jigsaw")) {
            Button filler = makeButton("Invisible");
            filler.setVisible(false);
            topHBox.getChildren().addAll(filler);
        }

        if (speed.equals("slow")) {
            topHBox.getChildren().addAll(pauseButton);
        }

        if (mode.equals("jigsaw")) {
            topHBox.getChildren().addAll(paintButton, unPaintButton);
            bottomHBox.getChildren().addAll(makeColorButtons());
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

                /*Integer value = given[y/ 2][x / 2];
                if (value == 0) {
                    value = null;
                }
                try {
                    square.setValue(value);
                } catch (OverrideException e) {
                    System.out.println(e.getMessage());
                }*/
            }
        }

        addLines();

        colorGrid();
    }

    private void rebuildGrid() {
        String newMode = getSettingsHandler().getMode();
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
        if (getSettingsHandler().getMode().equals("jigsaw")) {
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

    private void setKeyAction(Scene scene, boolean fill) {
        elementIterator = gridElements.listIterator();
        goToNextElement();

        keyEventHandler = event -> {

            currentElement.setBorderColor("black");

            KeyCode keyCode = event.getCode();

            if (keyCode.isDigitKey() || keyCode.isWhitespaceKey()) {

                if (!keyCode.isWhitespaceKey()) {
                    Integer number = new Integer(keyCode.getName());
                    if (number != 0) {

                        if (fill) {
                            try {
                                currentElement.getSquare().setValue(number);
                            } catch (OverrideException e) {
                                System.out.println(e.getMessage());
                            }

                        } else {
                            String color = ColorTable.getColors()[number - 1];
                            currentElement.setBackgroundColor(color);
                        }
                    }
                }

                if (!(keyCode.isWhitespaceKey() || keyCode == DIGIT0) || fill) {
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
                else {
                    currentElement.setBorderColor("lightgreen");
                }
            }

            else if (keyCode.equals(KeyCode.BACK_SPACE) || keyCode.equals(KeyCode.DELETE)) {
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
                        currentElement.setBackgroundColor("white");
                    }

                    lastMove = "previous";

                } else {
                    currentElement.setBorderColor("lightgreen");
                }
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
        String speed = getSettingsHandler().getSpeed();

        if (speed.equals("quick")) {
            solveButton.setOnAction(event -> {

                solveButton.setDisable(true);
                settingsButton.setDisable(true);
                IndependentGridSolver gridSolver = new IndependentGridSolver(this.grid);
                showOptionsOfAllSquares();
                gridSolver.solve();
            });
        }

        if (speed.equals("slow")) {
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

            this.grid = new Grid(oldMode);
            for (GridElement gridElement : gridElements) {
                String color = gridElement.getBackgroundColor();
                int index = ColorTable.getInstance().get(color);

                Square square = gridElement.getSquare();

                grid.addSquare(square, index);
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
    }
}
