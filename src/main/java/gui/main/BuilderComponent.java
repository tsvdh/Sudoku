package gui.main;

import core.misc.SettingsHandler;
import core.misc.options.Mode;
import core.misc.options.Speed;
import core.structure.Grid;
import core.structure.Square;
import gui.buttons.ColorButtons;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import static gui.buttons.ButtonFactory.makeButton;
import static gui.buttons.ButtonFactory.makeInvisibleButton;

class BuilderComponent {

    private Sudoku parent;
    private EventComponent eventComponent;

    private BorderPane borderPane;

    private final static SettingsHandler settingsHandler = SettingsHandler.getInstance();

    BuilderComponent(Sudoku parent) {
        if (parent.eventComponent == null) {
            throw new IllegalStateException("Parent Sudoku must have an EventComponent called 'eventComponent'.");
        }

        this.parent = parent;
        this.eventComponent = parent.eventComponent;

        this.buildGrid();

        HBox hBoxTop = new HBox();
        hBoxTop.setPadding(new Insets(10, 10, 50 ,10));
        hBoxTop.getChildren().addAll(eventComponent.getTopButtons());
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setSpacing(40);
        borderPane.setTop(hBoxTop);

        for (Node node : hBoxTop.getChildren()) {
            node.setOnMousePressed(event -> hBoxTop.requestFocus());
        }

        setSpecificButtons();
    }

    BorderPane getBorderPane() {
        return borderPane;
    }

    private void buildGrid() {
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 50, 10 ,50));
        borderPane.setStyle("-fx-background-color: white");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        //gridPane.setStyle("-fx-background-color: black;");
        borderPane.setCenter(gridPane);

        for (int y = 1; y <= 17; y += 2) {
            for (int x = 1; x <= 17; x += 2) {
                Square square = new Square(null, x / 2 + 1, y / 2 + 1);
                parent.grid.addSquare(square);

                GridElement gridElement = new GridElement(square);

                gridPane.add(gridElement, x , y);
                parent.gridElements.add(gridElement);
            }
        }

        addLines();
        parent.colorGrid();
    }

    void rebuildGrid() {
        Mode newMode = settingsHandler.getMode();
        if (!newMode.equals(settingsHandler.getOldMode())) {

            parent.grid = new Grid(newMode);
            for (GridElement gridElement : parent.gridElements) {
                Square square = gridElement.getSquare();
                parent.grid.addSquare(square);
            }

            addLines();
            parent.colorGrid();
        }
    }

    private void addLines() {
        GridPane gridPane = (GridPane) borderPane.getCenter();
        float width = 0.5f;
        if (settingsHandler.getMode() == Mode.JIGSAW) {
            width = 0;
        }
        String style = "-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: black;"
                + "-fx-border-width: " + width + "px;";

        for (int y = 1; y <= 17; y += 2) {
            for (int x = 0; x <= 18; x += 6) {
                Button verticalLine = (Button) getFromGridPane(gridPane, x, y);

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
                Button horizontalLine = (Button) getFromGridPane(gridPane, x, y);

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

    private Node getFromGridPane(GridPane gridPane, int column, int row) {
        for (Node element : gridPane.getChildren()) {
            if (GridPane.getRowIndex(element) == row && GridPane.getColumnIndex(element) == column) {
                return element;
            }
        }

        return null;
    }

    void setSpecificButtons() {
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


        Speed speed = settingsHandler.getSpeed();
        Mode mode = settingsHandler.getMode();

        if (speed == Speed.SLOW) {
            topHBox.getChildren().add(eventComponent.pauseButton);
        }
        else {
            topHBox.getChildren().add(makeInvisibleButton());
        }

        Button paintButton = eventComponent.paintButton;
        Button unPaintButton = eventComponent.unPaintButton;
        if (mode == Mode.JIGSAW) {
            topHBox.getChildren().addAll(paintButton, unPaintButton);
            ColorButtons colorButtons = eventComponent.colorButtons;
            colorButtons.disableAll();
            bottomHBox.getChildren().addAll(colorButtons.getButtons());

            if (eventComponent.painted) {
                paintButton.setDisable(true);
                unPaintButton.setDisable(false);
            }
            else {
                paintButton.setDisable(false);
                unPaintButton.setDisable(true);
            }
        }
        else {
            bottomHBox.getChildren().add(makeInvisibleButton());
        }

        for (Node node : topHBox.getChildren()) {
            node.setOnMousePressed(event -> topHBox.requestFocus());
        }
    }
}
