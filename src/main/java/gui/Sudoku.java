package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Sudoku extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Text text = new Text("Hello world!");
        text.setFont(new Font(20));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(text);

        Scene scene = new Scene(borderPane, 500, 300);

        stage.setScene(scene);
        stage.setTitle("Sudoku");
        stage.show();
    }
}
