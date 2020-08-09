package updater;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import common.FileHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

class DropBoxHandler {

    private static DropBoxHandler instance;

    static DropBoxHandler getInstance() {
        if (instance == null) {
            synchronized (DropBoxHandler.class) {
                if (instance == null) {
                    instance = new DropBoxHandler();
                }
            }
        }
        return instance;
    }

    private static final String ACCESS_KEY = "OjlnHAZZvuAAAAAAAAAAAahEhZ75NXfUw4R7Y3iTqXcammPohSKzwKHbLtNaOvLG";
    private static final String ADDRESS = "dropbox/Sudoku solver";

    private DbxClientV2 client;

    private DropBoxHandler() {
        DbxRequestConfig requestConfig = new DbxRequestConfig(ADDRESS);
        this.client = new DbxClientV2(requestConfig, ACCESS_KEY);
    }

    Optional<DbxClientV2> getClient() {
        if (dropBoxReachable()) {
            return Optional.of(client);
        } else {
            return Optional.empty();
        }
    }

    boolean dropBoxReachable() {
        System.out.println("Checking connection");
        String result;

        try {
            result = client.check().user().getResult();
        }
        catch (NetworkIOException e) {
            System.out.println("No internet");
            return false;
        }
        catch (DbxException e) {
            System.out.println(e.getClass().getName());
            e.printStackTrace();
            return false;
        }

        boolean validConnection = result.equals("\"\"");
        if (validConnection) {
            System.out.println("Valid");
        } else {
            System.out.println("Invalid");
        }
        return validConnection;
    }

    public static void main(String[] args) throws DbxException, IOException {
        DropBoxHandler dropBoxHandler = DropBoxHandler.getInstance();

        Optional<DbxClientV2> optionalClient = dropBoxHandler.getClient();
        if (!optionalClient.isPresent()) {
            return;
        }

        DbxClientV2 client = optionalClient.get();

        DbxUserFilesRequests filesRequests = client.files();

        ListFolderResult result = filesRequests.listFolder("");
        for (Metadata metadata : result.getEntries()) {

            String path = metadata.getPathDisplay();

            if (path.contains(".")) {
                System.out.println("Downloading: " + path);

                DbxDownloader<FileMetadata> downloader = filesRequests.download(path);
                File file = FileHandler.getExternalFileInHome("/Sudoku solver/" + downloader.getResult().getName());
                InputStream downloadStream = downloader.getInputStream();
                FileHandler.writeToFile(downloadStream, file);
            }
        }
    }
}
