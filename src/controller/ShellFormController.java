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

            processInputStream(mysql.getInputStream());

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

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void processInputStream(InputStream is) {
        new Thread(()->{
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    int read = is.read(buffer);

                    if (read == -1) {
                        break;
                    }

                    String output = new String(buffer, 0, read);
                    Platform.runLater(()->{

                        /*A little hack*/
                        if (txtOutput.getText().contentEquals("Enter password: ")) {
                            txtOutput.clear();
                            txtOutput.setText("Welcome to MySQL Client Shell\n" +
                                    "----------------------------------------------\n\n" +
                                    "Please enter your command to proceed. \nThank you! \uD83D\uDE09" +
                                    "\n\nCopyright © 2022 Hashadhi Jayasinghe. All Rights Reserved. \n");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void txtCommand_OnAction(ActionEvent actionEvent) {
        btnExecute.fire();
    }
}

















