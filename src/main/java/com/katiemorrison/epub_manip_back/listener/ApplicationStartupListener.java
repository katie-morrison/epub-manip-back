package com.katiemorrison.epub_manip_back.listener;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.katiemorrison.epub_manip_back.util.FileUtils;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent>{

    private final Path uploadsFolder = Paths.get("uploads").toAbsolutePath().normalize();
    private final Path outputFolder = Paths.get("output").toAbsolutePath().normalize();
    private final Path finishedFolder = Paths.get("finished").toAbsolutePath().normalize();
    private ArrayList<Path> directories = new ArrayList<>(Arrays.asList(uploadsFolder, outputFolder, finishedFolder));

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        purgeTempDirectories();
        recreateUploadsFolder();
    }

    private void purgeTempDirectories() {
        for (Path directory : directories) {
            FileUtils.deleteFileOrDirectory(directory, true);
        }
    }

    private void recreateUploadsFolder() {
        for (Path directory : directories) {
            File folder = new File(directory.toUri());
            if(!folder.exists()) {
                folder.mkdirs();
            }
        }

    }
    
}
