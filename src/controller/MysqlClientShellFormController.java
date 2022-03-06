package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;

public class MysqlClientShellFormController {
    public TextField txtCommand;
    public TextArea txtOutput;
    public Button btnRun;
    private Process mysql;

    public void initData(String host, String port, String username, String password){
        try {
            ProcessBuilder mysqlBuilder = new ProcessBuilder("mysql",
                    "-h", host,
                    "-u", username,
                    "--port", port,
                    "-p",
                    "-n",
                    "-v");

            this.mysql = mysqlBuilder.start();

            mysql.getOutputStream().write(password.getBytes());
            mysql.getOutputStream().flush();

            txtCommand.getScene().getWindow().setOnCloseRequest(event -> {
                if (this.mysql.isAlive()) {
                    this.mysql.destroy();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void txtCommand_OnAction(ActionEvent actionEvent) {
        btnRun.fire();
    }

    public void btnRun_OnAction(ActionEvent actionEvent) {
    }
}
