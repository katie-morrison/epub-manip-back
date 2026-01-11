package com.katiemorrison.epub_manip_back.model.pojos;

public class IgnoreFile {
    private int id;
    private String fileName;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return "IgnoreFile(id:" + id + ", fileName:" + fileName + ")";
    }
}
