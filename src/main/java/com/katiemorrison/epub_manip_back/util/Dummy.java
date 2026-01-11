package com.katiemorrison.epub_manip_back.util;

public interface Dummy {

    default String execute(String value) {
        System.out.println("Dummy.execute called without being overridden");
        return null;
    }
}
