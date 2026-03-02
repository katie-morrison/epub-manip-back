package com.katiemorrison.epub_manip_back.util.ReplacementProcessor;

import com.katiemorrison.epub_manip_back.util.FileOptions;

public class OPFLocationProcessor implements ReplacementProcessor {
    private FileOptions fileOptions;

    public OPFLocationProcessor(FileOptions fileOptions) {
        this.fileOptions = fileOptions;
    }

    @Override
    public String execute() {
        return fileOptions.getUniqueFileLocs().get(".opf");
    }

    @Override
    public void setContent(String content) {
        //Content not needed for this class
    }
    
}
