package sudoku.gui.screens;

import common.SettingsHandler;
import common.StyleHandler;
import common.options.BuildVersion;
import common.options.InputMethod;
import common.options.Mode;
import common.options.Speed;
import common.options.UISkin;
import common.popups.Message;
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
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.javatuples.Pair;
import update.Launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.function.Function;
import java.util.stream.Collectors;

import static sudoku.gui.buttons.ButtonFactory.makeSmallButton;

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

        Button okButton = makeSmallButton("OK");

        Slider intervalSlider = new Slider();
        intervalSlider.setBlockIncrement(10);
        intervalSlider.setMin(40);
        intervalSlider.setMax(200);
        intervalSlider.setPadding(new Insets(0, 0, 0, 20));
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
        pauseSlider.setPadding(new Insets(0, 0, 0, 20));
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

        CheckBox uiSkinCheckBox = new CheckBox();
        uiSkinCheckBox.setText("Classic skin");
        uiSkinCheckBox.setPrefHeight(40);
        uiSkinCheckBox.setFont(new Font(18));
        if (settingsHandler.getUISkin() == UISkin.CLASSIC) {
            uiSkinCheckBox.setSelected(true);
        }

        Label description1 = new Label();
        description1.setText("The amount of milliseconds between each step");
        description1.setFont(new Font(18));
        // description1.setPadding(new Insets(20, 0, 0, 0));

        Label description2 = new Label();
        description2.setText("The amount of seconds to pause after solving a red square");
        description2.setFont(new Font(18));
        // description2.setPadding(new Insets(20, 0, 0, 0));


        ArrayList<Node> list = new ArrayList<>();
        list.add(modeContainer);
        list.add(createFiller());
        list.add(inputMethodContainer);
        list.add(createFiller());
        list.add(buildVersionContainer);
        list.add(createFiller());
        list.add(createLine());
        list.add(createFiller());
        list.add(confirmationsCheckBox);
        list.add(createFiller());
        list.add(speedCheckBox);
        list.add(createFiller());
        list.add(uiSkinCheckBox);
        list.add(createFiller());
        list.add(createLine());
        list.add(createFiller());
        list.add(description1);
        list.add(createFiller());
        list.add(intervalSlider);
        list.add(createFiller());
        list.add(createLine());
        list.add(createFiller());
        list.add(description2);
        list.add(createFiller());
        list.add(pauseSlider);
        list.add(createFiller());

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

            UISkin oldSkin = settingsHandler.getUISkin();
            if (uiSkinCheckBox.isSelected()) {
                settingsHandler.setUISkin(UISkin.CLASSIC);
            } else {
                settingsHandler.setUISkin(UISkin.CLEAN);
            }

            settingsHandler.setMode(Mode.valueOf(modeChoiceBox.getValue().toUpperCase()));
            settingsHandler.setInputMethod(InputMethod.valueOf(inputMethodChoiceBox.getValue().toUpperCase()));

            BuildVersion oldBuildVersion = settingsHandler.getBuildVersion();
            BuildVersion newBuildVersion = BuildVersion.valueOf(buildVersionChoiceBox.getValue().toUpperCase());

            settingsHandler.setBuildVersion(newBuildVersion);

            settingsHandler.updateFile();

            boolean versionSwitch = oldBuildVersion != newBuildVersion;
            boolean skinSwitch = oldSkin != settingsHandler.getUISkin();

            String begin = "Restarting to switch ";

            String skin = skinSwitch ? "UI skin" : "";
            String version = versionSwitch ? "build version" : "";
            String middle = (skinSwitch && versionSwitch) ? " and " : "";

            String text = begin + skin + middle + version;
            if (versionSwitch || skinSwitch) {
                UISkin newSkin = settingsHandler.getUISkin();

                settingsHandler.setUISkin(oldSkin);
                new Message(text, false);
                settingsHandler.setUISkin(newSkin);

                if (versionSwitch) {
                    Launcher.reboot("-switch");
                }
                else {
                    Launcher.reboot();
                }
            }
            else {
                setChanged();
                notifyObservers();
                stage.close();
            }
        });

        speedCheckBox.setOnAction(event -> setCheckboxVisibility(gridPane, speedCheckBox));

        StyleHandler.applyStyle(scene);

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

    private static Region createFiller() {
        Region filler = new Region();
        filler.setVisible(false);
        filler.setPrefSize(0, 0);
        filler.setPadding(new Insets(20, 0, 0, 0));

        return filler;
    }

    private static Region createLine() {
        Region line = new Region();
        line.setPrefSize(300, 1);
        line.setStyle("-fx-border-width: 0.5px;"
                + "-fx-border-color: black");

        return line;
    }

    private static <T extends Enum<T>> Pair<ChoiceBox<String>, HBox> createChoiceBoxContainer(Function<SettingsHandler, T> getter) {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        T enumInstance = getter.apply(settingsHandler);

        String instanceName = enumInstance.name().toLowerCase();

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setValue(instanceName);
        choiceBox.setPrefHeight(40);
        choiceBox.setPrefWidth(100);
        choiceBox.setStyle("-fx-font-size: 15");

        Class<T> enumClass = enumInstance.getDeclaringClass();

        T[] enumValues = enumClass.getEnumConstants();

        List<String> modes = Arrays.stream(enumValues)
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        choiceBox.getItems().addAll(modes);

        char[] chars = enumClass.getSimpleName().toCharArray();
        List<Character> characters = new LinkedList<>();
        characters.add(chars[0]);

        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                characters.add(' ');
                characters.add(Character.toLowerCase(chars[i]));
            }
            else {
                characters.add(chars[i]);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        characters.forEach(stringBuilder::append);

        Label modeLabel = new Label();
        modeLabel.setText(stringBuilder.toString() + ":");
        modeLabel.setStyle("-fx-font-size: 18");
        modeLabel.setPrefWidth(120);

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(40);
        container.getChildren().addAll(modeLabel, choiceBox);

        return Pair.with(choiceBox, container);
    }
}
