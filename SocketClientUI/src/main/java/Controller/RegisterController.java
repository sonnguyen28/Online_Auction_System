package Controller;

import Main.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import static Main.App.client;
import static Main.App.myListener;

public class RegisterController {

    @FXML
    private Label errMessageLabel;

    @FXML
    private Label errMessageUserName;

    @FXML
    private Label errMessagePassword;

    @FXML
    private Label errMessageConfirmPassword;

    @FXML
    private TextField input_username;

    @FXML
    private PasswordField input_password;

    @FXML
    private PasswordField input_confirmPassword;

    public void registerButtonOnAction(ActionEvent event) throws IOException, InterruptedException {
        if (!input_username.getText().isBlank() && !input_password.getText().isBlank() && !input_confirmPassword.getText().isBlank()){
            if(input_password.getText().equals(input_confirmPassword.getText())){
                client.sendMessgase(createRegisterMess(input_username.getText(), input_password.getText()));
                synchronized (myListener){
                    try{
                        System.out.println("Waiting message from server ...");
                        myListener.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }}
                int command = myListener.getCommandMess();
                if (command == 1){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Create account success");
                    alert.show();
                    App.setRoot("login");
                }
                if(command == -1){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Account already exists");
                    alert.show();
                }
            }else {
                System.out.println("Hello");
                errMessageConfirmPassword.setText("The password confirmation does not match");
            }
        }
        else {
            if(input_username.getText().isBlank()){
                errMessageUserName.setText("Please enter username");
            }
            if(input_password.getText().isBlank()){
                errMessagePassword.setText("Please enter password");
            }
            if(input_confirmPassword.getText().isBlank()){
                errMessageConfirmPassword.setText("Please enter confirm password");
            }
        }
    }

    public void changeLoginPage(ActionEvent event) throws IOException {
        App.setRoot("login");
    }

    public void setOnClickInputUserName(MouseEvent event){
        errMessageLabel.setText("");
        errMessageUserName.setText("");
    }
    public void setOnClickInputPassword(MouseEvent event){
        errMessageLabel.setText("");
        errMessagePassword.setText("");
    }
    public void setOnClickInputConfirmPassword(MouseEvent event){
        errMessageLabel.setText("");
        errMessageConfirmPassword.setText("");
    }
    public String createRegisterMess(String username, String password){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 1);
        messJson.addProperty("username", username);
        messJson.addProperty("password", password);

        Gson gson = new GsonBuilder().create();
        String registerMesss = gson.toJson(messJson, JsonObject.class);
        return registerMesss;
    }
}
