package com.gmail.grigorij.ui.views.routes;

import com.gmail.grigorij.backend.database.entities.RecoveryLink;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.facades.RecoveryLinkFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.dialogs.password.ChangePasswordView;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.authentication.PasswordUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

/**
 * Page accessible form URL containing unique code
 */

@Route(value = ProjectConstants.RESET_PASSWORD_URL)
@StyleSheet("context://styles/views/password-reset.css")
public class ResetPasswordView extends Div implements HasUrlParameter<String> {

	private static final String CLASS_NAME = "reset-password-view";

	private Div content;
	private RecoveryLink link;


	public ResetPasswordView() {
		setClassName(CLASS_NAME);

		add(constructContent());
	}


	private Div constructContent() {
		content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		return content;
	}


	@Override
	public void setParameter(BeforeEvent event, String tokenParameter) {
		tokenParameter = tokenParameter.replaceAll("[^a-zA-Z]", "");

		if (tokenParameter.length() < ProjectConstants.RECOVERY_TOKEN_LENGTH || tokenParameter.length() > ProjectConstants.RECOVERY_TOKEN_LENGTH) {
			System.err.println("Token length is Invalid");
			showTokenInvalid();
			return;
		}

		link = RecoveryLinkFacade.getInstance().getRecoveryLinkByToken(tokenParameter);

		if (link == null) {
			System.err.println("RecoveryLink is NULL");
			showTokenInvalid();
		} else {
			if (link.isDeleted()) {
				System.err.println("RecoveryLink is 'DELETED'");
				showTokenInvalid();
			}

			showPasswordReset();
		}
	}


	private void showTokenInvalid() {
		Span message = new Span("Password reset link is invalid or has expired");
		message.addClassName(CLASS_NAME + "__exp-link");

		content.removeAll();
		content.add(message);
	}

	private void showPasswordReset() {
		content.removeAll();

		User user = link.getUser();

		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnOutsideClick(false);
		dialog.setCloseOnEsc(false);

		dialog.setHeader(UIUtils.createH3Label("Reset Password"));

		ChangePasswordView view = new ChangePasswordView(user);
		dialog.setContent(view);

		dialog.getCancelButton().setText("");
		dialog.getCancelButton().setEnabled(false);

		dialog.getConfirmButton().setText("Reset");
		dialog.getConfirmButton().addClickListener(e -> {
			if (view.isValid()) {

				String salt = PasswordUtils.getSalt(30);
				user.setSalt(salt);
				user.setPassword(PasswordUtils.generateSecurePassword(view.getNewPassword(), salt));

				dialog.close();

				if (UserFacade.getInstance().update(user)) {
					UIUtils.showNotification("Password reset", NotificationVariant.LUMO_SUCCESS);

					Transaction transaction = new Transaction();
					transaction.setUser(user);
					transaction.setCompany(user.getCompany());
					transaction.setOperation(Operation.CHANGE);
					transaction.setOperationTarget1(OperationTarget.PASSWORD);
					TransactionFacade.getInstance().insert(transaction);

					if (!RecoveryLinkFacade.getInstance().remove(link)) {
						System.err.println("Error removing RecoveryLink");

						link.setDeleted(true);
						RecoveryLinkFacade.getInstance().update(link);
					}

					if (UI.getCurrent() != null) {
						UI.getCurrent().navigate("");
					}
				} else {
					UIUtils.showNotification("Password reset Error", NotificationVariant.LUMO_ERROR);
				}
			}
		});

		dialog.open();
	}
}
