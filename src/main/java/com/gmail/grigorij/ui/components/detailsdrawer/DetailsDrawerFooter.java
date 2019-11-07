package com.gmail.grigorij.ui.components.detailsdrawer;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class DetailsDrawerFooter extends FlexBoxLayout {

    private static final String CLASS_NAME = "footer-content";

    private final Button close;
    private final Button save;


    public DetailsDrawerFooter() {
        addClassName(CLASS_NAME);

        close = UIUtils.createButton("Close", ButtonVariant.LUMO_PRIMARY);
        save = UIUtils.createButton("Save", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        add(close, save);
    }

    public Button getSave() {
        return save;
    }

    public Button getClose() {
        return close;
    }

    public FlexBoxLayout getContent() {
        return this;
    }
}
