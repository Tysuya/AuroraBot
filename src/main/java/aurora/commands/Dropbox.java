package aurora.commands;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Dropbox {
    private static String ACCESS_TOKEN;
    private static DbxClientV2 client;

    public Dropbox() {
        ACCESS_TOKEN = System.getenv("dbtoken");

        try {
            ACCESS_TOKEN = new String(Files.readAllBytes(Paths.get("").toAbsolutePath().resolve("dbtoken"))).split("\n")[0];
        } catch(Exception e) {
            e.printStackTrace();
        }
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("Tysuya");
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public String getHistory(String fileName) {
        try {
            // Get files and folder metadata from Dropbox root directory
            ListFolderResult result = client.files().listFolder("");
            boolean found = false;
            while (true) {
                for (Metadata metadata : result.getEntries())
                    if(metadata.getName().contains(fileName))
                        found = true;

                if (!result.getHasMore())
                    break;

                result = client.files().listFolderContinue(result.getCursor());
            }

            if (!found)
                return "";
        } catch (DbxException e) {
            e.printStackTrace();
        }
        String output = "";
        try {
            DbxDownloader downloader = client.files().download("/" + fileName + ".txt");
            InputStream inputStream = downloader.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNext())
                output += "\n" + scanner.nextLine();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return output;
    }

    public void setHistory(String fileName, String output) {
        try {
            InputStream inputStream = new ByteArrayInputStream(output.getBytes());
            client.files().uploadBuilder("/" + fileName + ".txt").withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
