package com.gmail.grigorij.ui.utils.components.detailsdrawer;

import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

public class DetailsDrawerFooter extends Composite<FlexBoxLayout> {

    private final Button save;
    private final Button cancel;
//    private final Button delete;

    public DetailsDrawerFooter() {
        getContent().setBackgroundColor(LumoStyles.Color.Contrast._5);
        getContent().setPadding(Horizontal.M, Vertical.XS);
//        getContent().setSpacing(Right.S);
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        getContent().setWidth("100%");

//        delete = UIUtils.createButton("Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);

        cancel = UIUtils.createButton("Close", ButtonVariant.LUMO_CONTRAST);
//        cancel.addClassName(LumoStyles.Margin.Left.AUTO);

        save = UIUtils.createButton("Save", ButtonVariant.LUMO_PRIMARY);
//        getContent().add(delete, cancel, save);
        getContent().add(cancel, save);
    }


    public Button getSave() {
        return save;
    }

    public Button getCancel() {
        return cancel;
    }

//    public Button getDelete() { return delete;}

    public void removeButton(Button button) {
        getContent().remove(button);
    }

    public void removeAllButtons() {
//        getContent().remove(save, cancel, delete);
        getContent().remove(save, cancel);
    }
}
