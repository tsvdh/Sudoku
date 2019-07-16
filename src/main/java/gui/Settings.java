package gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.SettingsHandler;

import static gui.Sudoku.getSettingsHandler;

class Settings {

    Settings() {
        SettingsHandler handler = getSettingsHandler();

        Button okButton = new Button();
        okButton.setFont(new Font(15));
        okButton.setText("OK");
        okButton.setOnAction(event -> {

        });
        okButton.setPrefSize(100, 40);

        Slider intervalSlider = new Slider();
        intervalSlider.setBlockIncrement(5);
        intervalSlider.setMin(10);
        intervalSlider.setMax(100);
        intervalSlider.setPadding(new Insets(20));
        intervalSlider.setShowTickLabels(true);
        intervalSlider.setShowTickMarks(true);
        intervalSlider.setPrefSize(500, 40);
        intervalSlider.setMajorTickUnit(10);
        intervalSlider.setMinorTickCount(1);
        intervalSlider.setSnapToTicks(true);
        intervalSlider.setValue(handler.getInterval());

        Slider pauseSlider = new Slider();
        pauseSlider.setBlockIncrement(0.5);
        pauseSlider.setMin(0);
        pauseSlider.setMax(5);
        pauseSlider.setPadding(new Insets(20));
        pauseSlider.setShowTickMarks(true);
        pauseSlider.setShowTickLabels(true);
        pauseSlider.setPrefSize(500, 40);
        pauseSlider.setMajorTickUnit(1);
        pauseSlider.setMinorTickCount(1);
        pauseSlider.setSnapToTicks(true);
        pauseSlider.setValue(handler.getPause() / 1000);

        ToggleButton toggleButton = new ToggleButton();


        GridPane gridPane = new GridPane();
        gridPane.add(intervalSlider, 0, 0);
        gridPane.add(pauseSlider, 0, 1);
        gridPane.add(toggleButton, 0, 2);
        gridPane.add(okButton, 1, 3);
        gridPane.setPadding(new Insets(20));
        gridPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.initModality(Modality.APPLICATION_MODAL);

        Platform.runLater(gridPane :: requestFocus);

        okButton.setOnAction(event -> {
            handler.setInterval((int) intervalSlider.getValue());
            handler.setPause((int) (pauseSlider.getValue() * 1000));

            handler.updateFile();
            stage.close();
        });

        stage.showAndWait();
    }
}
