package com.gmail.grigorij.ui.components.detailsdrawer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.layout.size.Horizontal;
import com.gmail.grigorij.ui.layout.size.Right;
import com.gmail.grigorij.ui.layout.size.Vertical;
import com.gmail.grigorij.ui.util.LumoStyles;
import com.gmail.grigorij.ui.util.UIUtils;

public class DetailsDrawerFooter extends Composite<FlexBoxLayout> {

    private final Button save;
    private final Button cancel;

    public DetailsDrawerFooter() {
        getContent().setBackgroundColor(LumoStyles.Color.Contrast._5);
        getContent().setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
        getContent().setSpacing(Right.S);
        getContent().setWidth("100%");

        save = UIUtils.createPrimaryButton("Save");
        cancel = UIUtils.createTertiaryButton("Cancel");
        getContent().add(save, cancel);
    }

    public Registration addSaveListener(ComponentEventListener<ClickEvent<Button>> listener) {
        return save.addClickListener(listener);
    }

    public Registration addCancelListener(ComponentEventListener<ClickEvent<Button>> listener) {
        return cancel.addClickListener(listener);
    }

}
