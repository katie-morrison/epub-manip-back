package com.katiemorrison.epub_manip_back.model.pojos;

public class RenameDetails {

    private String newName;
    private String originalParent;
    private String originalName;

    public RenameDetails(String originalName, String newName, String originalParent) {
        this.originalName = originalName;
        this.newName = newName;
        this.originalParent = originalParent;
    }

    public String getNewName() {
        return newName;
    }
    public String getOriginalParent() {
        return originalParent;
    }
    public String getOriginalName() {
        return originalName;
    }

    public String toString() {
        return "RenameDetails(originalName: " + originalName + ", newName:" + newName + ", originalParent:" + originalParent + ")";
    }
    
}
