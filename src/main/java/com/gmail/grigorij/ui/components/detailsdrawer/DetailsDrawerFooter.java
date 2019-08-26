package com.gmail.grigorij.ui.components.detailsdrawer;

import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class DetailsDrawerFooter extends Composite<FlexBoxLayout> {

    private final Button save;
    private final Button cancel;

    public DetailsDrawerFooter() {
        getContent().setBackgroundColor(LumoStyles.Color.Contrast._5);
        getContent().setPadding(Horizontal.M, Vertical.XS);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        getContent().setWidth("100%");

        cancel = UIUtils.createButton("Close", ButtonVariant.LUMO_CONTRAST);

        save = UIUtils.createButton("Save", ButtonVariant.LUMO_PRIMARY);
        getContent().add(cancel, save);
    }

    public Button getSave() {
        return save;
    }

    public Button getCancel() {
        return cancel;
    }

    public void removeButton(Button button) {
        getContent().remove(button);
    }
}
