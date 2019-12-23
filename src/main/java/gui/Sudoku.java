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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.Grid;
import utilities.GridSolver;
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

public class Sudoku extends Application implements Observer {

    private Grid grid;
    private List<GridElement> gridElements;
    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;
    private String lastMove;
    private Button clearButton;
    private Button fillInButton;
    private Button solveButton;
    private Button settingsButton;
    private Button pauseButton;
    private BorderPane borderPane;
    private EventHandler<KeyEvent> keyEventHandler;
    private EventHandler<ActionEvent> fillInActionEventHandler;
    private EventHandler<ActionEvent> cancelActionEventHandler;
    private String oldMode;
    private Integer[][] given;

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


        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: black;");

        given = new Integer[9][9];
        given[0] = new Integer[]{0, 4, 6, 0, 0, 5, 0, 0, 0};
        given[1] = new Integer[]{0, 2, 0, 3, 0, 0, 0, 0, 0};
        given[2] = new Integer[]{9, 0, 0, 0, 4, 0, 0, 0, 7};
        given[3] = new Integer[]{0, 0, 0, 0, 0, 7, 0, 0, 9};
        given[4] = new Integer[]{8, 0, 1, 0, 0, 0, 4, 0, 3};
        given[5] = new Integer[]{3, 0, 0, 6, 0, 0, 0, 0, 0};
        given[6] = new Integer[]{7, 0, 0, 0, 2, 0, 0, 0, 6};
        given[7] = new Integer[]{0, 0, 0, 0, 0, 6, 0, 5, 0};
        given[8] = new Integer[]{0, 0, 0, 9, 0, 0, 7, 3, 0};

        buildGrid(gridPane);


        clearButton = makeButton("Clear");
        fillInButton = makeButton("Fill in");
        solveButton = makeButton("Solve");
        settingsButton = makeButton("Settings");
        pauseButton = makeButton("Pause");

        clearButton.setDisable(true);
        //solveButton.setDisable(true);
        pauseButton.setDisable(true);


        HBox hBoxTop = new HBox();
        hBoxTop.setPadding(new Insets(10, 10, 50 ,10));
        hBoxTop.getChildren().addAll(clearButton,
                                fillInButton,
                                solveButton,
                                settingsButton);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setSpacing(40);


        borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setTop(hBoxTop);
        borderPane.setPadding(new Insets(10, 50, 10 ,50));
        borderPane.setStyle("-fx-background-color: white");

        setSpecificButtons();

        Scene scene = new Scene(borderPane);

        constructActionEventHandler(scene);

        fillInButton.setOnAction(fillInActionEventHandler);

        clearButton.setOnAction(event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                clearSquares();
                solveButton.setDisable(true);
                clearButton.setDisable(true);
                fillInButton.setDisable(false);
                settingsButton.setDisable(false);
            }
        });

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
        HBox hBoxBottom = new HBox();
        hBoxBottom.setPadding(new Insets(50, 10, 40, 10));
        hBoxBottom.setAlignment(Pos.CENTER);
        hBoxBottom.setSpacing(40);

        borderPane.setBottom(hBoxBottom);

        String speed = getSettingsHandler().getSpeed();
        if (speed.equals("slow")) {
            hBoxBottom.getChildren().addAll(pauseButton);

            for (Node node : hBoxBottom.getChildren()) {
                node.setOnMousePressed(event -> hBoxBottom.requestFocus());
            }
        }
        else {
            Button filler = makeButton("Invisible");
            filler.setVisible(false);

            hBoxBottom.getChildren().add(filler);
        }
    }

    private Button makeButton(String text) {
        Button button = new Button();
        button.setFont(new Font(20));
        button.setPrefSize(100, 30);
        button.setText(text);
        return button;
    }

    private void buildGrid(GridPane gridPane) {
        for (int y = 1; y <= 17; y += 2) {
            for (int x = 1; x <= 17; x += 2) {
                Square square = new Square(null, x / 2 + 1, y / 2 + 1);
                grid.addSquare(square);

                GridElement gridElement = new GridElement(square);

                gridPane.add(gridElement, x , y);
                gridElements.add(gridElement);

                Integer value = given[y/ 2][x / 2];
                if (value == 0) {
                    value = null;
                }
                try {
                    square.setValue(value);
                } catch (OverrideException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        addLines(gridPane);

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
    }

    private void addLines(GridPane gridPane) {
        String style = "-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 0.5px;";

        for (int y = 1; y <= 17; y += 2) {
            for (int x = 0; x <= 18; x += 6) {
                Button verticalLine = new Button();
                verticalLine.setPrefSize(0, GridElement.size);
                verticalLine.setStyle(style);
                verticalLine.setFont(new Font(0));

                gridPane.add(verticalLine, x, y);
            }
        }
        for (int x = 1; x <= 17; x += 2) {
            for (int y = 0; y <= 18; y += 6) {
                Button horizontalLine = new Button();
                horizontalLine.setPrefSize(GridElement.size, 1);
                horizontalLine.setStyle(style);
                horizontalLine.setFont(new Font(0));

                gridPane.add(horizontalLine, x, y);
            }
        }
    }

    private void goToNextElement() {
        currentElement = elementIterator.next();
        currentElement.setBorderColor("lightgreen");
    }

    private void goToPreviousElement() {
        currentElement = elementIterator.previous();
        currentElement.setBorderColor("lightgreen");
    }

    private void setKeyAction(Scene scene) {
        elementIterator = gridElements.listIterator();
        goToNextElement();

        keyEventHandler = event -> {

            currentElement.setBorderColor("black");

            KeyCode keyCode = event.getCode();

            if (keyCode.isDigitKey() || keyCode.isWhitespaceKey()) {

                if (!keyCode.isWhitespaceKey()) {
                    Integer number = new Integer(keyCode.getName());

                    if (number != 0) {
                        try {
                            currentElement.getSquare().setValue(number);
                        } catch (OverrideException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }

                if (elementIterator.hasNext()) {
                    if (lastMove.equals("previous")) {
                        currentElement = elementIterator.next();
                    }
                    goToNextElement();
                } else {

                    finishFillingIn(scene);
                }

                lastMove = "next";
            }

            else if (keyCode.equals(KeyCode.BACK_SPACE) || keyCode.equals(KeyCode.DELETE)) {
                if (elementIterator.hasPrevious()) {

                    if (lastMove.equals("next")) {
                        currentElement = elementIterator.previous();
                    }
                    goToPreviousElement();

                    try {
                        currentElement.getSquare().setValue(null);
                    } catch (OverrideException e) {
                        System.out.println(e.getMessage());
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

    private void clearSquares() {
        for (GridElement gridElement : gridElements) {
            try {
                gridElement.getSquare().setValue(null);
            } catch (OverrideException e) {
                System.out.println(e.getMessage());
            }
            gridElement.setBorderColor("black");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String status = (String) arg;

        if (o instanceof LinkedGridSolver) {
            if (status.equals("done")) {
                clearButton.setDisable(false);
                pauseButton.setDisable(true);
            }
            if (status.equals("magic")) {
                highLightFirstPair();
            }
        }

        if (o instanceof Settings) {
            setSolveButtonAction();

            rebuildGrid();
            setSpecificButtons();

            oldMode = getSettingsHandler().getMode();
        }
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
                pauseButton.setDisable(false);

                LinkedGridSolver gridSolver = new LinkedGridSolver(this.grid);
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

    private void finishFillingIn(Scene scene) {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        clearButton.setDisable(false);
        fillInButton.setDisable(true);
        settingsButton.setDisable(false);

        fillInButton.setText("Fill in");

        flipButtonAction();

        GridSolver solver = new IndependentGridSolver(grid);
        if (solver.isValid()) {
            solveButton.setDisable(false);
        } else {
            new Message("The sudoku you entered is invalid!");
        }
    }

    private void showOptionsOfAllSquares() {
        for (GridElement gridElement : gridElements) {
            if (gridElement.getSquare().hasNoValue()) {
                gridElement.update(null, true);
            }
        }
    }

    private void constructActionEventHandler(Scene scene) {
        fillInActionEventHandler = event -> {
            clearButton.setDisable(true);
            solveButton.setDisable(true);
            settingsButton.setDisable(true);

            setKeyAction(scene);
            fillInButton.setText("Cancel");
            flipButtonAction();
        };

        cancelActionEventHandler = event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                clearSquares();
                scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
                settingsButton.setDisable(false);
                fillInButton.setText("Fill in");
                flipButtonAction();
            }
        };
    }

    private void flipButtonAction() {
        EventHandler<ActionEvent> eventHandler = fillInButton.getOnAction();
        if (eventHandler.equals(fillInActionEventHandler)) {
            fillInButton.setOnAction(cancelActionEventHandler);
        } else {
            fillInButton.setOnAction(fillInActionEventHandler);
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
