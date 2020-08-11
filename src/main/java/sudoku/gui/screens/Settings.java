package sudoku.gui.screens;

import common.SettingsHandler;
import common.options.BuildVersion;
import common.options.InputMethod;
import common.options.Mode;
import common.options.Speed;
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
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Settings extends Observable {

    public void build() {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        Pair<ChoiceBox<String>, HBox> pair = createChoiceBoxContainer(SettingsHandler::getMode);
        ChoiceBox<String> modeChoiceBox = pair.getValue0();
        HBox modeContainer = pair.getValue1();

        pair = createChoiceBoxContainer(SettingsHandler::getInputMethod);
        ChoiceBox<String> inputMethodChoiceBox = pair.getValue0();
        HBox inputMethodContainer = pair.getValue1();

        pair = createChoiceBoxContainer(SettingsHandler::getBuildVersion);
        ChoiceBox<String> buildVersionChoiceBox = pair.getValue0();
        HBox buildVersionContainer = pair.getValue1();

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
        list.add(buildVersionContainer);
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
            settingsHandler.setBuildVersion(BuildVersion.valueOf(buildVersionChoiceBox.getValue().toUpperCase()));

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

    private static <T extends Enum<T>> Pair<ChoiceBox<String>, HBox> createChoiceBoxContainer(Function<SettingsHandler, T> getter) {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        String name = getter.apply(settingsHandler).toString().toLowerCase();

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setValue(name);
        choiceBox.setPrefHeight(40);
        choiceBox.setPrefWidth(100);
        choiceBox.setStyle("-fx-font-size: 15");

        T[] values = getter.apply(settingsHandler).getDeclaringClass().getEnumConstants();

        List<String> modes = Arrays.stream(values)
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        choiceBox.getItems().addAll(modes);

        Label modeLabel = new Label();
        modeLabel.setText(name + ":");
        modeLabel.setStyle("-fx-font-size: 18");
        modeLabel.setPrefWidth(120);

        HBox containter = new HBox();
        containter.setAlignment(Pos.CENTER_LEFT);
        containter.setSpacing(40);
        containter.getChildren().addAll(modeLabel, choiceBox);

        return Pair.with(choiceBox, containter);
    }
}
