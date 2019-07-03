package com.gmail.grigorij.ui.utils.css;

public enum BoxSizing {

    NONE("none"),
    BORDER_BOX("border-box"),
    CONTENT_BOX("content-box");

    private String value;

    BoxSizing(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
