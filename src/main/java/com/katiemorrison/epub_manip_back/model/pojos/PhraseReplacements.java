package com.katiemorrison.epub_manip_back.model.pojos;

public class PhraseReplacements {
    private int id;
    private String before;
    private String after;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getBefore() {
        return before;
    }
    public void setBefore(String before) {
        this.before = before;
    }
    public String getAfter() {
        return after;
    }
    public void setAfter(String after) {
        this.after = after;
    }

    public String toString() {
        return "Replacements(id:" + id + ", before:" + before + ", after:" + after + ")";
    }
}
