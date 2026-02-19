package com.katiemorrison.epub_manip_back.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katiemorrison.epub_manip_back.model.pojos.ChapterFormat;
import com.katiemorrison.epub_manip_back.model.pojos.FileType;
import com.katiemorrison.epub_manip_back.model.pojos.IgnoreFile;
import com.katiemorrison.epub_manip_back.model.pojos.NonChapterXHTML;
import com.katiemorrison.epub_manip_back.model.pojos.PhraseReplacements;
import com.katiemorrison.epub_manip_back.model.pojos.xhtmlNav;

public class FileOptions {

    private ArrayList<ChapterFormat> chapterFormat;
    private ArrayList<NonChapterXHTML> nonChapterXHTML;
    private ArrayList<IgnoreFile> ignoreFile;
    private ArrayList<xhtmlNav> xhtmlNav;
    private ArrayList<PhraseReplacements> replacements;
    private String outputName;
    private ReplacementData replacementData;
    private HashMap<String, String> uniqueFileLocs;
    private HashMap<String, String> fileLocs;
    private CumulativeData cumulativeData;

    public static FileOptions make(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FileOptions fileOptions = mapper.readValue(jsonString, FileOptions.class);
        fileOptions.cleanFileOptions();
        return fileOptions;
    }

    private FileOptions() {
        this.replacementData = new ReplacementData();
        this.fileLocs = new HashMap<String, String>();
        this.uniqueFileLocs = new HashMap<String, String>();
        this.uniqueFileLocs.put(".opf", "");
        this.uniqueFileLocs.put(".ncx", "");
        this.uniqueFileLocs.put(".xhtml", "");
        this.cumulativeData = new CumulativeData();
    }

    public ArrayList<ChapterFormat> getChapterFormat() {
        return chapterFormat;
    }

    public ArrayList<NonChapterXHTML> getNonChapterXHTML() {
        return nonChapterXHTML;
    }

    public ArrayList<IgnoreFile> getIgnoreFile() {
        return ignoreFile;
    }

    public ArrayList<xhtmlNav> getXhtmlNav() {
        return xhtmlNav;
    }

    public ArrayList<PhraseReplacements> getReplacements() {
        return replacements;
    }

    public String getOutputName() {
        return outputName;
    }

    public ReplacementData getReplacementData() {
        return replacementData;
    }

    public HashMap<String, String> getUniqueFileLocs() {
        return uniqueFileLocs;
    }

    public HashMap<String, String> getFileLocs() {
        return fileLocs;
    }

    public CumulativeData getCumulativeData() {
        return cumulativeData;
    }

    public String toString() {
        return "chapterFormat: " + 
                chapterFormat.toString() + 
                ", \nnonChapterXHTML: " + 
                nonChapterXHTML.toString() + 
                ", \nignoreFile: " + 
                ignoreFile.toString() + 
                ", \nxhtmlNav: " + 
                xhtmlNav.toString() + 
                ", \nreplacements: " + 
                replacements.toString() + 
                ", \noutputName: " + 
                outputName + 
                ", \nuniqueFileLocs: " + 
                uniqueFileLocs.toString() + 
                ", \nfileLocs: " + 
                fileLocs.toString() + 
                ", \nreplacementData: " + 
                replacementData.toString() + 
                ", \ncumulativeData: " + 
                cumulativeData.toString();
    }

    public boolean hasNav(String name) {
        for (xhtmlNav nav : xhtmlNav) {
            if (nav.getFormat().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNonChapterXHTML(String name) {
        for (NonChapterXHTML xhtml : nonChapterXHTML) {
            if (xhtml.getFileName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasIgnore(String name) {
        for (IgnoreFile xhtml : ignoreFile) {
            if (xhtml.getFileName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChapterFormat(String name) {
        name = name.replaceAll("[0-9]", "");
        for (ChapterFormat chapter: chapterFormat) {
            if (chapter.getFormat().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public FileType getFileType(String name) {
        if (hasIgnore(name)) {
            return FileType.IGNORE;
        } else if (hasNav(name)) {
            return FileType.NAVIGATION;
        } else if (hasNonChapterXHTML(name)) {
            return FileType.EXCLUSION;
        } else if (hasChapterFormat(name)) {
            return FileType.CHAPTER;
        } else {
            return FileType.OTHER;
        }
    }

    public PhraseReplacements getReplacement(String before) {
        for (PhraseReplacements replace : replacements) {
            if (replace.getBefore().equals(before)) {
                return replace;
            }
        }
        return null;
    }

    private void cleanFileOptions() {
        HashMap<String, Boolean> existing = new HashMap<String, Boolean>();

        Iterator<ChapterFormat> chapterIter = chapterFormat.iterator();
        while (chapterIter.hasNext()) {
            ChapterFormat chapter = chapterIter.next();
            String current = chapter.getFormat();
            if (existing.get(current) == null) {
                existing.put(current, true);
            } else {
                chapterIter.remove();
            }
        }
        existing = new HashMap<String, Boolean>();

        Iterator<NonChapterXHTML> nonChapterIter = nonChapterXHTML.iterator();
        while (nonChapterIter.hasNext()) {
            NonChapterXHTML nonChapter = nonChapterIter.next();
            String current = nonChapter.getFileName();
            if (existing.get(current) == null) {
                existing.put(current, true);
            } else {
                nonChapterIter.remove();
            }
        }

        Iterator<PhraseReplacements> phraseIter = replacements.iterator();
        while (phraseIter.hasNext()) {
            PhraseReplacements replacement = phraseIter.next();
            replacement.setBefore(replacement.getBefore().replaceAll("[<>]", ""));
            replacement.setAfter(replacement.getAfter().replaceAll("[<>]", ""));
            if (replacement.getBefore().equals("") || replacement.getAfter().equals("")) {
                phraseIter.remove();
            }
        }
    }
}
