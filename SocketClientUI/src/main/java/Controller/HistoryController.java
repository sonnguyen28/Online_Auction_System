package Controller;

import Main.App;
import Model.Lot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Main.App.*;
import static Main.App.myListener;


public class HistoryController implements Initializable {

    @FXML
    private GridPane grid;


    @FXML
    private Label btnHistoryPage;

    @FXML
    private Label btnSellPage;

    @FXML
    private Label labelUserName;

    @FXML
    private Label btnLogout;

    @FXML
    private ScrollPane scroll;

    private List<Lot> lots = new ArrayList<>();

    private List<Lot> getData(){
        Lot lot;
/*

        for (Lot lot: lotList) {
            lot.setTitle("Iphone 11");
            lot.setMin_price(389);
            lot.setPath_image("src/image/iphone-11-64gb.png");
            lots.add(lot);
        }
*/

        return lots;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelUserName.setText(client.getUser_name());
        //lots.addAll(getData());

        int column = 0;
        int row = 1;
        try {
            if(lotHistoryList.size() != 0){
                for (int i = 0; i < lotHistoryList.size(); i++) {

                    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("lot.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    LotController lotController = fxmlLoader.getController();
                    lotController.setData(1, lotHistoryList.get(i));


                    if(column == 4){
                        column = 0;
                        row++;
                    }


                    grid.add(anchorPane, column++, row);
                    //set grid width
                    grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                    grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    grid.setMaxWidth(Region.USE_PREF_SIZE);

                    //set grid height
                    grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                    grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    grid.setMaxHeight(Region.USE_PREF_SIZE);

                    GridPane.setMargin(anchorPane, new Insets(20));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeSellPage() throws IOException {
        App.setRoot("sellpage");
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