package pitheguy.autoblocks.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {
    public static String findFirstValidFilename(String name, Path folderPath, String extension) {
        int index = 0;
        String filename;
        Path filepath;
        do {
            filename = name + ((index == 0) ? "" : "_" + index) + "." + extension;
            index++;
            filepath = folderPath.resolve(filename);
        } while (Files.exists(filepath));
        return filename;
    }
}
