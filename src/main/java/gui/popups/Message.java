package gui.popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Message {

    public Message(String text) {
        Button button = new Button();
        button.setText("OK");
        button.setFont(new Font(20));
        button.setPrefSize(70, 25);

        Label label = new Label();
        label.setFont(new Font(20));
        label.setText(text);
        label.setStyle("-fx-text-fill: red");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label,
                                button);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(10));
        vBox.setMinWidth(300);

        Scene scene = new Scene(vBox);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Important message");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("images/icon.png"));

        button.setOnAction(event -> stage.close());

        vBox.requestFocus();
        for (Node node : vBox.getChildren()) {
            node.setOnMousePressed(event -> vBox.requestFocus());
        }
        stage.showAndWait();
    }
}
