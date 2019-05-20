package com.gmail.grigorij.ui.authentication;

import com.gmail.grigorij.ui.MainLayout;
import com.gmail.grigorij.ui.util.UIUtils;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * UI content when the user is not logged in yet.
 */
@HtmlImport("frontend://styles/views/login.html")
@PageTitle("Login")
@Route("login")
public class LoginView extends Div {

    private static final String CLASS_NAME = "login-screen";

    public LoginView() {
        buildUI();
    }


    private Label loginFailLabel;
    private TextField usernameField;
    private PasswordField passwordField;


    private void buildUI() {
        setSizeFull();
        setClassName(CLASS_NAME);


        Div info = new Div();
        info.setClassName(CLASS_NAME + "__info");

            Div logo = new Div();
            logo.setClassName(CLASS_NAME + "__logo");

        info.add(logo);
        info.add(new H1("Tools Inventory v2.0"));
        info.add(new H4("Easy way to monitor the status of all your tools."));

        Div form = new Div();
        form.setClassName(CLASS_NAME + "__form");

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setClassName(CLASS_NAME + "__vertical-layout");
            verticalLayout.add(new H2("Sign in"));

            usernameField = new TextField("Username");
            usernameField.setRequired(true);
            usernameField.setErrorMessage("Field must not be empty");
            usernameField.setRequiredIndicatorVisible(true);
            usernameField.getElement().setAttribute("name", "username");

            passwordField = new PasswordField("Password");
            passwordField.setRequired(true);
            passwordField.setErrorMessage("Field must not be empty");
            passwordField.setRequiredIndicatorVisible(true);

            Checkbox rememberMe = new Checkbox("Remember me");

            Button loginButton = UIUtils.createPrimaryButton("SIGN IN");
            loginButton.addClickShortcut(Key.ENTER);

            Button forgotPasswordButton = new Button("Forgot password");
            forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            loginFailLabel = new Label();
            loginFailLabel.setClassName(CLASS_NAME + "__login-fail");
            loginFailLabel.setText("");
            loginFailLabel.getStyle().set("visibility", "hidden");

            verticalLayout.add(usernameField);
            verticalLayout.add(passwordField);
            verticalLayout.add(rememberMe);
            verticalLayout.add(loginButton);
            verticalLayout.add(forgotPasswordButton);
            verticalLayout.add(loginFailLabel);

        form.add(verticalLayout);

        loginButton.addClickListener(e -> {
            validateAndLogIn(usernameField.getValue(), passwordField.getValue(), rememberMe.getValue());
        });

        forgotPasswordButton.addClickListener(e -> {
            PasswordRecoveryDialog forgotPasswordView = new PasswordRecoveryDialog();
            forgotPasswordView.open();
        });

        add(info);
        add(form);
    }


    private void validateAndLogIn(String username, String password, boolean rememberMe) {
        loginFailLabel.getStyle().set("visibility", "hidden");
        loginFailLabel.setText("");

        if (username.length() <= 0) {
            loginFailLabel.setText("Username must not be empty");
            loginFailLabel.getStyle().set("visibility", "visible");
            return;
        }

        if (password.length() <= 0) {
            loginFailLabel.setText("Password must not be empty");
            loginFailLabel.getStyle().set("visibility", "visible");
            return;
        }

        if (AuthService.signIn(username, password, rememberMe)) {
            getUI().ifPresent(ui -> ui.navigate(MainLayout.class));

            Notification
                    .show("Welcome " + username  +"!")
                    .setPosition(Notification.Position.TOP_CENTER);
        } else {
            showLoginFail();
        }
    }


    private void showLoginFail() {
        loginFailLabel.getStyle().set("visibility", "visible");
        loginFailLabel.setText("The username and password you entered do not match our records. Please double-check and try again");
        passwordField.setValue("");
    }
}
