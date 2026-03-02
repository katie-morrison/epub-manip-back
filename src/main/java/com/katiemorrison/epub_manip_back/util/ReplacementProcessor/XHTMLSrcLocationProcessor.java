package com.katiemorrison.epub_manip_back.util.ReplacementProcessor;

import com.katiemorrison.epub_manip_back.util.FileOptions;
import com.katiemorrison.epub_manip_back.util.FileParts;

public class XHTMLSrcLocationProcessor implements ReplacementProcessor {
    FileOptions fileOptions;
    String content;


    public XHTMLSrcLocationProcessor(FileOptions fileOptions) {
        this.fileOptions = fileOptions;
        this.content = "";
    }

    @Override
    public String execute() {
        FileParts fileParts = new FileParts(content);
        return "/" + fileOptions.getFileLocs().get(fileParts.getName() + fileParts.getExt());
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
