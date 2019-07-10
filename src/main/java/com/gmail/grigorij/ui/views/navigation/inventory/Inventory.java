package com.gmail.grigorij.ui.views.navigation.inventory;

import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.entities.tool.HierarchyType;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
import com.gmail.grigorij.ui.utils.components.CustomBadge;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.components.frames.SplitViewFrame;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.ui.utils.forms.ToolForm;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;


@PageTitle("Inventory")
public class Inventory extends SplitViewFrame {

	private static final String CLASS_NAME = "inventory";

	private ToolForm toolForm = new ToolForm();

	private TreeGrid<Tool> grid;
	private TreeDataProvider<Tool> dataProvider;

	private DetailsDrawer detailsDrawer;

	private final MenuLayout menuLayout;

	private Button takeToolButton;
	private Button reserveToolButton;
	private Button reportToolButton;

	public Inventory(MenuLayout menuLayout) {
		this.menuLayout = menuLayout;

//		setClassName(CLASS_NAME);

		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private Component createContent() {
		FlexBoxLayout wrapper = new FlexBoxLayout();
		wrapper.setClassName(CLASS_NAME + "__wrapper");
		wrapper.setFlexDirection(FlexDirection.COLUMN);

		//HEADER
		FlexBoxLayout header = new FlexBoxLayout();
		header.setClassName(CLASS_NAME + "__header");
		header.setWidthFull();
		header.setFlexDirection(FlexDirection.ROW);
		header.setMargin(Top.S);

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");

		header.add(searchField);

		FlexBoxLayout buttons = new FlexBoxLayout();
		buttons.setClassName(CLASS_NAME + "__header-buttons");
		buttons.setMargin(Left.S);

		takeToolButton = UIUtils.createButton("Take", ButtonVariant.LUMO_CONTRAST);
		takeToolButton.addClickListener(e -> {
			if (e != null) {
				Tool tool = grid.asSingleSelect().getValue();
				if (tool != null) {

					if (tool.getUser() == null) {
						takeTool(tool);
					} else {
						if (tool.getUser().equals(AuthenticationService.getSessionData().getUser())) {
							UIUtils.showNotification("You already have this tool", UIUtils.NotificationType.INFO);
						} else {
							takeTool(tool);
						}
					}
				}
			}
		});
		takeToolButton.setEnabled(false);

		reserveToolButton = UIUtils.createButton("Reserve", ButtonVariant.LUMO_CONTRAST);
		reserveToolButton.addClickListener(e -> {
			if (e != null) {
				Tool tool = grid.asSingleSelect().getValue();
				if (tool != null) {

					if (tool.getReservedByUser() == null) {
						reserveTool(tool);
					} else {
						if (tool.getReservedByUser().equals(AuthenticationService.getSessionData().getUser())) {
							UIUtils.showNotification("You have already reserved this tool", UIUtils.NotificationType.INFO);
						} else {
							reserveTool(tool);
						}
					}
				}
			}
		});
		reserveToolButton.setEnabled(false);

		reportToolButton = UIUtils.createButton("Report", VaadinIcon.EXCLAMATION, ButtonVariant.LUMO_ERROR);
		reportToolButton.addClickListener(e -> {
			if (e != null) {
				if (grid.asSingleSelect().getValue() != null) {
					reportTool(grid.asSingleSelect().getValue());
				}
			}
		});
		reportToolButton.setEnabled(false);

		buttons.setComponentMargin(reserveToolButton, Left.S);
		buttons.setComponentMargin(reportToolButton, Left.S);

		buttons.add(takeToolButton, reserveToolButton, reportToolButton);
		header.add(buttons);

		wrapper.add(header);

		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.setSizeFull();
		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		content.add(createGrid());

		wrapper.add(content);
		return wrapper;
	}

	private Component createGrid() {
		User currentUser = AuthenticationService.getSessionData().getUser();

		if (currentUser == null) {
			AuthenticationService.signOut();
			UIUtils.showNotification("Current user is NULL", UIUtils.NotificationType.ERROR);
		} else {
			dataProvider = new TreeDataProvider<>(ToolFacade.getInstance().getSortedToolsAndCategoriesByCompany(currentUser.getCompany().getId()));
		}

		if (dataProvider == null) {
			UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
			return new Span("No tools found in your company");
		} else {
			if (dataProvider.getTreeData().getRootItems().size() <= 0) {
				UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
				return new Span("No tools found in your company");
			}

			grid = new TreeGrid<>();
			grid.setSizeFull();

			grid.asSingleSelect().addValueChangeListener(e -> {
				if (grid.asSingleSelect().getValue() != null) {
					showDetails(grid.asSingleSelect().getValue());
					handleButtonsAvailability(grid.asSingleSelect().getValue());
				} else {
					detailsDrawer.hide();
					handleButtonsAvailability(null);
				}
			});

			grid.setDataProvider(dataProvider);

			grid.addHierarchyColumn(Tool::getName).setHeader("Tools")
					.setWidth(UIUtils.COLUMN_WIDTH_XXL);

			ComponentRenderer<FlexBoxLayout, Tool> toolStatusRenderer = new ComponentRenderer<>(
					tool -> {
						FlexBoxLayout layout = new FlexBoxLayout();
						ToolStatus status = tool.getUsageStatus();
						if (status != null) {
							layout = new CustomBadge(status.getStringValue(), status.getColor(), status.getIcon());
						}
						return layout;
					});
			grid.addColumn(toolStatusRenderer)
					.setHeader("Status")
					.setWidth(UIUtils.COLUMN_WIDTH_S)
					.setFlexGrow(0);

			//FOR DEBUG
//			toolsAndCategoriesInCompany.forEach(tool -> {
//				System.out.println("----: " + tool.getLevel() + ", " + tool.getHierarchyType().toString() + ", " + tool.getName());
//			});

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

	private void handleButtonsAvailability(Tool tool) {

		takeToolButton.setEnabled(false);
		reserveToolButton.setEnabled(false);
		reportToolButton.setEnabled(false);

		if (tool == null) {
			return;
		}
		if (tool.getHierarchyType().equals(HierarchyType.CATEGORY)) {
			return;
		}

		reportToolButton.setEnabled(true);

		if (tool.getUsageStatus().equals(ToolStatus.LOST)) {
			return;
		}
		if (tool.getUsageStatus().equals(ToolStatus.BROKEN)) {
			return;
		}
		if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
			return;
		}

		if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
			if (!tool.getUser().equals(AuthenticationService.getSessionData().getUser())) {
				reserveToolButton.setEnabled(true);
			}
		}

		if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
			takeToolButton.setEnabled(true);
		}
	}

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(toolForm);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Tool Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.removeButton(detailsDrawerFooter.getSave());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}

	private void showDetails(Tool tool) {
//		detailsDrawerFooter.getDelete().setEnabled( tool != null );

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

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}

	private void takeTool(Tool tool) {
		System.out.println("takeTool: " + tool.getName());

		if (!tool.getUsageStatus().equals(ToolStatus.FREE)) {
			if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
				UIUtils.showNotification("Tool is currently in use and was reserved by another user", UIUtils.NotificationType.INFO);
				refreshDetails(tool);
				return;
			}
			if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {

				ConfirmDialog confirmDialog = new ConfirmDialog("Tool was taken by another user. Would you like to reserve it?");
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(e -> {

					if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
						UIUtils.showNotification("Tool was reserved by another user", UIUtils.NotificationType.INFO);
					} else {
						tool.setReservedByUser(AuthenticationService.getSessionData().getUser());
						tool.setUsageStatus(ToolStatus.RESERVED);

						if (ToolFacade.getInstance().update(tool)) {
							UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
						} else {
							UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
						}
					}
					refreshDetails(tool);
					confirmDialog.close();
				});
				confirmDialog.open();
			}
		} else {    // TOOL STATUS FREE
			tool.setUser(AuthenticationService.getSessionData().getUser());
			tool.setUsageStatus(ToolStatus.IN_USE);

			if (ToolFacade.getInstance().update(tool)) {
				UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}
		}

		refreshDetails(tool);
	}

	private void reserveTool(Tool tool) {
		System.out.println("reserveTool: " + tool.getName());

		if (!tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
			if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
				UIUtils.showNotification("Tool was reserved by another user", UIUtils.NotificationType.INFO);
				refreshDetails(tool);
				return;
			}
			if (tool.getUsageStatus().equals(ToolStatus.FREE)) {

				ConfirmDialog confirmDialog = new ConfirmDialog("Tool currently free. Would you like to take it?");
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(e -> {

					if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
						UIUtils.showNotification("Tool was taken by another user", UIUtils.NotificationType.INFO);
					} else {
						tool.setUser(AuthenticationService.getSessionData().getUser());
						tool.setUsageStatus(ToolStatus.IN_USE);

						if (ToolFacade.getInstance().update(tool)) {
							UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
						} else {
							UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
						}
					}
					refreshDetails(tool);
					confirmDialog.close();
				});
				confirmDialog.open();
			}
		} else {    // TOOL STATUS IN USE
			tool.setReservedByUser(AuthenticationService.getSessionData().getUser());
			tool.setUsageStatus(ToolStatus.RESERVED);

			if (ToolFacade.getInstance().update(tool)) {
				UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
			}
		}
		refreshDetails(tool);
	}

	private void refreshDetails(Tool tool) {
		if (detailsDrawer.isOpen()) {
			detailsDrawer.hide();
			showDetails(tool);
		}
		handleButtonsAvailability(tool);
		dataProvider.refreshAll();
	}


	private void reportTool(Tool tool) {
		if (tool == null) {
			System.err.println("Reporting NULL tool");
			return;
		}

		System.out.println("Reporting Tool: " + tool.getName());


	}
}
