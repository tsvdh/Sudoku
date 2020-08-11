package update;

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
import common.Security;
import common.options.BuildVersion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

    private static final String ENCODED_ACCESS_KEY = "WHg1akZHYi1uOEFBQUFBQUFBQUFBY05IMzJFcWRfVkVoSmlqVnRJVTdsQ09jaEppQ1k3eEhZS0hNalZXMVJzTg==";
    private static final String ADDRESS = "dropbox/Sudoku solver";
    private static final String DOWNLOAD_FOLDER = "/downloads/";

    private DbxClientV2 client;
    private Metadata currentMetadata;

    private DropBoxHandler() {
        DbxRequestConfig requestConfig = new DbxRequestConfig(ADDRESS);
        String accessKey = Security.decode(ENCODED_ACCESS_KEY);
        this.client = new DbxClientV2(requestConfig, accessKey);
    }

    private Optional<DbxClientV2> getClient() {
        if (dropBoxReachable()) {
            return Optional.of(client);
        } else {
            return Optional.empty();
        }
    }

    private boolean dropBoxReachable() {
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
            e.printStackTrace();
            System.out.println("Error while connecting");
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

    Optional<String> getFileName(BuildVersion buildVersion) {
        Optional<DbxClientV2> optionalClient = this.getClient();
        if (!optionalClient.isPresent()) {
            return Optional.empty();
        }

        DbxUserFilesRequests filesRequests = optionalClient.get().files();
        String buildFolder = buildVersion.name().toLowerCase();

        ListFolderResult result;
        try {
            result = filesRequests.listFolder(DOWNLOAD_FOLDER + buildFolder);
        } catch (DbxException e) {
            System.out.println("Could not access the folder");
            return Optional.empty();
        }

        List<Metadata> entries = result.getEntries();
        if (entries.size() != 1) {
            throw new IllegalStateException("There should be exactly one file in the folder");
        }

        this.currentMetadata = entries.get(0);

        return Optional.of(currentMetadata.getName());
    }

    boolean downloadToFile(File file) {
        if (currentMetadata == null) {
            throw new IllegalStateException("Metadata is null, pleas call 'getFileName' first.");
        }

        Optional<DbxClientV2> optionalClient = this.getClient();
        if (!optionalClient.isPresent()) {
            return false;
        }

        DbxUserFilesRequests filesRequests = optionalClient.get().files();

        DbxDownloader<FileMetadata> downloader;
        try {
            downloader = filesRequests.download(currentMetadata.getPathDisplay());
        } catch (DbxException e) {
            System.out.println("Could not download the file");
            return false;
        }

        InputStream inputStream = downloader.getInputStream();

        try {
            FileHandler.writeToFile(inputStream, file);
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
