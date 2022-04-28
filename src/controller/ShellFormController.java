package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShellFormController {
    public TextField txtCommand;
    public Button btnExecute;
    public TextArea txtOutput;
    public Label lblCurrentSchema;
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
                    "-v",
                    "-L",
                    "-f");

            mysqlBuilder.redirectErrorStream(true);
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
            Platform.exit();
        }
    }

    public void btnExecute_OnAction(ActionEvent actionEvent) {
        try {
            String statement = txtCommand.getText();

            if (!statement.endsWith(";")) {
                statement += ";";
            }

            txtOutput.clear();

            if (statement.equalsIgnoreCase("exit")) {
                Platform.exit();
                return;
            }

            this.mysql.getOutputStream().write((statement +"\n").getBytes());
            this.mysql.getOutputStream().flush();
            txtCommand.selectAll();

            Pattern pattern = Pattern.compile(".*[;]?((?i)(use)) (?<db>[A-Za-z0-9-_]+);.*");
            Matcher matcher = pattern.matcher(statement);
            if (matcher.matches()) {
                lblCurrentSchema.setText("SCHEMA" + matcher.group("db"));
                txtOutput.setText("Database changed");
                lblCurrentSchema.setTextFill(Paint.valueOf("blue"));

            }


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
        btnExecute.fire();
    }
}
