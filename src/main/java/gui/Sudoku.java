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
    private EventHandler<KeyEvent> keyEventHandler;
    private EventHandler<ActionEvent> fillInActionEventHandler;
    private EventHandler<ActionEvent> cancelActionEventHandler;

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

        this.grid = new Grid();
        this.gridElements = new LinkedList<>();
        this.lastMove = "none";


        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: black;");

        buildGrid(gridPane);


        clearButton = new Button();
        clearButton.setFont(new Font(20));
        clearButton.setPrefSize(100, 30);
        clearButton.setText("Clear");
        clearButton.setDisable(true);

        fillInButton = new Button();
        fillInButton.setFont(new Font(20));
        fillInButton.setPrefSize(100, 30);
        fillInButton.setText("Fill in");

        solveButton = new Button();
        solveButton.setFont(new Font(20));
        solveButton.setPrefSize(100, 30);
        solveButton.setText("Solve");
        solveButton.setDisable(true);

        settingsButton = new Button();
        settingsButton.setFont(new Font(20));
        settingsButton.setPrefSize(100, 30);
        settingsButton.setText("Settings");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 50 ,10));
        hBox.getChildren().addAll(clearButton,
                                fillInButton,
                                solveButton,
                                settingsButton);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(40);


        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setTop(hBox);
        borderPane.setPadding(new Insets(10, 50, 50 ,50));
        borderPane.setStyle("-fx-background-color: white");

        Scene scene = new Scene(borderPane);

        constructActionEventHandler(scene);

        fillInButton.setOnAction(fillInActionEventHandler);

        clearButton.setOnAction(event -> {
            String result = new Confirmation().getResult();

            if (result.equals("yes")) {
                clearSquares();
                solveButton.setDisable(true);
                clearButton.setDisable(true);
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

        hBox.requestFocus();
        for (Node node : hBox.getChildren()) {
            node.setOnMousePressed(event -> hBox.requestFocus());
        }

        stage.show();
    }

    private void buildGrid(GridPane gridPane) {
        for (int y = 1; y <= 17; y += 2) {
            for (int x = 1; x <= 17; x += 2) {
                Square square = new Square(null, x / 2 + 1, y / 2 + 1);
                grid.addSquare(square);

                GridElement gridElement = new GridElement();
                gridElement.setSquare(square);

                gridPane.add(gridElement, x , y);
                gridElements.add(gridElement);
            }
        }

        addLines(gridPane);
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
        currentElement.setStyleAndColor("lightgreen");
    }

    private void goToPreviousElement() {
        currentElement = elementIterator.previous();
        currentElement.setStyleAndColor("lightgreen");
    }

    private void setKeyAction(Scene scene) {
        elementIterator = gridElements.listIterator();
        goToNextElement();

        keyEventHandler = event -> {

            currentElement.setStyleAndColor("black");

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
                    currentElement.setStyleAndColor("lightgreen");
                }
            }

            else {
                currentElement.setStyleAndColor("lightgreen");
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
            gridElement.setStyleAndColor("black");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String status = (String) arg;

        if (o instanceof LinkedGridSolver) {
            if (status.equals("done")) {
                clearButton.setDisable(false);
                fillInButton.setDisable(false);
                settingsButton.setDisable(false);
            }
            if (status.equals("working")) {
                highLightFirstPair();
            }
        }

        if (o instanceof Settings) {
            setSolveButtonAction();
        }
    }

    private void highLightFirstPair() {
        for (GridElement gridElement : gridElements) {
            Square square = gridElement.getSquare();
            if (square.isPair()) {
                gridElement.setStyleAndColor("red");
                break;
            }
        }
    }

    private void setSolveButtonAction() {
        String mode = getSettingsHandler().getMode();

        if (mode.equals("quick")) {
            solveButton.setOnAction(event -> {

                solveButton.setDisable(true);
                IndependentGridSolver gridSolver = new IndependentGridSolver(this.grid);
                showOptionsOfAllSquares();
                gridSolver.solve();
            });
        }

        if (mode.equals("slow")) {
            solveButton.setOnAction(event -> {

                solveButton.setDisable(true);
                fillInButton.setDisable(true);
                clearButton.setDisable(true);
                settingsButton.setDisable(true);

                LinkedGridSolver gridSolver = new LinkedGridSolver(this.grid);
                gridSolver.addObserver(this);

                showOptionsOfAllSquares();
                gridSolver.solve();
            });
        }
    }

    private void finishFillingIn(Scene scene) {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        clearButton.setDisable(false);
        fillInButton.setDisable(false);
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

            clearSquares();
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
}
