package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Main.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import static Main.App.*;

public class LoginController implements Initializable {

    @FXML
    private Label errMessageLabel;

    @FXML
    private Label errMessageUserName;

    @FXML
    private Label errMessagePassword;

    @FXML
    private TextField input_username;

    @FXML
    private PasswordField input_password;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //To do
    }

    public void loginButtonOnAction(ActionEvent event) throws IOException, InterruptedException {
        if (!input_username.getText().isBlank() && !input_password.getText().isBlank()){

            client.sendMessgase(createLoginMess(input_username.getText(), input_password.getText()));
            synchronized (myListener){
                try{
                    System.out.println("Waiting message from server ...");
                    myListener.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
            }}
            int command = myListener.getCommandMess();
            if (command == 2){
                client.setUser_name(input_username.getText());
                App.resizeScene(1290, 900);

                App.setRoot("homepage");
            }
            if(command == -2){
                errMessageLabel.setText("Sai username hoặc password\nChú ý: tài khoản chỉ được đăng nhập một nơi");
            }
        }
        else {
            if(input_username.getText().isBlank()){
                errMessageUserName.setText("Please enter username");
            }
            if(input_password.getText().isBlank()){
                errMessagePassword.setText("Please enter password");
            }
        }
    }

    public void changeRegisterPage(ActionEvent event) throws IOException {
        App.setRoot("register");
    }

    public void setOnClickInputUserName(MouseEvent event){
        errMessageLabel.setText("");
        errMessageUserName.setText("");
    }
    public void setOnClickInputPassword(MouseEvent event){
        errMessageLabel.setText("");
        errMessagePassword.setText("");
    }
    public String createLoginMess(String username, String password){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 2);
        messJson.addProperty("username", username);
        messJson.addProperty("password", password);

        Gson gson = new GsonBuilder().create();
        String loginMess = gson.toJson(messJson, JsonObject.class);
        return loginMess;
    }



}
