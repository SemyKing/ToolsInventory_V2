package com.gmail.grigorij;

import com.gmail.grigorij.backend.DummyDataGenerator;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.ApplicationContainerView;
import com.gmail.grigorij.ui.views.LoginView;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;


/**
 *  Application UI entry point
 */
@Route("")
@Push(PushMode.MANUAL)
@PWA(name = ProjectConstants.PROJECT_NAME_FULL, shortName = ProjectConstants.PROJECT_NAME_FULL, iconPath = ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_IMG_ONLY_PNG)
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")

@CssImport("./styles/global-styles.css")
@CssImport(value = "./styles/components/vaadin-components/vaadin-notification-style.css", themeFor = "vaadin-notification-card")
public class MainLayout extends Div {

//	private static final Logger log = LoggerFactory.getLogger(MainLayout.class);


	/* MAIN T0D0 LIST */

	//TODO: ADD LOGGER FUNCTIONALITY IF NEEDED
	//TODO: ADD USER TOOL REPORT FUNCTIONALITY: LOST, STOLEN...
	//TODO: ADD TOOL GEOLOCATION
	//TODO: ADD IMPORT / EXPORT FUNCTIONALITY


	public MainLayout() {
		setSizeFull();
		addClassName("root");

		//TODO: REMOVE AT PRODUCTION
		DummyDataGenerator dummyDataGenerator = new DummyDataGenerator();
		dummyDataGenerator.generateDummyData();

		VaadinSession.getCurrent().setErrorHandler((ErrorHandler) errorEvent -> {
//			log.error("Uncaught UI exception", errorEvent.getThrowable());
			System.out.println("-------------CRITICAL UI ERROR-------------");
			errorEvent.getThrowable().printStackTrace();

			UIUtils.showNotification("We are sorry, but an internal error occurred", NotificationVariant.LUMO_ERROR);
		});

		System.out.println("\nAuthentication...");

		if (AuthenticationService.isAuthenticated()) {
			if (AuthenticationService.isActive()) {
				System.out.println("User authenticated -> construct main menu view");

				constructApplication();
				return;
			}
		}

//		showLoginView();
		constructLoginView();
	}

//	private void constructLoginView() {
//		LoginOverlay loginOverlay = new LoginOverlay();
//
//		loginOverlay.addLoginListener(e -> {
//			if (AuthenticationService.signIn(e.getUsername(), e.getPassword())) {
//				loginOverlay.close();
//				constructApplication();
//			} else {
//				loginOverlay.setError(true);
//				loginOverlay.setEnabled(true);
//			}
//		});
//
//		loginOverlay.addForgotPasswordListener(e -> {
//			ForgotPasswordDialog dialog = new ForgotPasswordDialog();
//			dialog.open();
//		});
//
//		loginOverlay.setTitle(ProjectConstants.PROJECT_NAME_FULL);
//		loginOverlay.setDescription("");
//
//		LoginI18n i18n = LoginI18n.createDefault();
//		loginOverlay.setI18n(i18n);
//
//		loginOverlay.setOpened(true);
//	}


	private void constructLoginView() {
		this.removeAll();

		add(new LoginView(this));
	}

	public void constructApplication() {
		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		transaction.setOperation(Operation.LOG_IN_T);
		TransactionFacade.getInstance().insert(transaction);

		this.removeAll();

		add(new ApplicationContainerView(this));
	}

	public void setThemeVariant(String themeVariant) {
		getElement().setAttribute(LumoStyles.THEME, "");
		if (UI.getCurrent() != null) {
			UI.getCurrent().getElement().setAttribute(LumoStyles.THEME, themeVariant);
		}
	}
}