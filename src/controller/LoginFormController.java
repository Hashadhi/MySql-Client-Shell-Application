package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class LoginFormController {
    public Button btnConnect;
    public Button btnExit;
    public PasswordField txtPassword;
    public TextField txtUserName;
    public TextField txtPort;
    public TextField txtHost;

    public void initialize(){
        Platform.runLater(()->txtUserName.requestFocus());}

    public void btnConnect_OnAction(ActionEvent actionEvent) {
        if (isValidInputs()) {

            try {
               Process mysql= new ProcessBuilder("mysql",
                        "-h", txtHost.getText(),
                        "-u", txtUserName.getText(),
                        "--port", txtPort.getText(),
                        "-p",
                        "-e", "exit").start();

                mysql.getOutputStream().write(txtPassword.getText().getBytes());
                mysql.getOutputStream().close();
                int exitCode = mysql.waitFor();

                if (exitCode != 0) {
                    System.out.println(exitCode);
                    InputStream es = mysql.getErrorStream();
                    byte[] buffer = new byte[es.available()];
                    es.read(buffer);
                    es.close();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection failure");
                    alert.setHeaderText("Cannot establish the connection");
                    alert.setContentText(new String(buffer));
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.show();

                    txtUserName.requestFocus();
                    txtPassword.selectAll();
                }else{
                    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/ShellForm.fxml"));
                    AnchorPane root = fxmlLoader.load();
                    Scene shellScene = new Scene(root);
                    Stage stage = (Stage) txtUserName.getScene().getWindow();
                    stage.setScene(shellScene);
                    stage.centerOnScreen();
                    stage.setResizable(true);
                    stage.setTitle("MySQL Client Shell");
                    Platform.runLater(stage::sizeToScene);

                    ShellFormController controller = fxmlLoader.getController();
                    controller.initData(txtHost.getText(),
                            txtPort.getText(),
                            txtUserName.getText(),
                            txtPassword.getText());

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    public void btnExit_OnAction(ActionEvent actionEvent) {System.exit(0);}
}
