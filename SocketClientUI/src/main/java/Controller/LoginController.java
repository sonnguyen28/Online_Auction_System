package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;

import Main.App;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import static Main.App.client;

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

    public void loginButtonOnAction(ActionEvent event) throws IOException {
        if (input_username.getText().isBlank() == false && input_password.getText().isBlank() == false){
            /*if(validateLogin(input_username.getText(), input_password.getText())){
                App.setRoot("secondary");
            }*/
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

    public void setOnClickInputUserName(MouseEvent event){
        errMessageLabel.setText("");
        errMessageUserName.setText("");
    }
    public void setOnClickInputPassword(MouseEvent event){
        errMessageLabel.setText("");
        errMessagePassword.setText("");
    }

    public boolean validateLogin(String username, String password) throws IOException {
        client.createMessLogin(username, password);
    }


}
