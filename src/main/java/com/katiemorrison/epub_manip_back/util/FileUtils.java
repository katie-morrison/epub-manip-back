package com.katiemorrison.epub_manip_back.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    public static String processReplacements(String content, String patternString, int groupToReplace, Dummy dummy) {
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String before = matcher.group(0);
            String replacement = dummy.execute(matcher.group(groupToReplace));
            StringBuilder after = new StringBuilder();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (i == groupToReplace) {
                    after.append(replacement);
                } else {
                    after.append(firstNonNull(matcher.group(i), ""));
                }
            }
            content = content.replace(before, after);
        }

        return content;
    }

    public static String addTimeStamp(String name) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        return now.format(formatter) + name;
    }

}
