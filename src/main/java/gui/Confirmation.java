package gui;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

class Confirmation {

    private String result;

    Confirmation() {
        result = "no";

        Button yesButton = new Button();
        yesButton.setFont(new Font(20));
        yesButton.setText("Yes");

        Button noButton = new Button();
        noButton.setFont(new Font(20));
        noButton.setText("No");

        HBox hBox = new HBox();
        hBox.getChildren().addAll(yesButton,
                                noButton);
        hBox.setSpacing(30);
        hBox.setAlignment(Pos.CENTER);

        Label label = new Label();
        label.setText("Do you want to perform the action?");
        label.setFont(new Font(20));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label,
                                hBox);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Confirmation");
        stage.getIcons().add(new Image("images/icon.png"));

        yesButton.setOnAction(event -> {
            result = "yes";
            stage.close();
        });

        noButton.setOnAction(event -> stage.close());

        hBox.requestFocus();

        for (Node node : hBox.getChildren()) {
            node.setOnMousePressed(event -> hBox.requestFocus());
        }

        stage.setOnCloseRequest(Event :: consume);

        stage.showAndWait();
    }

    String getResult() {
        return result;
    }
}
