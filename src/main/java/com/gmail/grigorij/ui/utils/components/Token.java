package com.gmail.grigorij.ui.utils.components;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ICON;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.utils.css.FontSize;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.TextColor;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.BorderRadius;
import com.gmail.grigorij.ui.utils.css.Display;

public class Token extends Composite<FlexBoxLayout> implements HasStyle {

    private final String CLASS_NAME = "token";

    public Token(String text) {
        addClassName(CLASS_NAME);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setBackgroundColor(LumoStyles.Color.Primary._10);
        getContent().setBorderRadius(BorderRadius.M);
        getContent().setDisplay(Display.INLINE_FLEX);
        getContent().setPadding(Left.S);
        getContent().setPadding(Right.XS);
        getContent().setSpacing(Right.XS);

        Label label = UIUtils.createLabel(FontSize.S, TextColor.BODY, text);
        Button button = UIUtils.createButton(VaadinIcon.CLOSE_SMALL, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
        button.getThemeNames().remove(LUMO_ICON.getVariantName());

        getContent().add(label, button);
    }

}
