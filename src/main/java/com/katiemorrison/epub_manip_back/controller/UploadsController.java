package com.katiemorrison.epub_manip_back.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katiemorrison.epub_manip_back.model.pojos.FileType;
import com.katiemorrison.epub_manip_back.model.pojos.ParentData;
import com.katiemorrison.epub_manip_back.model.pojos.RenameInfo;
import com.katiemorrison.epub_manip_back.util.CumulativeData;
import com.katiemorrison.epub_manip_back.util.Dummy;
import com.katiemorrison.epub_manip_back.util.FileOptions;
import com.katiemorrison.epub_manip_back.util.FileParts;
import com.katiemorrison.epub_manip_back.util.FileUtils;
import com.katiemorrison.epub_manip_back.util.ReplacementData;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;


@RestController
public class UploadsController {

    private static final String CONTAINER_NAME = "EpubManipGenerated";

    private String outputDirectory = "output" + File.separator;
    private String uploadDirectory = "uploads" + File.separator;
    private String finishedDirectory = "finished" + File.separator;

    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploads(@RequestPart("myFiles") ArrayList<MultipartFile> files, @RequestParam("fileOptions") String fileOptionsJSON) {
        try {
            FileOptions fileOptions = FileOptions.make(fileOptionsJSON);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");

            String epubBaseName = now.format(formatter) + files.get(0).getOriginalFilename();

            File dir = new File(uploadDirectory + epubBaseName);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (MultipartFile file : files) {
                File savedFile = new File(uploadDirectory + epubBaseName + File.separator + file.getOriginalFilename());
                file.transferTo(savedFile.toPath());
                unzipEpub(fileOptions, epubBaseName, savedFile.getAbsolutePath());
            }
            processEpub(epubBaseName, fileOptions);
            deleteTempFolder(epubBaseName);
            recalculateDirectories(epubBaseName, fileOptions);
            reconstructEpub(epubBaseName);
            System.out.println(fileOptions.toString());
            FileUtils.deleteFileOrDirectory(Paths.get(outputDirectory + epubBaseName).toAbsolutePath().normalize());
            FileUtils.deleteFileOrDirectory(Paths.get(uploadDirectory + epubBaseName).toAbsolutePath().normalize());
            return ResponseEntity.ok(epubBaseName);
        } catch (JsonProcessingException e) {
            System.out.println("JsonProcessingException: " + e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    private void unzipEpub(FileOptions fileOptions, String epubBaseName, String epubToUnzipPath) throws IOException {
        try {
            String writeTo = outputDirectory + epubBaseName;
            String parentFile = (new FileParts(epubToUnzipPath)).getName();
            ZipFile zip = new ZipFile(epubToUnzipPath);
            List<FileHeader> headers = zip.getFileHeaders();
            for (FileHeader header : headers) {
                if (!header.isDirectory()) {
                    FileParts fileParts = new FileParts(header.getFileName());
                    String ext = fileParts.getExt();
                    String fileName = fileParts.getName();
                    String pathFromEpubRoot = fileParts.getDir();
                    String pathPrepend = CONTAINER_NAME + File.separator;
                    String finalPath = (fileParts.getRootDir().equals(CONTAINER_NAME) ? "" : pathPrepend) + pathFromEpubRoot;
                    FileType type = fileOptions.getFileType(fileName);
                    System.out.println("Name: " + fileName + ", type: " + type);
                    if (ext.equals(".ncx") || ext.equals(".opf") || (ext.equals(".xhtml") && type == FileType.NAVIGATION)) {
                        boolean firstInstance = false;
                        if (fileOptions.getUniqueFileLocs().get(ext).equals("")) {
                            fileOptions.getUniqueFileLocs().put(ext, pathPrepend + fileName + ext);
                            firstInstance = true;
                        }
                        String tempLoc = writeTo + File.separator + "_tempmanip_";
                        int tempInd = fileOptions.getReplacementData().getIndices().get(ext);
                        zip.extractFile(header, tempLoc, fileName + tempInd + ext);
                        addHeaderToFile(parentFile, tempLoc + File.separator + fileName + tempInd + ext, firstInstance);
                        fileOptions.getReplacementData().getIndices().put(ext, tempInd + 1);
                        fileParts = new FileParts(pathPrepend, fileName, ext);
                    } else if ((fileName.equals("mimetype") && ext.equals("")) || "container.xml".equals(fileName + ext)) {
                        fileParts = new FileParts(pathFromEpubRoot, fileName, ext);
                    } else if (ext.equals(".xhtml")) {
                        if (type == FileType.EXCLUSION || type == FileType.CHAPTER || type == FileType.OTHER) {
                            String newName = (fileOptions.getReplacementData().generateNewName(fileName, parentFile, type)).getNewName();
                            fileParts = new FileParts(finalPath, newName, ext);
                        } else if (type == FileType.IGNORE) {
                            continue;
                        }
                    } else {
                        fileParts = new FileParts(finalPath, fileName, ext);
                    }
                    ExtractAndRecordFile(fileOptions, zip, header, writeTo, fileParts);
                }
            }
            zip.close();
        } catch (ZipException e) {
            System.out.println(e.getMessage());
        }
    }

    private void ExtractAndRecordFile(FileOptions fileOptions, ZipFile zip, FileHeader header, String baseDir, FileParts pathParts) throws ZipException{
        String fullPath = pathParts.toString();
        Path dir = Paths.get(fullPath);
        if (!Files.exists(dir)) {
            fileOptions.getFileLocs().put(pathParts.getName() + pathParts.getExt(), fullPath);
            zip.extractFile(header, baseDir, fullPath);
        }
    }

    private void addHeaderToFile(String parentFile, String filePath, boolean copyOfFinal) {
        try {
            Path dir = Paths.get(filePath);
            String fileContents = "<meta:EpubManip copyOfFinal=\"" + copyOfFinal + "\">" + parentFile + "</meta>\n" + Files.readString(dir);
            Files.writeString(dir, fileContents);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processEpub(String epubBaseName, FileOptions fileOptions) throws IOException {
        String tempFolder = outputDirectory + epubBaseName + File.separator + "_tempmanip_";
        Path tempDir = Paths.get(tempFolder);
        if (Files.exists(tempDir) && Files.isDirectory(tempDir)) {
            Files.walk(tempDir)
                .sorted()
                .forEach(path -> {
                    if (!Files.isDirectory(path)) {
                        processTempFile(path, fileOptions);
                    }
                });
        }
        String epubDir = outputDirectory + epubBaseName;
        fileOptions.getCumulativeData().mergeData();
        alterUniqueFiles(epubDir, fileOptions);
    }

    private void processTempFile(Path path, FileOptions fileOptions) {
        FileParts fileParts = new FileParts(path.getFileName().toString());
        String ext = fileParts.getExt();
        if (ext.equals(".ncx")) {
            processNCXFile(path, fileOptions);
        } else if (ext.equals(".opf")) {
            processOPFFile(path, fileOptions);
        } else if (ext.equals(".xhtml")) {
            processContentsFile(path, fileOptions);
        } else {
            System.out.println("Unexpected file type " + ext + " found in _tempmanip_ folder");
        }
    }

    private ParentData getParentData(String fileContent) {
        Pattern parentPattern = Pattern.compile("(<meta:EpubManip copyOfFinal=\")(.*?)(\">)(.*?)(</meta>)", Pattern.DOTALL);
        Matcher parentMatcher = parentPattern.matcher(fileContent);
        if (parentMatcher.find()) {
            return new ParentData(parentMatcher.group(4), Boolean.parseBoolean(parentMatcher.group(2)));
        }
        return null;
    }

    private ArrayList<String> extractSections(String fileContent, String patternString, int groupToExtract) {
        ArrayList<String> ret = new ArrayList<String>();
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            ret.add(matcher.group(groupToExtract));
        }

        return ret;
    }

    private RenameInfo handleNodeFileRename(FileOptions fileOptions, String content, String patternString, int groupToRename, String parentExt, String parentEpub) {
        RenameInfo ret = new RenameInfo();
        ReplacementData replacementData = fileOptions.getReplacementData();
        CumulativeData cumulativeData = fileOptions.getCumulativeData();
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        String newName = null;
        String replacement = null;
        String originalPath = null;
        
        if (matcher.find()) {
            FileParts fileParts = new FileParts(matcher.group(groupToRename));
            String fileName = fileParts.getName();
            String ext = fileParts.getExt();
            originalPath = matcher.group(groupToRename);
            FileType type = fileOptions.getFileType(fileName);

            if (ext.equals(".xhtml")) {
                if (type == FileType.NAVIGATION) {
                    ret.setNavigationNode(true);
                    String savedContentsName = (new FileParts(fileOptions.getUniqueFileLocs().get(ext))).getName();
                    if (!fileName.equals(savedContentsName)) {
                        ret.setIgnoreNode(true);
                    } else if (parentExt.equals(".opf") && cumulativeData.getOpfContentsElement() != null) {
                        ret.setIgnoreNode(true);
                    }
                } else if (type == FileType.EXCLUSION) {
                    newName = replacementData.getExclusions().get(fileName).getNewName();
                } else if (type == FileType.CHAPTER) {
                    newName = replacementData.getChapterName(fileName, parentEpub);
                } else if (type == FileType.OTHER) {
                    newName = replacementData.getOtherName(fileName, parentEpub);
                } else if (type == FileType.IGNORE) {
                    return null;
                }
            } else if (ext.equals(".ncx")) {
                ret.setNCXNode(true);
                String savedNCXName = (new FileParts(fileOptions.getUniqueFileLocs().get(".ncx"))).getName();
                if (cumulativeData.getOpfNCXElement() != null || !savedNCXName.equals((new FileParts(originalPath)).getName())) {
                    ret.setIgnoreNode(true);
                }
            } else {
                //Other
            }

            newName = FileUtils.firstNonNull(newName, fileName);

            if (replacementData.getRecordedFiles().get(parentExt).get(newName) == null) {
                replacementData.getRecordedFiles().get(parentExt).put(newName, true);
            } else {
                ret.setIgnoreNode(true);
            }

            replacement = fileOptions.getFileLocs().get(newName + ext);
            if (parentExt.equals(".opf")) {
                replacement = replacement.replaceFirst(CONTAINER_NAME + File.separator, "");
            } else {
                replacement = "/" + replacement;
            }
        }

        String before = matcher.group(0);
        StringBuilder after = new StringBuilder();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (i == groupToRename) {
                after.append(replacement);
            } else {
                after.append(FileUtils.firstNonNull(matcher.group(i), ""));
            }
        }
        content = content.replace(before, after);
        
        ret.setOriginalPath(originalPath);
        ret.setNewPath(replacement);
        ret.setContent(content);
        return ret;
    }

    private void processNCXFile(Path path, FileOptions fileOptions) {
        try {
            String fileContent = Files.readString(path);
            CumulativeData cumulativeData = fileOptions.getCumulativeData();
            String parentFile = getParentData(fileContent).getParentFile();
            ArrayList<String> navPoints = extractSections(fileContent, "(\n*)(\s*)(<navPoint.*?</navPoint>)", 0);

            for (String navPoint : navPoints) {
                navPoint = navPoint.replaceAll("\splayOrder=\".*?\"", "");
                Dummy dummy = new Dummy() {
                    @Override
                    public String execute(String value) {
                        int navPointInd = cumulativeData.getNcxInd();
                        return "navPoint-" + navPointInd + "\" playOrder=\"" + navPointInd;
                    }
                };
                navPoint = processReplacements(navPoint, "(id=\")(.*?)(\")", 2, dummy);

                RenameInfo renameInfo = handleNodeFileRename(fileOptions, navPoint, "(src=\")(.*?)(#.*)?(\")", 2, ".ncx", parentFile);

                if (renameInfo != null && !renameInfo.isIgnoreNode()) {
                    navPoint = renameInfo.getContent();
                    if (renameInfo.isNavigationNode()) {
                        if (cumulativeData.getNcxContentNavPoint() == null) {
                            cumulativeData.setNcxContentNavPoint(navPoint);
                            cumulativeData.incrementNCXInd();
                        }
                    } else {
                        fileOptions.getCumulativeData().getNcxNavPoints().add(navPoint);
                        cumulativeData.incrementNCXInd();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void processOPFFile(Path path, FileOptions fileOptions) {
        try {
            String fileContent = Files.readString(path);
            CumulativeData cumulativeData = fileOptions.getCumulativeData();
            ParentData parentData = getParentData(fileContent);
            HashMap<String, String> spineRefs = new HashMap<String, String>();

            ArrayList<String> items = extractSections(fileContent, "(\n*)(\s*)(<item .*?/>)", 0);
            for (String item : items) {
                RenameInfo renameInfo = handleNodeFileRename(fileOptions, item, "(href=\")(.*?)(\")", 2, ".opf", parentData.getParentFile());
                if (renameInfo != null && !renameInfo.isIgnoreNode()) {
                    String content = renameInfo.getContent();
                    String newPathName = new FileParts(renameInfo.getNewPath()).getName();
                    content = content.replaceAll("id=\".*?\"", "id=\"" + newPathName + "\"");
                    if (renameInfo.isNCXNode()) {
                        cumulativeData.setOpfNCXElement(content);
                        cumulativeData.setOpfSpineToc(newPathName);
                    } else if (renameInfo.isNavigationNode()) {
                        cumulativeData.setOpfContentsElement(content);
                        cumulativeData.setOpfFallback(newPathName);
                    } else {
                        cumulativeData.getOpfManifestData().add(content);
                    }
                    spineRefs.put((new FileParts(renameInfo.getOriginalPath())).getName(), newPathName);
                }
            }

            ArrayList<String> itemRefs = extractSections(fileContent, "(\n*)(\s*)(<itemref.*?/>)", 0);
            for (String itemRef : itemRefs) {
                Pattern itemRefIdRefPattern = Pattern.compile("(idref=\")(.*?)(\")", Pattern.DOTALL);
                Matcher itemRefIdRefMatcher = itemRefIdRefPattern.matcher(itemRef);
                if (itemRefIdRefMatcher.find()) {
                    String newName = spineRefs.get(itemRefIdRefMatcher.group(2));
                    if (newName != null) {
                        itemRef = itemRef.replaceAll(itemRefIdRefMatcher.group(0), itemRefIdRefMatcher.group(1) + newName + itemRefIdRefMatcher.group(3));
                        cumulativeData.getOpfSpineData().add(itemRef);
                    }
                }
            }

            if (parentData.isCopyOfFinal()) {
                ArrayList<String> references = extractSections(fileContent, "(\n*)(\s*)(<reference.*?/>)", 0);
                for (String reference : references) {
                    RenameInfo renameInfo = handleNodeFileRename(fileOptions, reference, "(href=\")(.*?)(#.*)?(\")", 2, ".opf", parentData.getParentFile());
                    if (renameInfo != null) {
                        cumulativeData.getOpfReferenceData().add(renameInfo.getContent());
                    }
                }
            }
                    
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void processContentsFile(Path path, FileOptions fileOptions) {
        try {
            String fileContent = Files.readString(path);
            CumulativeData cumulativeData = fileOptions.getCumulativeData();
            ParentData parentData = getParentData(fileContent);
            String parentFile = parentData.getParentFile();
            ArrayList<String> contentsOL1 = cumulativeData.getContentsOL1Data();
            ArrayList<String> contentsOL2 = cumulativeData.getContentsOL2Data();
            ArrayList<String> OLs = extractSections(fileContent, "(<ol>.*?</ol>)", 0);
            String OL1 = OLs.get(0);
            String liPattern = "(\n*)(\s*)(<li>.*?</li>)";
            String hrefPattern = "(href=\")(.*?)(#.*)?(\")";
            ArrayList<String> OL1LIs = extractSections(OL1, liPattern, 0);

            for (String LI : OL1LIs) {
                RenameInfo renameInfo = handleNodeFileRename(fileOptions, LI, hrefPattern, 2, ".xhtml", parentFile);
                if (renameInfo != null && !renameInfo.isIgnoreNode()) {
                    contentsOL1.add(renameInfo.getContent());
                }
            }

            if (parentData.isCopyOfFinal()) {
                String OL2 = OLs.get(1);
                ArrayList<String> OL2LIs = extractSections(OL2, liPattern, 0);
                for (String LI : OL2LIs) {
                    RenameInfo renameInfo = handleNodeFileRename(fileOptions, LI, hrefPattern, 2, ".xhtml", parentFile);
                    if (renameInfo != null) {
                        contentsOL2.add(renameInfo.getContent());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private void alterUniqueFiles(String root, FileOptions fileOptions) {
        try {
            CumulativeData cumulativeData = fileOptions.getCumulativeData();
            HashMap<String, ArrayList<String>> chunks = new HashMap<String, ArrayList<String>>();
            Path path = Paths.get(root + File.separator + fileOptions.getUniqueFileLocs().get(".ncx"));
            String fileContent = Files.readString(path);
            ArrayList<String> title = new ArrayList<String>(Arrays.asList(fileOptions.getOutputName()));
            ArrayList<String> toc = new ArrayList<String>(Arrays.asList(cumulativeData.getOpfSpineToc()));
            Dummy dummy = new Dummy() {
                @Override
                public String execute(String value) {
                    StringBuilder sb = new StringBuilder();
                    for (String item : chunks.get("next")) {
                        sb.append(item);
                    }
                    return sb.toString();
                }
            };

            chunks.put("next", title);
            fileContent = processReplacements(fileContent, "(<docTitle.*?<text>)(.*?)(\n*)(\s*)(</text>)", 2, dummy);

            chunks.put("next", cumulativeData.getNcxNavPoints());
            fileContent = processReplacements(fileContent, "(<navMap>)(.*?)(\n*)(\s*)(</navMap>)", 2, dummy);

            Files.writeString(path, fileContent, StandardOpenOption.TRUNCATE_EXISTING);
            path = Paths.get(root + File.separator + fileOptions.getUniqueFileLocs().get(".opf"));
            fileContent = Files.readString(path);

            chunks.put("next", title);
            fileContent = processReplacements(fileContent, "(<dc:title>)(.*?)(\n*)(\s*)(</dc:title>)", 2, dummy);

            chunks.put("next", cumulativeData.getOpfManifestData());
            fileContent = processReplacements(fileContent, "(<manifest>)(.*?)(\n*)(\s*)(</manifest>)", 2, dummy);

            chunks.put("next", toc);
            fileContent = processReplacements(fileContent, "(<spine.*?toc=\")(.*?)(\")", 2, dummy);

            chunks.put("next", cumulativeData.getOpfSpineData());
            fileContent = processReplacements(fileContent, "(<spine.*?>)(.*?)(\n*)(\s*)(</spine>)", 2, dummy);

            chunks.put("next", cumulativeData.getOpfReferenceData());
            fileContent = processReplacements(fileContent, "(<guide.*?>)(.*?)(\n*)(\s*)(</guide>)", 2, dummy);
    
            Files.writeString(path, fileContent, StandardOpenOption.TRUNCATE_EXISTING);
            path = Paths.get(root + File.separator + fileOptions.getUniqueFileLocs().get(".xhtml"));
            fileContent = Files.readString(path);

            chunks.put("next", fileOptions.getCumulativeData().getContentsOL1Data());
            fileContent = processReplacements(fileContent, "(<ol>)(.*?)(\n*)(\s*)(</ol>)", 2, dummy);

            chunks.put("next", fileOptions.getCumulativeData().getContentsOL2Data());
            fileContent = processReplacements(fileContent, "(<ol>)(.*?)(</ol>)(.*?)(<ol>)(.*?)(\n*)(\s*)(</ol>)", 6, dummy);

            Files.writeString(path, fileContent, StandardOpenOption.TRUNCATE_EXISTING);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteTempFolder(String epubBaseName) throws IOException {
        String root = outputDirectory + epubBaseName + File.separator + "_tempmanip_";
        Path rootPath = Paths.get(root);
        if (Files.exists(rootPath) && Files.isDirectory(rootPath)) {
            Files.walk(rootPath)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete " + path + ": " + e.getMessage());
                    }
                });
        }
    }

    private void recalculateDirectories(String epubBaseName, FileOptions fileOptions) throws IOException {
        String root = outputDirectory + epubBaseName;
        Path rootPath = Paths.get(root);
        if (Files.exists(rootPath) && Files.isDirectory(rootPath)) {
            Files.walk(rootPath)
                .forEach(path -> {
                    if (!Files.isDirectory(path)) {
                        try {
                            if (path.getFileName().toString().equals("container.xml")) {
                                recalculateDirectory(fileOptions, path, true);
                            } else if ((new FileParts(path.getFileName().toString())).getExt().equals(".xhtml")) {
                                recalculateDirectory(fileOptions, path, false);
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
        }
    }

    private void recalculateDirectory(FileOptions fileOptions, Path path, boolean isContainerXML) throws IOException {
        String fileContent = Files.readString(path);
        if (isContainerXML) {
            Dummy dummy = new Dummy() {
                @Override
                public String execute(String value) {
                    return fileOptions.getUniqueFileLocs().get(".opf");
                }
            };
            fileContent = processReplacements(fileContent, "(full-path=\")(.*?)(\")", 2, dummy);
        } else {
            Dummy dummy = new Dummy() {
                @Override
                public String execute(String value) {
                    FileParts fileParts = new FileParts(value);
                    return "/" + fileOptions.getFileLocs().get(fileParts.getName() + fileParts.getExt());
                }
            };
            fileContent = processReplacements(fileContent, "(href=\")(.*?)(\")", 2, dummy);
            fileContent = processReplacements(fileContent, "(src=\")(.*?)(\")", 2, dummy);
        }

        Files.writeString(path, fileContent, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String processReplacements(String content, String patternString, int groupToReplace, Dummy dummy) {
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
                    after.append(FileUtils.firstNonNull(matcher.group(i), ""));
                }
            }
            content = content.replace(before, after);
        }

        return content;
    }

    private void reconstructEpub(String epubBaseName) throws IOException {
        try {
            String sourcePath = outputDirectory + epubBaseName;
            final Path outputFolder = Paths.get(sourcePath).toAbsolutePath().normalize();
            String zipPath = finishedDirectory + epubBaseName;
            ZipFile zip = new ZipFile(zipPath);

            if (Files.exists(outputFolder) && Files.isDirectory(outputFolder)) {
                Files.walk(outputFolder, 1)
                    .forEach(path -> {
                        try {
                            if (!outputFolder.equals(path)) {
                                if (Files.isDirectory(path)) {
                                    File dirToZip = new File(path.toString());
                                    zip.addFolder(dirToZip);
                                } else {
                                    zip.addFile(path.toString());
                                }
                            }
                        } catch (ZipException e) {
                            System.out.println(e.getMessage());
                        }

                    });
            } else {
                System.out.println("Directory does not exist or is not a directory.");
            }
            zip.close();
        } catch (ZipException e) {
            System.out.println(e.getMessage());
        }
    }
}