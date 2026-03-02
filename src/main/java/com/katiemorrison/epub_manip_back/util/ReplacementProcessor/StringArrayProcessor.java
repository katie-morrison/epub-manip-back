package com.katiemorrison.epub_manip_back.util.ReplacementProcessor;

import java.util.ArrayList;

public class StringArrayProcessor implements ReplacementProcessor {
    private ArrayList<String> strings;

    public StringArrayProcessor(ArrayList<String> strings) {
        this.strings = strings;
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }

    public void setStrings(ArrayList<String> strings) {
        this.strings = strings;
    }

    @Override
    public void setContent(String content) {
        //Content not needed for this class
    }
    
}
