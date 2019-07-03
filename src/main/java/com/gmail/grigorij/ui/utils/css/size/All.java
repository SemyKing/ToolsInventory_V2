package com.gmail.grigorij.ui.utils.css.size;

public enum All implements Size {

    AUTO("auto", null),
    NONE("0", null),

    XS("var(--lumo-space-xs)", "spacing-r-xs"),
    S("var(--lumo-space-s)", "spacing-r-s"),
    M("var(--lumo-space-m)", "spacing-r-m"),
    L("var(--lumo-space-l)", "spacing-r-l"),
    XL("var(--lumo-space-xl)", "spacing-r-xl"),

    RESPONSIVE_M("var(--lumo-space-r-m)", null),
    RESPONSIVE_L("var(--lumo-space-r-l)", null),
    RESPONSIVE_X("var(--lumo-space-r-x)", null);

    private String variable;
    private String spacingClassName;

    All(String variable, String spacingClassName) {
        this.variable = variable;
        this.spacingClassName = spacingClassName;
    }

    @Override
    public String[] getMarginAttributes() {
        return new String[] { "margin" };
    }

    @Override
    public String[] getPaddingAttributes() {
        return new String[] { "padding" };
    }

    @Override
    public String getSpacingClassName() {
        return this.spacingClassName;
    }

    @Override
    public String getVariable() {
        return this.variable;
    }
}
