package com.gmail.grigorij.ui.utils.frames;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;

/**
 * A view frame that establishes app design guidelines. It consists of four
 * parts:
 * <ul>
 * <li>Center {@link #setViewContent(Component...) content}</li>
 * <li>Center {@link #setViewDetails(Component...) details}</li>
 * <li>Bottom {@link #setViewFooter(Component...) footer}</li>
 * </ul>
 */
public class SplitViewFrame extends Composite<Div> implements HasStyle {

	private final String CLASS_NAME = "view-frame";

	private final FlexBoxLayout wrapper = new FlexBoxLayout();
	private final Div content = new Div();
	private final Div details = new Div();
	private final Div footer = new Div();

	public enum Position {
		RIGHT, BOTTOM
	}

	public SplitViewFrame() {
		setClassName(CLASS_NAME);

		wrapper.setClassName(CLASS_NAME + "__wrapper");
		content.setClassName(CLASS_NAME + "__content");
		details.setClassName(CLASS_NAME + "__details");
		footer.setClassName(CLASS_NAME + "__footer");

		wrapper.add(content, details);
		getContent().add(wrapper, footer);
	}


	/**
	 * Sets the content slot's components.
	 */
	public void setViewContent(Component... components) {
		content.removeAll();
		content.add(components);
	}

	/**
	 * Sets the detail slot's components.
	 */
	protected void setViewDetails(Component... components) {
		details.removeAll();
		details.add(components);
	}

	protected void setViewDetailsPosition(Position position) {
		if (position.equals(Position.RIGHT)) {
			wrapper.setFlexDirection(FlexDirection.ROW);

		} else if (position.equals(Position.BOTTOM)) {
			wrapper.setFlexDirection(FlexDirection.COLUMN);
		}
	}

	/**
	 * Sets the footer slot's components.
	 */
	public void setViewFooter(Component... components) {
		footer.removeAll();
		footer.add(components);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
	}
}
