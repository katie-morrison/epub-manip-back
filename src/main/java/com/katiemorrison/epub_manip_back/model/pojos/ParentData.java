package com.katiemorrison.epub_manip_back.model.pojos;

public class ParentData {

    private String parentFile;
    private boolean isCopyOfFinal;

    
    public ParentData(String parentFile, boolean isCopyOfFinal) {
        this.parentFile = parentFile;
        this.isCopyOfFinal = isCopyOfFinal;
    }


    public String getParentFile() {
        return parentFile;
    }


    public boolean isCopyOfFinal() {
        return isCopyOfFinal;
    }
    
    
    
}
