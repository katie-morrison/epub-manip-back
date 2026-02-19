package com.katiemorrison.epub_manip_back.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.katiemorrison.epub_manip_back.util.FileParts;
import com.katiemorrison.epub_manip_back.util.FileUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;


@RestController
public class CalculateDiagnosticsController {

    private String uploadDirectory = "uploads" + File.separator;

    @PostMapping(value = "/calculateDiagnostics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> calculateDiagnostics(@RequestPart("myFile") ArrayList<MultipartFile> files) {
        Iterator<MultipartFile> iter = files.iterator();
        while (iter.hasNext()) {
            MultipartFile file = iter.next();
            String ext = new FileParts(file.getOriginalFilename()).getExt();
            if (!ext.equals(".epub")) {
                iter.remove();
            }
        }
        try {
            if (files.size() > 0) {
                MultipartFile multipartFile = files.get(0);
                String epubBaseName = FileUtils.addTimeStamp(multipartFile.getOriginalFilename());

                File dir = new File(uploadDirectory + epubBaseName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(uploadDirectory + epubBaseName + File.separator + multipartFile.getOriginalFilename());
                multipartFile.transferTo(file.toPath());
                ZipFile zip = new ZipFile(file.getAbsolutePath());
                List<FileHeader> headers = zip.getFileHeaders();

                ArrayList<String> names = new ArrayList<String>();
                for (FileHeader header : headers) {
                    if (!header.isDirectory()) {
                        names.add(header.getFileName());
                    }
                }

                FileUtils.deleteFileOrDirectory(Paths.get(uploadDirectory + epubBaseName).toAbsolutePath().normalize());
                zip.close();
                return ResponseEntity.ok(names);
            } else {
                String err = "No files of type epub received";
                System.out.println(err);
                ArrayList<String> ret = new ArrayList<String>();
                ret.add(err);
                return ResponseEntity.status(400).body(ret);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            ArrayList<String> ret = new ArrayList<String>();
            ret.add(e.getMessage());
            return ResponseEntity.status(500).body(ret);
        }

    }
    
    
}
