package update;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ProgressInfo {

    ProgressInfo(DownloadTask downloadTask, Updater updater, String newName) {
        String oldName = updater.getCurrentName();
        String oldVersion = Updater.getVersionAsString(oldName);
        String newVersion = Updater.getVersionAsString(newName);

        Label versionLabel = new Label();
        versionLabel.setFont(new Font(20));
        versionLabel.setText("New version available: " + newVersion + " (current: " + oldVersion + ")");

        Label progressLabel = new Label();
        progressLabel.setFont(new Font(15));
        progressLabel.textProperty().bind(downloadTask.messageProperty());

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefSize(150, 25);
        progressBar.progressProperty().bind(downloadTask.progressProperty());

        HBox progressContainer = new HBox();
        progressContainer.getChildren().addAll(progressBar, progressLabel);
        progressContainer.setAlignment(Pos.CENTER);
        progressContainer.setSpacing(15);
        progressContainer.setPadding(new Insets(0, 0, 20, 0));

        Button button = new Button();
        button.setFont(new Font(20));
        button.setText("Start");
        button.setPrefSize(75, 25);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(versionLabel, button);
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
            vBox.getChildren().add(progressContainer);
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
