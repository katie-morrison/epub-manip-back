package com.katiemorrison.epub_manip_back.controller;

import org.springframework.web.bind.annotation.RestController;

import com.katiemorrison.epub_manip_back.util.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class GetEpubController {

    private String finishedDirectory = "finished" + File.separator;
    private final Path finishedFolder = Paths.get(finishedDirectory).toAbsolutePath().normalize();

    @GetMapping("/getEpub/{name}")
    public ResponseEntity<Resource> getEpub(@PathVariable String name) {
        try {
            Path filePath = finishedFolder.resolve(name).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    FileUtils.deleteFileOrDirectory(Paths.get(finishedDirectory + name).toAbsolutePath().normalize());
                }
            };
            executorService.schedule(task, 1, TimeUnit.MINUTES);

            if (resource.exists()) {
                String contentType = "application/octet-stream";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            System.out.println("Exception caught in getEpub: " + e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    
}
