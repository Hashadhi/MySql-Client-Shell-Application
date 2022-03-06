package controller;

import javafx.event.ActionEvent;
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

    public void btnExit_OnAction(ActionEvent actionEvent) {((Stage) btnConnect.getScene().getWindow()).close();}
}
