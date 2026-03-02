package com.katiemorrison.epub_manip_back.util.ReplacementProcessor;

import com.katiemorrison.epub_manip_back.model.pojos.PhraseReplacements;

public class BeforeAfterProcessor implements ReplacementProcessor {
    private PhraseReplacements replacements;
    private String content;

    public BeforeAfterProcessor(PhraseReplacements replacements) {
        this.replacements = replacements;
        this.content = "";
    }

    @Override
    public String execute() {
        return content.replaceAll(replacements.getBefore(), replacements.getAfter());
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }
    
}
