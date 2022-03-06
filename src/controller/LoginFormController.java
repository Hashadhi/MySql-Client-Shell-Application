package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginFormController {
    public Button btnConnect;
    public Button btnExit;
    public PasswordField txtPassword;
    public TextField txtUserName;
    public TextField txtPort;
    public TextField txtHost;

    public void btnConnect_OnAction(ActionEvent actionEvent) {


    }

    private boolean isValidInputs() {
        String username = txtUserName.getText().trim();
        if (username.equals("")) {
            new Alert(Alert.AlertType.ERROR, "Username cannot be empty!").show();
            txtUserName.requestFocus();
            return false;
        } else if (!username.matches("[A-Za-z0-9]+")) {
            new Alert(Alert.AlertType.ERROR, "Enter a valid username").show();
            txtUserName.selectAll();
            return false;
        } else if (txtPassword.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Password cannot be empty").show();
            txtPassword.requestFocus();
            return false;
        } else if (txtHost.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Host cannot be empty").show();
            txtHost.requestFocus();
            return false;
        } else if (!txtPort.getText().matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid port!").show();
        }
        return true;
    }

    public void btnExit_OnAction(ActionEvent actionEvent) {((Stage) btnConnect.getScene().getWindow()).close();}
}
