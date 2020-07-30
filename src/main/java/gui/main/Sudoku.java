package gui.main;

import core.misc.SettingsHandler;
import core.misc.options.Mode;
import core.structure.Grid;
import core.structure.Square;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Sudoku {

    Grid grid;
    List<GridElement> gridElements;
    Scene scene;

    BuilderComponent builderComponent;
    EventComponent eventComponent;
    FillingComponent fillingComponent;
    ObserverComponent observerComponent;

    public Sudoku(Stage stage) {
        Mode mode = SettingsHandler.getInstance().getMode();
        this.grid = new Grid(mode);
        this.gridElements = new ArrayList<>();

        scene = new Scene(new Region());


        this.observerComponent = new ObserverComponent(this);

        this.fillingComponent = new FillingComponent(this);

        this.eventComponent = new EventComponent(this);
        fillingComponent.setEventComponent(eventComponent);
        this.eventComponent.delayedConstruction();

        this.builderComponent = new BuilderComponent(this);
        scene.setRoot(builderComponent.getBorderPane());


        stage.setScene(scene);
        stage.setTitle("Sudoku solver");
        stage.getIcons().add(new Image("/images/icon.png"));

        scene.getRoot().requestFocus();

        stage.show();
    }

    void colorGrid() {
        for (GridElement gridElement : gridElements) {
            Square square = gridElement.getSquare();

            if (grid.diagonalModeEnabledAndOnDiagonal(square)) {
                gridElement.setBackgroundColor("lightgrey");
            }
            else {
                gridElement.setBackgroundColor("white");
            }
        }

        Mode mode = SettingsHandler.getInstance().getMode();
        grid.setPainted(mode != Mode.JIGSAW);
    }
}
