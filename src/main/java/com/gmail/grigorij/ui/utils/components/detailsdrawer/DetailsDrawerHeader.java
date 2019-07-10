package com.gmail.grigorij.ui.utils.components.detailsdrawer;

import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.*;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DetailsDrawerHeader extends FlexBoxLayout {

    private Button close;
    private Label title;
    private FlexBoxLayout container;

    public DetailsDrawerHeader(String title) {
        setDisplay(Display.FLEX);
        setFlexDirection(FlexDirection.ROW);
        setAlignItems(Alignment.CENTER);

        addClassNames(LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Vertical.XS, BoxShadowBorders.BOTTOM);
        UIUtils.setBoxSizing(BoxSizing.BORDER_BOX, this);
        setWidth("100%");

        this.close = UIUtils.createTertiaryInlineButton(VaadinIcon.CLOSE);
        this.close.removeThemeVariants(ButtonVariant.LUMO_ICON);
        this.close.getElement().getStyle().set("line-height", "1");

        this.title = UIUtils.createH3Label(title);


        container = new FlexBoxLayout(this.close, this.title);
        container.setSizeFull();
        container.setAlignItems(Alignment.CENTER);
        container.setComponentPadding(this.close, Right.M);
        add(container);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public Label getTitleLabel() {
        return this.title;
    }

    public Button getClose() {
        return this.close;
    }

    public FlexBoxLayout getContainer() {
        return this.container;
    }

    public void removeBoxing() {
        UIUtils.setBoxSizing(BoxSizing.NONE, this);
    }
    public void removeStyles() {
        removeClassNames(LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.M, LumoStyles.Padding.Bottom.S, BoxShadowBorders.BOTTOM);
    }
}
