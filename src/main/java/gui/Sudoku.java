package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.Grid;
import utilities.OverrideException;
import utilities.Square;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Sudoku extends Application {

    private Grid grid;
    private List<GridElement> gridElements;
    private ListIterator<GridElement> elementIterator;
    private GridElement currentElement;

    public Grid getGrid() {
        return this.grid;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.grid = new Grid();
        this.gridElements = new LinkedList<>();


        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: black;");

        buildGrid(gridPane);


        Button clearButton = new Button();
        clearButton.setFont(new Font(20));
        clearButton.setPrefSize(100, 30);
        clearButton.setText("Clear");

        Button fillInButton = new Button();
        fillInButton.setFont(new Font(20));
        fillInButton.setPrefSize(100, 30);
        fillInButton.setText("Fill in");

        Button solveButton = new Button();
        solveButton.setFont(new Font(20));
        solveButton.setPrefSize(100, 30);
        solveButton.setText("Solve");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 50 ,10));
        hBox.getChildren().addAll(clearButton,
                                fillInButton,
                                solveButton);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(40);


        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setTop(hBox);
        borderPane.setPadding(new Insets(10, 50, 50 ,50));
        borderPane.setStyle("-fx-background-color: white");

        Platform.runLater(borderPane :: requestFocus);

        Scene scene = new Scene(borderPane);

        fillInButton.setOnAction(event -> {
            setKeyAction(scene);
        });

        clearButton.setOnAction(event -> {

        });

        stage.setScene(scene);
        stage.setTitle("Sudoku");
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

        scene.setOnKeyPressed(event -> {
            currentElement.setStyleAndColor("black");

            KeyCode keyCode = event.getCode();

            if (keyCode.isDigitKey()) {
                Integer number = new Integer(keyCode.getName());

                if (number != 0) {
                    try {
                        currentElement.getSquare().setValue(number);
                    } catch (OverrideException e) {
                        System.out.println(e.getMessage());
                    }
                }

                if (elementIterator.hasNext()) {
                    goToNextElement();
                } else {
                    scene.setOnKeyPressed(null);
                }
            }

            if (keyCode.equals(KeyCode.BACK_SPACE)) {
                if (elementIterator.hasPrevious()) {
                    goToPreviousElement();
                    try {
                        currentElement.getSquare().setValue(null);
                    } catch (OverrideException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    currentElement.setStyleAndColor("lightgreen");
                }
            }
        });
    }
}
