package com.katiemorrison.epub_manip_back.model.pojos;

public class RenameInfo {
    private boolean ignoreNode;
    private boolean isNavigationNode;
    private boolean isNCXNode;
    private String originalPath;
    private String newPath;
    private String content;

    public RenameInfo() {
        ignoreNode = false;
        isNavigationNode = false;
        isNCXNode = false;
        originalPath = null;
        newPath = null;
        content = null;
    }

    public boolean isIgnoreNode() {
        return ignoreNode;
    }
    public void setIgnoreNode(boolean ignoreNode) {
        this.ignoreNode = ignoreNode;
    }
    public boolean isNavigationNode() {
        return isNavigationNode;
    }
    public void setNavigationNode(boolean isNavigationNode) {
        this.isNavigationNode = isNavigationNode;
    }
    public boolean isNCXNode() {
        return isNCXNode;
    }
    public void setNCXNode(boolean isNCXNode) {
        this.isNCXNode = isNCXNode;
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
}
