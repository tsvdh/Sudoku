package gui.main;

import core.misc.SettingsHandler;
import core.misc.SettingsPossibilities.Mode;
import core.structure.Grid;
import core.structure.Square;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class Sudoku {

    Grid grid;
    List<GridElement> gridElements;
    Mode oldMode;

    BuilderComponent builderComponent;
    EventComponent eventComponent;
    IterationComponent iterationComponent;
    ObserverComponent observerComponent;

    private final static SettingsHandler settingsHandler = SettingsHandler.getInstance();

    public Sudoku(Stage stage) {
        Mode mode = settingsHandler.getMode();
        this.grid = new Grid(mode);
        this.gridElements = new LinkedList<>();
        this.oldMode = mode;

        Scene scene = new Scene(new Region());


        this.observerComponent = new ObserverComponent(this);

        this.eventComponent = new EventComponent(this, scene);

        this.builderComponent = new BuilderComponent(this);
        scene.setRoot(builderComponent.getBorderPane());

        this.iterationComponent = new IterationComponent(this);


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

        Mode mode = settingsHandler.getMode();
        eventComponent.painted = !(mode == Mode.JIGSAW);
    }
}