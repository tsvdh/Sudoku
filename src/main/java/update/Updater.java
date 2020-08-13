package update;

import common.FileHandler;
import common.SettingsHandler;
import common.options.BuildVersion;
import common.popups.Message;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Updater {

    private static final String NAME_START = "Sudoku-";
    private boolean success;
    private String currentName;

    public Updater() {
        String protocol = getClass().getResource("").getProtocol();
        if (!protocol.equals("jar")) {
            throw new IllegalStateException("Can only update when running in jar");
        }

        this.success = false;

        String oldPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        currentName = new File(oldPath).getName();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCurrentName() {
        return currentName;
    }

    private static int[] getVersionAsArray(String name) {
        if (!name.contains(NAME_START)) {
            throw new IllegalArgumentException("File name must start with '" + NAME_START + "'");
        }

        String[] version = name.replace(NAME_START, "").split("\\.");

        int[] versionNumber = new int[3];

        try {
            for (int i = 0; i < 3; i++) {
                versionNumber[i] = Integer.parseInt(version[i]);
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalArgumentException("Name must contain three consecutive numbers separated by dots");
        }

        return versionNumber;
    }

    static String getVersionAsString(String name) {
        int[] versionArray = getVersionAsArray(name);
        return versionArray[0] + "." + versionArray[1] + "." + versionArray[2];
    }

    private boolean isNewer(String newName) {
        int[] oldVersion = getVersionAsArray(currentName);
        int[] newVersion = getVersionAsArray(newName);

        for (int i = 0; i < 3; i++) {
            int oldPart = oldVersion[i];
            int newPart = newVersion[i];

            if (newPart > oldPart) {
                return true;
            }
            if (newPart < oldPart) {
                return false;
            }
        }

        return false;
    }

    public void downloadNew() {
        DropBoxHandler dropBoxHandler = DropBoxHandler.getInstance();
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        BuildVersion version = settingsHandler.getBuildVersion();

        Optional<String> optionalFileName = dropBoxHandler.getFileName(version);

        if (!optionalFileName.isPresent()) {
            System.out.println("No file available for download");
            success = false;
            return;
        }

        String fileName = optionalFileName.get();

        if (isNewer(fileName)) {
            File file;
            try {
                file = FileHandler.getExternalFileInHome("/Sudoku solver/" + fileName);
            } catch (IOException e) {
                success = false;
                return;
            }

            // sets 'this.success'
            dropBoxHandler.downloadToFile(file, this);

            afterDownload();
        }
        else {
            System.out.println("No update available");
            success = false;
        }
    }

    void afterDownload() {
        if (success) {
            new Message("Download succeeded", false);
        } else {
            new Message("Download failed", true);
        }
    }
}
