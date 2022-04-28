package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;

public class ShellFormController {
    public TextField txtCommand;
    public TextArea txtOutput;
    public Button btnRun;
    private Process mysql;
    private InputStream is;
    private InputStream es;
    private String password;

    public void initialize(){
        Platform.runLater(()-> txtCommand.requestFocus());
        txtOutput.setWrapText(true);
    }


    public void initData(String host, String port, String username, String password) {
        this.password = password;
        try {

//            -n --> flush buffer after each query
//            -v --> verbose/write more
            ProcessBuilder mysqlBuilder = new ProcessBuilder("mysql",
                    "-h", host,
                    "-u", username,
                    "--port", port,
                    "-n",
                    "-p",
                    "-v");

            this.mysql = mysqlBuilder.start();

            mysql.getOutputStream().write((password+"\n").getBytes());
            mysql.getOutputStream().flush();

            txtCommand.getScene().getWindow().setOnCloseRequest(event -> {
                if (this.mysql.isAlive()) {
                    this.mysql.destroy();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to establish the connection for some reason").show();
            if (mysql.isAlive()) {
                this.mysql.destroyForcibly();
            }
        }
    }

    public void btnExecute_OnAction(ActionEvent actionEvent) {
        try {
            String statement = txtCommand.getText();

            if (!statement.endsWith(";")) {
                statement += ";";
            }

            this.mysql.getOutputStream().write((statement +"\n").getBytes());
//            this.mysql.getOutputStream().flush();

            is = mysql.getInputStream();
            es = mysql.getErrorStream();



            if (is.available() != 0) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                txtOutput.setText(new String(buffer));
                System.out.println("is");

            }

            if (es.available() != 0) {
                byte[] buffer = new byte[is.available()];
                es.read(buffer);
                txtOutput.setText(new String(buffer));
                System.out.println("es");
                this.mysql.getOutputStream().write((password + "\n").getBytes());
                this.mysql.getOutputStream().flush();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        txtCommand.selectAll();

    }

    public void txtCommand_OnAction(ActionEvent actionEvent) {
        btnRun.fire();
    }
}
