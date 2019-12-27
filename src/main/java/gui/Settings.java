package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utilities.SettingsHandler;

import java.util.Observable;

import static gui.Sudoku.getSettingsHandler;

class Settings extends Observable {

    void build() {
        SettingsHandler handler = getSettingsHandler();

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setValue(handler.getMode());
        choiceBox.setPrefHeight(40);
        choiceBox.setPrefWidth(100);
        choiceBox.getItems().addAll("normal", "diagonal", "jigsaw");
        choiceBox.setStyle("-fx-font-size: 15");

        Button okButton = new Button();
        okButton.setFont(new Font(15));
        okButton.setText("OK");
        okButton.setPrefSize(100, 40);

        Slider intervalSlider = new Slider();
        intervalSlider.setBlockIncrement(10);
        intervalSlider.setMin(40);
        intervalSlider.setMax(200);
        intervalSlider.setPadding(new Insets(20));
        intervalSlider.setShowTickLabels(true);
        intervalSlider.setShowTickMarks(true);
        intervalSlider.setPrefSize(500, 40);
        intervalSlider.setMajorTickUnit(20);
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
        pauseSlider.setValue(handler.getPause() / 1000.0);

        CheckBox checkBox = new CheckBox();
        checkBox.setText("Slow mode");
        checkBox.setPrefHeight(40);
        checkBox.setFont(new Font(18));
        if (handler.getSpeed().equals("slow")) {
            checkBox.setSelected(true);
        }

        Label description1 = new Label();
        description1.setText("The amount of milliseconds between each step");
        description1.setFont(new Font(18));
        description1.setPadding(new Insets(20, 0, 0, 0));

        Label description2 = new Label();
        description2.setText("The amount of seconds to pause after solving a red square");
        description2.setFont(new Font(18));
        description2.setPadding(new Insets(20, 0, 0, 0));

        Button line1 = new Button();
        line1.setPrefSize(300, 1);
        line1.setFont(new Font(0));

        Button line2 = new Button();
        line2.setPrefSize(300, 1);
        line2.setFont(new Font(0));

        Button filler1 = createFiller();
        Button filler2 = createFiller();


        GridPane gridPane = new GridPane();
        gridPane.add(choiceBox, 0, 0);
        gridPane.add(filler1, 0, 1);
        gridPane.add(checkBox, 0, 2);
        gridPane.add(filler2, 0, 3);
        gridPane.add(line1, 0, 4);
        gridPane.add(description1, 0, 5);
        gridPane.add(intervalSlider, 0, 6);
        gridPane.add(line2, 0, 7);
        gridPane.add(description2, 0, 8);
        gridPane.add(pauseSlider, 0, 9);

        setCheckboxVisibility(gridPane, checkBox);

        gridPane.add(okButton, 1, 10);
        gridPane.setPadding(new Insets(20));
        gridPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("images/icon.png"));

        okButton.setOnAction(event -> {
            handler.setInterval((int) intervalSlider.getValue());
            handler.setPause((int) (pauseSlider.getValue() * 1000));
            if (checkBox.isSelected()) {
                handler.setSpeed("slow");
            } else {
                handler.setSpeed("quick");
            }
            handler.setMode(choiceBox.getValue());

            handler.updateFile();
            setChanged();
            notifyObservers();

            stage.close();
        });

        checkBox.setOnAction(event -> setCheckboxVisibility(gridPane, checkBox));

        gridPane.requestFocus();
        for (Node node : gridPane.getChildren()) {
            node.setOnMousePressed(event -> gridPane.requestFocus());
        }

        stage.showAndWait();
    }

    private void setCheckboxVisibility(GridPane gridPane, CheckBox checkBox) {
        boolean checked = checkBox.isSelected();
        int start = 3;
        int end = 9;
        int counter = 0;

        for (Node node : gridPane.getChildren()) {

            if (counter >= start && counter <= end) {
                node.setVisible(checked);
            }

            counter++;
        }
    }

    private Button createFiller() {
        Button filler = new Button();
        filler.setVisible(false);
        filler.setPrefSize(0, 0);
        filler.setFont(new Font(0));
        filler.setPadding(new Insets(20, 0, 0, 0));

        return filler;
    }
}
