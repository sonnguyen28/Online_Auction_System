package Controller;

import Main.App;
import Model.Lot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static Main.App.*;

public class LotDetailController {

    @FXML
    private ImageView btnBackHomePage;

    @FXML
    private Label btnBid;

    @FXML
    private Label btnHistoryPage;

    @FXML
    private Label btnLogout;

    @FXML
    private Label btnSellPage;

    @FXML
    private ImageView imageMain;

    @FXML
    private TextField inputBidAmount;

    @FXML
    private Label labelBidList;

    @FXML
    private Label labelUserName;

    @FXML
    private Label lotCurrentBid;

    @FXML
    private Label lotDescription;

    @FXML
    private HBox lotDetailPane;

    @FXML
    private Label lotTime;

    @FXML
    private Label lotTitle;

    @FXML
    private GridPane grid;

    @FXML
    void backHomePage(MouseEvent event) throws IOException {

    }

    private Lot lot;

    public void setDataDetail(int code, Lot lot){
        this.lot = lot;

        if(code == 1) {
            btnBid.setVisible(false);
            inputBidAmount.setVisible(false);
        }

        labelUserName.setText(client.getUser_name());

        lotTitle.setText(lot.getTitle());
        lotDescription.setText(lot.getDescription());
        lotTime.setText(lot.getTime_stop());
        lotCurrentBid.setText(lot.getWinning_bid() > 0 ? Float.toString(lot.getWinning_bid()) : Float.toString(lot.getMin_price()));

        String pathImageList = "src/main/resources/Main/image/" + lot.getLot_id() + "/";

        File folder = new File(pathImageList);
        File[] listOfFiles = folder.listFiles();

        Image img = null;
        try {
            img = new Image(listOfFiles[0].toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        imageMain.setImage(img);

        int column = 1;
        int row = 0;

        for (int i = 0; i < lot.getImages().size(); i++) {

            img = null;
            try {
                img = new Image(listOfFiles[i].toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            AnchorPane anchorPane = new AnchorPane();
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(90);
            imageView.setFitWidth(200);
            Image finalImg = img;
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    imageMain.setImage(finalImg);
                }
            });
            anchorPane.getChildren().add(imageView);
            grid.add(anchorPane, column++, row);
            //set grid width
            grid.setMinHeight(Region.USE_COMPUTED_SIZE);
            grid.setPrefWidth(100);
            grid.setMaxWidth(Region.USE_PREF_SIZE);

            //set grid height
            grid.setMinHeight(Region.USE_COMPUTED_SIZE);
            grid.setPrefHeight(100);
            grid.setMaxHeight(Region.USE_PREF_SIZE);

            GridPane.setMargin(anchorPane, new Insets(20));
        }

    }

    public void changeHomePage() throws IOException {
        App.setRoot("homepage");
    }

    public void changeSellPage() throws IOException {
        App.setRoot("sellpage");
    }

    public void changeHistoryPage() throws IOException {
        client.sendMessgase(createHistoryMess());
        synchronized (myListener){
            try{
                System.out.println("Waiting message from server ...");
                myListener.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }}
        int command = myListener.getCommandMess();
        if (command == 5){
            App.setRoot("historypage");
        }
    }

    public void logout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Confirm Logout");

        // Header Text: null
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to Logout ?");

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
                dataModel = null;
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
    public String createHistoryMess(){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 5);
        messJson.addProperty("user_id", client.getUser_id());

        Gson gson = new GsonBuilder().create();
        String historyMess = gson.toJson(messJson, JsonObject.class);
        return historyMess;
    }

}
