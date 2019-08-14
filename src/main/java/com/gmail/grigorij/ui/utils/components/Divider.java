package com.gmail.grigorij.ui.utils.components;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Size;
import com.vaadin.flow.component.html.Div;

public class Divider extends FlexBoxLayout {

    public Divider(int colSpan, Size... sizes) {

        for (Size size : sizes) {
            for (String attribute : size.getPaddingAttributes()) {
                getStyle().set(attribute, size.getVariable());
            }
        }
        String height = "1px";

        getElement().getStyle().set("pointer-events", "none");
        setHeight(height);
        setWidth("100%");
        setAlignItems(Alignment.CENTER);

        Div d = new Div();
        d.setHeight(height);
        d.setWidth("100%");
        UIUtils.setBackgroundColor(LumoStyles.Color.Contrast._20, d);

//        if (colSpan > 0) {
//            UIUtils.setColSpan(colSpan, d);
//        }

        if (colSpan > 0) {
            UIUtils.setColSpan(colSpan, this);
        }

        add(d);
    }
}
