package com.katiemorrison.epub_manip_back.util;

import java.io.File;

public class FileParts {

    private String dir;
    private String name;
    private String ext;

    public FileParts(String dir, String name, String ext) {
        this.dir = dir;
        this.name = name;
        this.ext = ext;
    }

    public FileParts(String fullPath) {
        String dir = fullPath;
        String name = fullPath;
        String ext = fullPath;
        int extInd = ext.lastIndexOf(".");
        int slashInd = ext.lastIndexOf(File.separator);
        if (extInd == -1) {
            ext = "";
        } else {
            ext = ext.substring(ext.lastIndexOf("."));
            name = name.substring(0, extInd);
        }
        if (slashInd != -1) {
            dir = dir.substring(0, slashInd + 1);
            name = name.substring(slashInd + 1);
        } else {
            dir = "";
        }
        this.dir = dir;
        this.name = name;
        this.ext = ext;
    }

    public String getDir() {
        return dir;
    }

    public String getName() {
        return name;
    }

    public String getExt() {
        return ext;
    }

    public String getRootDir() {
        return getRootDir(dir);
    }

    public String toString() {
        return dir + name + ext;
    }

    private String getRootDir(String dir) {
        int slashInd = dir.indexOf(File.separator);
        if (slashInd < 0) {
            return "";
        } else if (slashInd == 0) {
            return getRootDir(dir.substring(1));
        } else {
            return dir.substring(0, slashInd);
        }
    }
    
}
