package update;

import javafx.event.Event;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ProgressInfo {

    private DownloadTask downloadTask;

    ProgressInfo(DownloadTask downloadTask, Updater updater, String newName) {
        this.downloadTask = downloadTask;
        String oldName = updater.getCurrentName();

        Label versionLabel = new Label();
        versionLabel.setFont(new Font(20));
        versionLabel.setText(oldName + " -> " + newName);

        Label progressLabel = new Label();
        progressLabel.setFont(new Font(20));
        progressLabel.textProperty().bind(downloadTask.messageProperty());

        Button button = new Button();
        button.setFont(new Font(20));
        button.setText("Start");
        button.setPrefSize(75, 25);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(versionLabel, progressLabel, button);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(10));
        vBox.setMinWidth(400);
        vBox.setMinHeight(100);

        Scene scene = new Scene(vBox);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Progress info");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("images/icon.png"));

        vBox.requestFocus();
        for (Node node : vBox.getChildren()) {
            node.setOnMousePressed(event -> vBox.requestFocus());
        }


        Executor executor = Executors.newSingleThreadExecutor();

        button.setOnAction(event -> {
            vBox.getChildren().remove(button);
            executor.execute(downloadTask);
        });

        downloadTask.setOnSucceeded(event -> {
            updater.setSuccess(true);
            stage.close();
        });

        downloadTask.setOnFailed(event -> {
            updater.setSuccess(false);
            stage.close();
        });


        stage.setOnCloseRequest(Event::consume);
        stage.showAndWait();
    }
}
