package update;

import common.FileHandler;
import common.SettingsHandler;
import common.options.BuildVersion;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Updater {

    private static final String NAME_START = "Sudoku-";

    public Updater() {
        String protocol = getClass().getResource("").getProtocol();
        if (!protocol.equals("jar")) {
            throw new IllegalStateException("Can only update when running in jar");
        }
    }

    private int[] getVersion(String name) {
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

    private boolean isNewer(String newName) {
        String oldPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String oldName = new File(oldPath).getName();

        int[] oldVersion = getVersion(oldName);
        int[] newVersion = getVersion(newName);

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

    public boolean downloadNew() {
        DropBoxHandler dropBoxHandler = DropBoxHandler.getInstance();
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        BuildVersion version = settingsHandler.getBuildVersion();

        Optional<String> optionalFileName = dropBoxHandler.getFileName(version);

        if (!optionalFileName.isPresent()) {
            System.out.println("No file available for download");
            return false;
        }

        String fileName = optionalFileName.get();

        if (isNewer(fileName)) {
            System.out.println("Starting download");

            File file;
            try {
                file = FileHandler.getExternalFileInHome("/Sudoku solver/" + fileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }

            System.out.println("Downloading");

            boolean succeeded = dropBoxHandler.downloadToFile(file);

            System.out.print("Download ");
            if (succeeded) {
                System.out.println("succeeded");
                return true;
            } else {
                System.out.println("failed");
                return false;
            }
        }
        else {
            System.out.println("No update available");
            return false;
        }
    }
}
