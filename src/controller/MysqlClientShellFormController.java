package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MysqlClientShellFormController {
    public TextField txtCommand;
    public TextArea txtOutput;
    public Button btnRun;

    public void initData(String host, String port, String username, String password){

    }

    public void txtCommand_OnAction(ActionEvent actionEvent) {
        btnRun.fire();
    }

    public void btnRun_OnAction(ActionEvent actionEvent) {
    }
}
