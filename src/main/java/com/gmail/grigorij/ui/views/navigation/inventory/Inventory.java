package com.gmail.grigorij.ui.views.navigation.inventory;

import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.tool.HierarchyType;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.ui.utils.components.Badge;
import com.gmail.grigorij.ui.utils.css.badge.BadgeShape;
import com.gmail.grigorij.ui.utils.css.badge.BadgeSize;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.forms.ToolForm;
import com.gmail.grigorij.ui.utils.frames.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

@PageTitle("Inventory")
public class Inventory extends SplitViewFrame {

	private static final String CLASS_NAME = "inventory";

	private ToolForm toolForm = new ToolForm();

	private TreeGrid<Tool> grid;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerFooter detailsDrawerFooter;

	private final MenuLayout menuLayout;

	public Inventory(MenuLayout menuLayout) {
		this.menuLayout = menuLayout;
		menuLayout.getAppBar().reset();

		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private Component createContent() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");


		content.add(initToolsGrid());

		return content;
	}

	private Component initToolsGrid() {
		List<Tool> tools = ToolFacade.getInstance().getAllToolsList();
		if (tools == null) {
			UIUtils.showNotification("Tools could not be loaded", UIUtils.NotificationType.ERROR);
			return new Span("Tools could not be loaded");
		}

		/*
		List must be sorted -> Parent must be added before child
		 */
		tools.sort(Comparator.comparing(Tool::getLevel).thenComparing(Tool::getName));

		grid = new TreeGrid<>();
		grid.setSizeFull();

		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});


		grid.addHierarchyColumn(Tool::getName).setHeader("Tools");

		ComponentRenderer<Badge, Tool> toolStatusRenderer = new ComponentRenderer<>(
				tool -> {
					ToolStatus status = tool.getStatus();
					Badge badge;
					if (status == null) {
						badge = new Badge("", null, null);
					} else {
						badge = new Badge(status.getStringValue(), status.getIcon(), status.getColor());
					}
					return badge;
				});
		grid.addColumn(toolStatusRenderer)
				.setHeader("Status")
				.setWidth(UIUtils.COLUMN_WIDTH_S)
				.setFlexGrow(0);

		ComponentRenderer<Span, Tool> toolUserRenderer = new ComponentRenderer<>(
				tool -> {
					ToolStatus status = tool.getStatus();
					Span username = new Span("");

					if (status != null) {
						if (status.equals(ToolStatus.IN_USE)) {
							if (tool.getInUseByUserId() <= 0) {
								System.out.println("User id is <= 0 for tool with 'IN USE' status");
								System.out.println("Tool id: " + tool.getId() + ", name: " + tool.getName());
							} else {
								username.setText(UserFacade.getInstance().getUserById(tool.getInUseByUserId()).getUsername());
							}
						}
					}
					return username;
				});
		grid.addColumn(toolUserRenderer)
				.setHeader("User")
				.setWidth(UIUtils.COLUMN_WIDTH_S)
				.setFlexGrow(0);

		tools.forEach(tool -> grid.getTreeData().addItem(tool.getParent(), tool));

		return grid;
	}

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(toolForm);
		detailsDrawer.getElement().setAttribute("large", true);

		// Header
		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("Details");

		detailsDrawer.setHeader(detailsDrawerTitle);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> saveOnClick());
		detailsDrawerFooter.getCancel().addClickListener(e -> detailsDrawer.hide());
		detailsDrawerFooter.getDelete().addClickListener(e -> deleteOnClick());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}

	private HierarchyType type;

	private void showDetails(Tool tool) {
		detailsDrawerFooter.getDelete().setEnabled( tool != null );
		type = HierarchyType.TOOL;

		if (tool != null) {
			type = tool.getHierarchyType();
		}

		if (toolForm.setTool(tool, type)) {
			detailsDrawer.show();
		} else {
			UIUtils.showNotification("Error opening details", UIUtils.NotificationType.ERROR);
		}
	}


	private void saveOnClick() {
		System.out.println("Tool saveOnClick()");

		Tool editedTool = toolForm.getTool();

		if (editedTool != null) {
			if (ToolFacade.getInstance().update(editedTool)) {

				grid.getDataProvider().refreshAll();
				grid.select(editedTool);

				UIUtils.showNotification(StringUtils.capitalize(type.toString()) + " saved", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification(StringUtils.capitalize(type.toString()) + " save ERROR", UIUtils.NotificationType.ERROR);
			}
		}
	}

	private void deleteOnClick() {

	}
}
