package com.katiemorrison.epub_manip_back.model.pojos;

public class ChapterFormat {
    private int id;
    private String format;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }

    public String toString() {
        return "ChapterFormat(id:" + id + ", format:" + format + ")";
    }
    
}
