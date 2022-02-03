package Controller;

import Main.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Main.App.*;

public class SellController implements Initializable {

    @FXML
    private Label labelUserName;

    @FXML
    private Label btnHistoryPage;

    @FXML
    private Label btnHomePage;

    @FXML
    private Label btnLogout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelUserName.setText(client.getUser_name());
    }

    public void changeHomePage() throws IOException {
        App.setRoot("homepage");
    }

    public void logout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Logout " + " ?", ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            client.sendMessgase(createLogoutMess());
            synchronized (myListener){
                try{
                    System.out.println("Waiting message from server ...");
                    myListener.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }}
            int command = myListener.getCommandMess();
            if (command == 6){
                client.setUser_name("");
                client.setUser_id(0);
                lotList = null;
                App.resizeScene(800, 560);
                App.setRoot("login");
            }
        }
    }

    public String createLogoutMess(){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 6);
        messJson.addProperty("user_id", client.getUser_id());

        Gson gson = new GsonBuilder().create();
        String logoutMess = gson.toJson(messJson, JsonObject.class);
        return logoutMess;
    }
}
