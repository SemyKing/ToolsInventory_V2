package com.gmail.grigorij.ui.components.detailsdrawer;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class DetailsDrawerFooter extends FlexBoxLayout {

    private static final String CLASS_NAME = "footer-content";

    private final Button save;
    private final Button cancel;

    public DetailsDrawerFooter() {
        addClassName(CLASS_NAME);

        cancel = UIUtils.createButton("Close", ButtonVariant.LUMO_CONTRAST);
        save = UIUtils.createButton("Save", ButtonVariant.LUMO_PRIMARY);
        add(cancel, save);
    }

    public Button getSave() {
        return save;
    }

    public Button getCancel() {
        return cancel;
    }

    public FlexBoxLayout getContent() {
        return this;
    }
}
