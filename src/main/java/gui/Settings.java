package gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.SettingsHandler;

import java.util.Observable;

import static gui.Sudoku.getSettingsHandler;

class Settings extends Observable {

    void build() {
        SettingsHandler handler = getSettingsHandler();

        Button okButton = new Button();
        okButton.setFont(new Font(15));
        okButton.setText("OK");
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

        if (handler.getMode().equals("quick")) {
            intervalSlider.setDisable(true);
            pauseSlider.setDisable(true);
        }

        CheckBox checkBox = new CheckBox();
        checkBox.setText("Slow mode");
        checkBox.setPrefHeight(40);
        checkBox.setFont(new Font(18));
        if (handler.getMode().equals("slow")) {
            checkBox.setSelected(true);
        }

        Text description1 = new Text();
        description1.setText("The amount of milliseconds between each step");
        description1.setFont(new Font(18));

        Label description2 = new Label();
        description2.setText("The amount of seconds to pause after solving a red square");
        description2.setFont(new Font(18));
        description2.setPadding(new Insets(50, 0, 0, 0));

        Button line1 = new Button();
        line1.setPrefSize(300, 1);
        line1.setFont(new Font(0));

        Button line2 = new Button();
        line2.setPrefSize(300, 1);
        line2.setFont(new Font(0));

        Button filler = new Button();
        filler.setVisible(false);
        filler.setPrefSize(0, 0);
        filler.setFont(new Font(0));
        filler.setPadding(new Insets(50, 0, 0, 0));

        GridPane gridPane = new GridPane();
        gridPane.add(description1, 0, 0);
        gridPane.add(intervalSlider, 0, 1);
        gridPane.add(line1, 0, 2);
        gridPane.add(description2, 0, 3);
        gridPane.add(pauseSlider, 0, 4);
        gridPane.add(line2, 0, 5);
        gridPane.add(filler, 0, 6);
        gridPane.add(checkBox, 0, 7);
        gridPane.add(okButton, 1, 8);
        gridPane.setPadding(new Insets(20));
        gridPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("images/icon.png"));

        Platform.runLater(gridPane :: requestFocus);

        okButton.setOnAction(event -> {
            handler.setInterval((int) intervalSlider.getValue());
            handler.setPause((int) (pauseSlider.getValue() * 1000));
            if (checkBox.isSelected()) {
                handler.setMode("slow");
            } else {
                handler.setMode("quick");
            }

            handler.updateFile();
            setChanged();
            notifyObservers();

            stage.close();
        });

        checkBox.setOnAction(event -> {
            intervalSlider.setDisable(!checkBox.isSelected());
            pauseSlider.setDisable(!checkBox.isSelected());
        });

        stage.showAndWait();
    }
}
