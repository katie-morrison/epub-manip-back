package com.katiemorrison.epub_manip_back.model.pojos;

public class RenameInfo {
    private boolean ignoreNode;
    private boolean isNCXNode;
    private boolean noMatch;
    private boolean uniqueFileAlreadyExists;
    private boolean fileAlreadyProcessed;
    private String originalPath;
    private String newPath;
    private String content;
    private FileType type;

    public RenameInfo() {
        ignoreNode = false;
        isNCXNode = false;
        noMatch = false;
        uniqueFileAlreadyExists = false;
        fileAlreadyProcessed = false;
        originalPath = null;
        newPath = null;
        content = null;
        type = null;
    }

    public boolean isIgnoreNode() {
        return ignoreNode;
    }
    public void setIgnoreNode(boolean ignoreNode) {
        this.ignoreNode = ignoreNode;
    }
    public boolean isNCXNode() {
        return isNCXNode;
    }
    public void setNCXNode(boolean isNCXNode) {
        this.isNCXNode = isNCXNode;
    }
    public boolean isNoMatch() {
        return noMatch;
    }

    public void setNoMatch(boolean noMatch) {
        this.noMatch = noMatch;
    }

    public boolean isUniqueFileAlreadyExists() {
        return uniqueFileAlreadyExists;
    }

    public void setUniqueFileAlreadyExists(boolean uniqueFileAlreadyExists) {
        this.uniqueFileAlreadyExists = uniqueFileAlreadyExists;
    }

    public String getOriginalPath() {
        return originalPath;
    }
    
    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }
    public String getNewPath() {
        return newPath;
    }
    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public boolean isFileAlreadyProcessed() {
        return fileAlreadyProcessed;
    }

    public void setFileAlreadyProcessed(boolean fileAlreadyProcessed) {
        this.fileAlreadyProcessed = fileAlreadyProcessed;
    }

    public boolean isValidGeneral() {
        return !noMatch && !ignoreNode && !uniqueFileAlreadyExists && !fileAlreadyProcessed && type != FileType.IGNORE;
    }

    public boolean isValidReference() {
        return !noMatch && !ignoreNode && type == FileType.NAVIGATION;
    }

    public boolean isValidSecondOL() {
        return !noMatch && !ignoreNode && type != FileType.IGNORE;
    }

    
}
