package com.gmail.grigorij.ui.views.navigation.inventory;

import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.tool.HierarchyType;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.Badge;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.ui.utils.forms.ToolForm;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.frames.SplitViewFrame;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;

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

		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private Component createContent() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.setMargin(Top.S);

		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		content.add(initToolsGrid());

		return content;
	}

	private Component initToolsGrid() {
		User currentUser = AuthenticationService.getSessionData().getUser();
		List<Tool> toolsAndCategoriesInCompany = null;

		if (currentUser == null) {
			AuthenticationService.signOut();
			UIUtils.showNotification("Current user is NULL", UIUtils.NotificationType.ERROR);
		} else {
			toolsAndCategoriesInCompany = ToolFacade.getInstance().getAllInCompany(currentUser.getCompanyId());
		}

		if (toolsAndCategoriesInCompany == null) {
			UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
			return new Span("No tools found in your company");
		} else {
			if (toolsAndCategoriesInCompany.size() <= 0) {
				UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
				return new Span("No tools found in your company");
			}

			/*
			List must be sorted -> Parent must be added before child
			*/
			toolsAndCategoriesInCompany.sort(Comparator.comparing(Tool::getLevel).thenComparing(Tool::getName));

			grid = new TreeGrid<>();
			grid.setSizeFull();

			grid.asSingleSelect().addValueChangeListener(e -> {
				if (grid.asSingleSelect().getValue() != null) {
					showDetails(grid.asSingleSelect().getValue());
				} else {
					detailsDrawer.hide();
				}
			});


			grid.addHierarchyColumn(Tool::getName).setHeader("Tools")
					.setWidth(UIUtils.COLUMN_WIDTH_XXL);

			ComponentRenderer<Badge, Tool> toolStatusRenderer = new ComponentRenderer<>(
					tool -> {
						ToolStatus status = tool.getUsageStatus();
						Badge badge;
						if (status == null) {
							badge = new Badge("", null, null);
						} else {
							badge = new Badge(status.getStringValue(), status.getIcon(), status.getColor());
						}
						badge.setWidth("100%");
						return badge;
					});
			grid.addColumn(toolStatusRenderer)
					.setHeader("Status")
					.setWidth(UIUtils.COLUMN_WIDTH_S)
					.setFlexGrow(0);

			ComponentRenderer<Span, Tool> toolUserRenderer = new ComponentRenderer<>(
					tool -> {
						ToolStatus status = tool.getUsageStatus();
						Span username = new Span("");
						username.setWidth("100%");

						if (status != null) {
							if (status.equals(ToolStatus.IN_USE)) {
								if (tool.getInUseByUserId() <= 0) {
									//TODO: ADD TO LOG
//								System.out.println("\nTool with '" + ToolStatus.IN_USE.getStringValue() + "' status is being used by unknown User with id <= 0");
//								System.out.println("Tool id: " + tool.getId() + ", name: " + tool.getName());
								} else {
									username.setText(UserFacade.getInstance().getUserById(tool.getInUseByUserId()).getUsername());
								}
							}
						}
						return username;
					});
			grid.addColumn(toolUserRenderer)
					.setHeader("User")
					.setWidth(UIUtils.COLUMN_WIDTH_S);



//			toolsAndCategoriesInCompany.forEach(tool -> {
//				System.out.println("----: " + tool.getLevel() + ", " + tool.getHierarchyType().toString() + ", " + tool.getName());
//			});


			/*
			Add data to grid
			 */
			toolsAndCategoriesInCompany.forEach(tool -> grid.getTreeData().addItem(tool.getParentCategory(), tool));

			grid.addItemClickListener(e -> {
				if (e != null) {
					if (e.getItem() != null) {
						Tool tool = e.getItem();
						if (tool.getHierarchyType().equals(HierarchyType.CATEGORY)) {
							if (grid.isExpanded(tool)) {
								grid.collapse(tool);
							} else {
								grid.expand(tool);
							}
							grid.select(null);
						}
					}
				}
			});

			return grid;
		}
	}

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(toolForm);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.removeButton(detailsDrawerFooter.getSave());
		detailsDrawerFooter.removeButton(detailsDrawerFooter.getDelete());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}

	private void showDetails(Tool tool) {
		detailsDrawerFooter.getDelete().setEnabled( tool != null );

		if (tool != null) {
			if (tool.getHierarchyType() != null) {
				if (tool.getHierarchyType().equals(HierarchyType.TOOL)) {

					toolForm.setTool(tool);
					detailsDrawer.show();

					UIUtils.updateFormSize(toolForm);
//				} else if (tool.getHierarchyType().equals(HierarchyType.CATEGORY)) {
////					if (grid.isExpanded(tool)) {
////						grid.collapse(tool);
////					} else {
////						grid.expand(tool);
////					}
////					grid.select(null);
//
				}
			}
		}
	}
}
