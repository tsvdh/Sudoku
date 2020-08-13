package update;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.v2.files.FileMetadata;
import common.FileHandler;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

class DownloadTask extends Task<Void> implements Observer {

    private long totalBytes;
    private FileHandler fileHandler;

    DownloadTask(DbxDownloader<FileMetadata> downloader, File file) {
        this.totalBytes = downloader.getResult().getSize();
        this.fileHandler = new FileHandler(file, downloader.getInputStream());
        fileHandler.addObserver(this);
    }

    @Override
    protected Void call() throws IOException {
        fileHandler.writeToFile();
        return null;
    }

    private void updateProgress(long bytesWritten) {
        super.updateMessage("Downloading: " + bytesWritten + " / " + totalBytes + " bytes");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FileHandler) {
            updateProgress((long) arg);
        }
    }
}
