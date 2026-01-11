package com.katiemorrison.epub_manip_back.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.katiemorrison.epub_manip_back.model.pojos.FileType;
import com.katiemorrison.epub_manip_back.model.pojos.RenameDetails;

public class ReplacementData {

    private static final String EXCLUSION_RENAME = "exclusion";
    private static final String CHAPTER_RENAME = "chapter";
    private static final String OTHER_RENAME = "other";

    private int exclusionsInd;
    private int chaptersInd;
    private int othersInd;
    private HashMap<String, Integer> indices;
    private HashMap<String, RenameDetails> exclusions;
    private ArrayList<RenameDetails> chapters;
    private ArrayList<RenameDetails> others;
    private HashMap<String, HashMap<String, Boolean>> recordedFiles;

    public ReplacementData() {
        this.exclusionsInd = 0;
        this.chaptersInd = 0;
        this.othersInd = 0;
        this.indices = new HashMap<String, Integer>();
        this.indices.put(".opf", 0);
        this.indices.put(".ncx", 0);
        this.indices.put(".xhtml", 0);
        this.exclusions = new HashMap<String, RenameDetails>();
        this.chapters = new ArrayList<RenameDetails>();
        this.others = new ArrayList<RenameDetails>();
        this.recordedFiles = new HashMap<String, HashMap<String, Boolean>>();
        this.recordedFiles.put(".opf", new HashMap<String, Boolean>());
        this.recordedFiles.put(".ncx", new HashMap<String, Boolean>());
        this.recordedFiles.put(".xhtml", new HashMap<String, Boolean>());
    }

    public HashMap<String, HashMap<String, Boolean>> getRecordedFiles() {
        return recordedFiles;
    }

    public HashMap<String, Integer> getIndices() {
        return indices;
    }

    public HashMap<String, RenameDetails> getExclusions() {
        return exclusions;
    }

    public ArrayList<RenameDetails> getChapters() {
        return chapters;
    }

    public RenameDetails generateNewName(String originalName, String parentEpub, FileType type) {
        if (type == FileType.CHAPTER) {
            return generateChapterName(originalName, parentEpub);
        } else if (type == FileType.EXCLUSION) {
            return generateExclusionName(originalName, parentEpub);
        } else if (type == FileType.OTHER) {
            return generateOtherName(originalName, parentEpub);
        } else {
            System.err.println("Invalid FileType " + type + " supplied to generateNewName");
            return null;
        }
    }

    public RenameDetails generateExclusionName(String originalName, String parentEpub) {
        RenameDetails entry = exclusions.get(originalName);
        if (entry == null) {
            String newName = EXCLUSION_RENAME + exclusionsInd;
            exclusionsInd++;
            entry = new RenameDetails(originalName, newName, parentEpub);
            exclusions.put(originalName, entry);
        }
        return entry;
    }

    public RenameDetails generateChapterName(String originalName, String parentEpub) {
        String newName = CHAPTER_RENAME + chaptersInd;
        chaptersInd++;
        RenameDetails entry = new RenameDetails(originalName, newName, parentEpub);
        chapters.add(entry);
        return entry;
    }

    public RenameDetails generateOtherName(String originalName, String parentEpub) {
        String newName = OTHER_RENAME + othersInd;
        othersInd++;
        RenameDetails entry = new RenameDetails(originalName, newName, parentEpub);
        others.add(entry);
        return entry;
    }

    public String getChapterName(String originalName, String parentFileName) {
        for (RenameDetails detail: chapters) {
            if (detail.getOriginalName().equals(originalName) && detail.getOriginalParent().equals(parentFileName)) {
                return detail.getNewName();
            }
        }
        return null;
    }

    public String getOtherName(String originalName, String parentFileName) {
        for (RenameDetails detail: others) {
            if (detail.getOriginalName().equals(originalName) && detail.getOriginalParent().equals(parentFileName)) {
                return detail.getNewName();
            }
        }
        return null;
    }

    public String toString() {
        return "\nexclusionsInd: " + 
                exclusionsInd + 
                "\n, indices: " + 
                indices.toString() + 
                "\n, chapters: " + 
                chapters.toString() + 
                "\n, exclusions: " + 
                exclusions.toString() + 
                "\n, others: " + 
                others.toString();
    }

    
}
