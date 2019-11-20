package com.gmail.grigorij.ui.components.detailsdrawer;

import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DetailsDrawerHeader extends FlexBoxLayout {

    private static final String CLASS_NAME = "header-content";

    private Button close;
    private Label title;

    public DetailsDrawerHeader(String title) {
        addClassNames(CLASS_NAME);

        close = UIUtils.createButton(VaadinIcon.CLOSE, ButtonVariant.LUMO_TERTIARY_INLINE);
        close.addClassName(CLASS_NAME + "__close_button");

        this.title = UIUtils.createH3Label(title);
        this.title.addClassName(CLASS_NAME + "__title");

        add(close, this.title);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public Button getClose() {
        return this.close;
    }

    public FlexBoxLayout getContent() {
        return this;
    }
}
