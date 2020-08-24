package update;

import common.FileHandler;
import common.SettingsHandler;
import common.options.BuildVersion;
import common.popups.Message;
import mslinks.ShellLink;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Updater {

    enum Mode {
        INSTALLING,
        UPDATING
    }

    private static final String NAME_START = "Sudoku-";
    private static final String NAME_INSTALLER = "Installer";
    private static final String SHORTCUT_NAME = "Sudoku solver.lnk";

    private boolean downloadAttempt;
    private boolean success;
    private String currentName;
    private File newFile;
    private Mode mode;

    public Updater() {
        if (!FileHandler.inJar()) {
            throw new IllegalStateException("Can only update when running in jar");
        }

        this.success = false;
        this.downloadAttempt = false;

        String oldPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        currentName = new File(oldPath).getName();

        if (currentName.contains(NAME_INSTALLER)) {
            this.mode = Mode.INSTALLING;
        } else {
            this.mode = Mode.UPDATING;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isDownloadAttempt() {
        return downloadAttempt;
    }

    public String getCurrentName() {
        return currentName;
    }

    public File getNewFile() {
        return newFile;
    }

    public Mode getMode() {
        return mode;
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
        if (mode == Mode.INSTALLING) {
            return true;
        }

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

        BuildVersion version;
        if (mode == Mode.UPDATING) {
            version = SettingsHandler.getInstance().getBuildVersion();
        } else {
            version = BuildVersion.STABLE;
        }

        Optional<String> optionalFileName = dropBoxHandler.getFileName(version);

        if (!optionalFileName.isPresent()) {
            System.out.println("No file available for download");
            return;
        }

        String fileName = optionalFileName.get();

        if (isNewer(fileName)) {
            downloadAttempt = true;

            try {
                newFile = FileHandler.getExternalFileInProgramFolder(fileName);
            } catch (IOException e) {
                success = false;
                return;
            }

            // sets 'this.success'
            dropBoxHandler.downloadToFile(newFile, this);
        }
        else {
            System.out.println("No update available");
        }
    }

    private File getShortcut() {
        File desktop = FileHandler.getDesktop();
        String shortcutPath = desktop.getPath() + "/" + SHORTCUT_NAME;
        return new File(shortcutPath);
    }

    public void delete(String path) {
        File file = new File(path);
        if (!file.delete()) {
            new Message("Could not delete old file at: " + file.getPath(), true);
        }
    }

    public void updateShortcut() {
        File shortcut = getShortcut();
        String shortcutPath = shortcut.getPath();
        String newPath = newFile.getPath();

        if (mode == Mode.UPDATING) {
            if (!shortcut.delete()) {
                new Message("Could not delete old shortcut", true);
                return;
            }
        }

        try {
            ShellLink.createLink(newPath, shortcutPath);
        } catch (IOException e) {
            new Message("Could not create shortcut", true);
        }
    }
}
