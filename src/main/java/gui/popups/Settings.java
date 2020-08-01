package gui.popups;

import core.misc.SettingsHandler;
import core.misc.options.InputMethod;
import core.misc.options.Mode;
import core.misc.options.Speed;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

public class Settings extends Observable {

    public void build() {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        ChoiceBox<String> modeChoiceBox = new ChoiceBox<>();
        modeChoiceBox.setValue(settingsHandler.getMode().toString().toLowerCase());
        modeChoiceBox.setPrefHeight(40);
        modeChoiceBox.setPrefWidth(100);
        modeChoiceBox.setStyle("-fx-font-size: 15");

        List<String> modes = Arrays .stream(Mode.values())
                                    .map(Enum::name)
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toList());
        modeChoiceBox.getItems().addAll(modes);

        Label modeLabel = new Label();
        modeLabel.setText("Mode:");
        modeLabel.setStyle("-fx-font-size: 18");
        modeLabel.setPrefWidth(120);

        HBox modeContainer = new HBox();
        modeContainer.setAlignment(Pos.CENTER_LEFT);
        modeContainer.setSpacing(40);
        modeContainer.getChildren().addAll(modeLabel, modeChoiceBox);


        ChoiceBox<String> inputMethodChoiceBox = new ChoiceBox<>();
        inputMethodChoiceBox.setValue(settingsHandler.getInputMethod().toString().toLowerCase());
        inputMethodChoiceBox.setPrefHeight(40);
        inputMethodChoiceBox.setPrefWidth(100);
        inputMethodChoiceBox.setStyle("-fx-font-size: 15");

        List<String> inputMethods = Arrays  .stream(InputMethod.values())
                                            .map(Enum::toString)
                                            .map(String::toLowerCase)
                                            .collect(Collectors.toList());
        inputMethodChoiceBox.getItems().addAll(inputMethods);

        Label inputMethodLabel = new Label();
        inputMethodLabel.setText("Input method:");
        inputMethodLabel.setStyle("-fx-font-size: 18");
        inputMethodLabel.setPrefWidth(120);

        HBox inputMethodContainer = new HBox();
        inputMethodContainer.setAlignment(Pos.CENTER_LEFT);
        inputMethodContainer.setSpacing(40);
        inputMethodContainer.setPadding(new Insets(20, 0, 20, 0));
        inputMethodContainer.getChildren().addAll(inputMethodLabel, inputMethodChoiceBox);


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
        intervalSlider.setValue(settingsHandler.getInterval());

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
        pauseSlider.setValue(settingsHandler.getPause() / 1000.0);

        CheckBox speedCheckBox = new CheckBox();
        speedCheckBox.setText("Slow mode");
        speedCheckBox.setPrefHeight(40);
        speedCheckBox.setFont(new Font(18));
        if (settingsHandler.getSpeed() == Speed.SLOW) {
            speedCheckBox.setSelected(true);
        }

        CheckBox confirmationsCheckBox = new CheckBox();
        confirmationsCheckBox.setText("Confirmations");
        confirmationsCheckBox.setPrefHeight(40);
        confirmationsCheckBox.setFont(new Font(18));
        if (settingsHandler.getConfirmations()) {
            confirmationsCheckBox.setSelected(true);
        }

        Label description1 = new Label();
        description1.setText("The amount of milliseconds between each step");
        description1.setFont(new Font(18));
        description1.setPadding(new Insets(20, 0, 0, 0));

        Label description2 = new Label();
        description2.setText("The amount of seconds to pause after solving a red square");
        description2.setFont(new Font(18));
        description2.setPadding(new Insets(20, 0, 0, 0));


        ArrayList<Node> list = new ArrayList<>();
        list.add(modeContainer);
        list.add(inputMethodContainer);
        list.add(createLine());
        list.add(createFiller());
        list.add(confirmationsCheckBox);
        list.add(createFiller());
        list.add(speedCheckBox);
        list.add(createFiller());
        list.add(createLine());
        list.add(description1);
        list.add(intervalSlider);
        list.add(createLine());
        list.add(description2);
        list.add(pauseSlider);

        GridPane gridPane = new GridPane();
        for (int i = 0; i < list.size(); i++) {
            gridPane.add(list.get(i), 0, i);
        }

        gridPane.add(okButton, 1, list.size());
        gridPane.setPadding(new Insets(20));
        gridPane.setAlignment(Pos.CENTER);

        setCheckboxVisibility(gridPane, speedCheckBox);

        Scene scene = new Scene(gridPane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("images/icon.png"));

        okButton.setOnAction(event -> {
            settingsHandler.setInterval((int) intervalSlider.getValue());
            settingsHandler.setPause((int) (pauseSlider.getValue() * 1000));
            settingsHandler.setConfirmations(confirmationsCheckBox.isSelected());
            if (speedCheckBox.isSelected()) {
                settingsHandler.setSpeed(Speed.SLOW);
            } else {
                settingsHandler.setSpeed(Speed.QUICK);
            }
            settingsHandler.setMode(Mode.valueOf(modeChoiceBox.getValue().toUpperCase()));
            settingsHandler.setInputMethod(InputMethod.valueOf(inputMethodChoiceBox.getValue().toUpperCase()));

            settingsHandler.updateFile();
            setChanged();
            notifyObservers();

            stage.close();
        });

        speedCheckBox.setOnAction(event -> setCheckboxVisibility(gridPane, speedCheckBox));

        gridPane.requestFocus();
        for (Node node : modeContainer.getChildren()) {
            node.setOnMousePressed(event -> gridPane.requestFocus());
        }
        for (Node node : inputMethodContainer.getChildren()) {
            node.setOnMousePressed(event -> gridPane.requestFocus());
        }
        for (Node node : gridPane.getChildren()) {
            node.setOnMousePressed(event -> gridPane.requestFocus());
        }

        stage.showAndWait();
    }

    private void setCheckboxVisibility(GridPane gridPane, CheckBox checkBox) {
        boolean checked = checkBox.isSelected();
        ObservableList<Node> children = gridPane.getChildren();

        int start = GridPane.getRowIndex(checkBox) + 1;
        int end = children.size() - 1;

        for (int i = start; i < end; i++) {
            children.get(i).setVisible(checked);
        }
    }

    private static Button createFiller() {
        Button filler = new Button();
        filler.setVisible(false);
        filler.setPrefSize(0, 0);
        filler.setFont(new Font(0));
        filler.setPadding(new Insets(20, 0, 0, 0));

        return filler;
    }

    private static Button createLine() {
        Button line = new Button();
        line.setPrefSize(300, 1);
        line.setFont(new Font(0));

        return line;
    }
}
