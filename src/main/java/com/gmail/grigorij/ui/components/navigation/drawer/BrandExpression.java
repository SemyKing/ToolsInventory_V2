package com.gmail.grigorij.ui.components.navigation.drawer;

import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.gmail.grigorij.ui.utils.UIUtils;

@StyleSheet("styles/components/brand-expression.css")
public class BrandExpression extends Composite<Div> {

    private String CLASS_NAME = "brand-expression";

    private Image logo;
    private Label title;

    public BrandExpression(String text) {
        getContent().setClassName(CLASS_NAME);

        logo = new Image(ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_IMG_ONLY_SVG, "");
        logo.addClassName(CLASS_NAME + "__logo");
        logo.setAlt(text + " logo");

        title = UIUtils.createH3Label(text);
        title.addClassName(CLASS_NAME + "__title");

        getContent().add(logo, title);
    }
}
