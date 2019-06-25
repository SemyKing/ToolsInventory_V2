package com.gmail.grigorij.ui.utils.components.detailsdrawer;

import com.vaadin.flow.component.html.Label;
import com.gmail.grigorij.ui.utils.css.BoxShadowBorders;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.BoxSizing;

public class DetailsDrawerHeader extends Label {

    public DetailsDrawerHeader(String title) {
        super(title);

        // Default styling
        addClassNames(LumoStyles.Heading.H3, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Vertical.M, BoxShadowBorders.BOTTOM);
        UIUtils.setBoxSizing(BoxSizing.BORDER_BOX, this);
        setWidth("100%");
    }
}
