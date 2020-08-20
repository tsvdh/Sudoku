import common.FileHandler;
import common.popups.Message;
import javafx.application.Application;
import javafx.stage.Stage;
import sudoku.gui.main.Sudoku;
import update.Updater;

import java.io.IOException;
import java.util.Map;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        Updater updater = new Updater();

        Map<String, String> args = getParameters().getNamed();
        String wantedKey = "delete";

        if (args.containsKey(wantedKey)) {
            updater.delete(args.get(wantedKey));
        }
        else {
            updater.downloadNew();

            if (updater.isDownloadAttempt()) {
                if (updater.isSuccess()) {

                    updater.updateShortcut();

                    String oldPath = FileHandler.getCurrentFile().getPath();
                    String newPath = updater.getNewFile().getPath();

                    String deleteArg = "--delete=" + oldPath;
                    try {
                        new ProcessBuilder(newPath, deleteArg).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                else {
                    new Message("Download failed", true);
                }
            }
        }

        new Sudoku(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
