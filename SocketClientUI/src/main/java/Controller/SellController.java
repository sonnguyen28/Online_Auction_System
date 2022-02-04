package Controller;

import Main.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static Main.App.*;

public class SellController implements Initializable {

    @FXML
    private Label btnAddImage;

    @FXML
    private Label btnHistoryPage;

    @FXML
    private Label btnHomePage;

    @FXML
    private Label btnLogout;

    @FXML
    private Label btnSell;

    @FXML
    private Label errDescriptionField;

    @FXML
    private Label errImage;

    @FXML
    private Label errTimeField;

    @FXML
    private Label errTitleField;

    @FXML
    private DatePicker inputDate;

    @FXML
    private TextArea inputDescription;

    @FXML
    private ComboBox<String> inputHour;

    @FXML
    private ComboBox<String> inputMinutes;

    @FXML
    private ComboBox<String> inputSecondes;

    @FXML
    private TextField inputTitle;

    @FXML
    private Label labelUserName;

    @FXML
    private TextArea litstImage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelUserName.setText(client.getUser_name());

        ObservableList<String> hoursList = FXCollections.observableArrayList();
        ObservableList<String> minutesAndSecondsList = FXCollections.observableArrayList();
        for (int i = 0; i <= 60 ; i++) {
            if(0 <= i && i <= 24){
                hoursList.add(String.format("%02d", i));
            }
            minutesAndSecondsList.add(String.format("%02d", i));
        }
        inputHour.setItems(hoursList);
        inputHour.setValue("12");

        inputMinutes.setItems(minutesAndSecondsList);
        inputMinutes.setValue("00");

        inputSecondes.setItems(minutesAndSecondsList);
        inputSecondes.setValue("00");



    }

    public void changeHomePage() throws IOException {
        App.setRoot("homepage");
    }

    public void changeHistoryPage() throws IOException {
        App.setRoot("historypage");
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
