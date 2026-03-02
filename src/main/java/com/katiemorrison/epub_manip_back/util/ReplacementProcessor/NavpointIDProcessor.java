package com.katiemorrison.epub_manip_back.util.ReplacementProcessor;

public class NavpointIDProcessor implements ReplacementProcessor {
    private int ncxId;

    public NavpointIDProcessor() {
        this.ncxId = 0;
    }

    @Override
    public String execute() {
        return "navPoint-" + ncxId + "\" playOrder=\"" + ncxId;
    }

    @Override
    public void setContent(String content) {
        //Content not needed for this class
    }

    public void setNcxId(int ncxId) {
        this.ncxId = ncxId;
    }
    
}
