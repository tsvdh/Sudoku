import javafx.application.Application;
import javafx.stage.Stage;
import update.Updater;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        // new Sudoku(primaryStage);

        Updater updater = new Updater();
        updater.downloadNew();

        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);

    //     // ---old.exe---
    //     String callPath = Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    //     String joinedPath = new File(callPath).getParent() + "/new.exe";
    //     File file = new File(joinedPath);
    //     file.createNewFile();
    //
    //     InputStream inputStream = Launcher.class.getResourceAsStream("/configs/new.exe");
    //     OutputStream outputStream = new FileOutputStream(file);
    //
    //     int buffer = inputStream.read();
    //
    //     while (buffer != -1) {
    //         outputStream.write(buffer);
    //         buffer = inputStream.read();
    //     }
    //
    //     inputStream.close();
    //     outputStream.close();
    //
    //     new ProcessBuilder(joinedPath).start();
    //
    //     System.exit(0);
    //
    //     // ---new.exe---
    //     // String callPath = Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    //     // String joinedPath = new File(callPath).getParent() + "/old.exe";
    //     // File file = new File(joinedPath);
    //     //
    //     // file.delete();
    }
}
