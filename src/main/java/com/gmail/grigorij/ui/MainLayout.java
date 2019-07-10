package com.gmail.grigorij.ui;

import com.gmail.grigorij.backend.DatabaseDummyInsert;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.ui.views.authentication.LoginView;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Application UI entry point
 *
 *
 *  PWA iconPath: cannot be .svg -> causes lots of NullPointerExceptions
 *
 */
//@Push
@Route("")
@HtmlImport("frontend://styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(name = ProjectConstants.PROJECT_NAME_FULL, shortName = ProjectConstants.PROJECT_NAME_FULL, iconPath = ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_IMG_ONLY_PNG, backgroundColor = "#233348", themeColor = "#233348")
public class MainLayout extends Div {

	private static final Logger log = LoggerFactory.getLogger(MainLayout.class);
	private static final String CLASS_NAME = "root";


	public MainLayout() {

		//TODO:REMOVE AT PRODUCTION
		DatabaseDummyInsert dbDummy = new DatabaseDummyInsert();
		dbDummy.generateAndInsert();
		System.out.println("DatabaseDummyInsert done");


		if (UI.getCurrent() != null) {
			UI.getCurrent().getElement().setAttribute(LumoStyles.THEME, Lumo.DARK);
		} else {
			getElement().setAttribute(LumoStyles.THEME, Lumo.DARK);
		}

		setId("root-layout");
		addClassName(CLASS_NAME);
		setSizeFull();

		VaadinSession.getCurrent().setErrorHandler((ErrorHandler) errorEvent -> {
			log.error("Uncaught UI exception", errorEvent.getThrowable());
			System.out.println("-------------CRITICAL UI ERROR-------------");
			System.out.println(errorEvent.getThrowable());
			UIUtils.showNotification("We are sorry, but an internal error occurred", UIUtils.NotificationType.ERROR);
		});

		System.out.println();
		System.out.println("Authentication...");
		if (AuthenticationService.isAuthenticated()) {
			System.out.println("User is authenticated -> show menu");
			showMainMenuLayout();
		} else {
			System.out.println("User is not authenticated -> show login");
			showLoginView();
		}
	}

	private void showLoginView() {
		this.removeAll();

		add(new LoginView(new OperationStatus() {
			@Override
			public void onSuccess(String msg) {
				System.out.println(msg);

				showMainMenuLayout();
			}

			@Override
			public void onFail(String msg) {
				System.out.println(msg);
			}
		}));
	}

	private void showMainMenuLayout() {
		this.removeAll();

		add(new MenuLayout(this));
	}

	public void setThemeVariant(String themeVariant) {
		getElement().setAttribute(LumoStyles.THEME, "");
		if (UI.getCurrent() != null) {
			UI.getCurrent().getElement().setAttribute(LumoStyles.THEME, themeVariant);
		}
	}
}