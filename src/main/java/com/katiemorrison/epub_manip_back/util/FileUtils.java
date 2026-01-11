package com.katiemorrison.epub_manip_back.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileUtils {

    @SafeVarargs
    public static <T> T firstNonNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static void deleteFileOrDirectory(Path directory, boolean log) {
        try {
            if (Files.exists(directory)) {
                if (Files.isDirectory(directory)) {
                    Files.walk(directory)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Failed to delete " + path + ": " + e.getMessage());
                            }
                        });
                    if (log) {
                        System.out.println("All files within " + directory + " deleted");
                    }
                } else {
                    Files.delete(directory);
                }
            } else {
                System.out.println("File/directory does not exist.");
            }
        } catch (IOException e) {
            System.err.println("Error deleting file/directory: " + e.getMessage());
        }
    }

    public static void deleteFileOrDirectory(Path directory) {
        deleteFileOrDirectory(directory, false);
    }
    
}
