package Controller;

import Main.App;
import Model.Bid;
import Model.DataModel;
import Model.Lot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
    private TableView<Bid> table;

    @FXML
    private Label lotIdLabel;

    @FXML
    void backHomePage(MouseEvent event) throws IOException {

    }

    private DataModel dataModelLotDetail;

    @FXML
    private Label errBid;

    @FXML
    private Label time_stop;

    public void setDataDetail(int code, Lot lot){
        this.dataModelLotDetail = dataModel;
        dataModelLotDetail.setCurrentLotOb(lot);

        if(code == 1) {
            btnBid.setVisible(false);
            inputBidAmount.setVisible(false);
            time_stop.setText("Time stop:");
        }

        if(client.getUser_id() == lot.getOwner_id()){
            btnBid.setVisible(false);
            inputBidAmount.setVisible(false);
        }

        labelUserName.setText(client.getUser_name());

        lotTitle.setText(lot.getTitle());
        lotIdLabel.setText("( ID: " + String.valueOf(lot.getLot_id()) + " )");
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

        dataModelLotDetail.getCurrentLotOb().winning_bidProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lotCurrentBid.setText(dataModelLotDetail.getCurrentLotOb().getWinning_bid() > 0 ? Float.toString(dataModelLotDetail.getCurrentLotOb().getWinning_bid()) : Float.toString(dataModelLotDetail.getCurrentLotOb().getMin_price()));
                    }
                });
            }
        });

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

    public int checkFloat(String str){
        float a = dataModelLotDetail.getCurrentLotOb().getWinning_bid();
        if(a>0){
            a = dataModelLotDetail.getCurrentLotOb().getWinning_bid();
        }else{
            a = dataModelLotDetail.getCurrentLotOb().getMin_price();
        }
        try{
            if(Float.parseFloat(str) < a){
                return 0;
            }
        }catch(NumberFormatException e){
            return 0;
        }
        return 1;
    }

    public void setOnClickInputBid(MouseEvent event){
        errBid.setText("");
    }

    public String createBidMess(){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 3);
        messJson.addProperty("user_id", client.getUser_id());
        messJson.addProperty("lot_id", dataModelLotDetail.getCurrentLotOb().getLot_id());
        messJson.addProperty("bid_amount", Float.parseFloat(inputBidAmount.getText()));

        Gson gson = new GsonBuilder().create();
        String bidMess = gson.toJson(messJson, JsonObject.class);
        System.out.println(bidMess);
        return bidMess;
    }

    public void bidButtonOnAction(MouseEvent event) {
        if (!inputBidAmount.getText().isBlank() && checkFloat(inputBidAmount.getText()) == 1 &&
                Float.parseFloat(inputBidAmount.getText()) >
                        (dataModelLotDetail.getCurrentLotOb().getWinning_bid() == 0 ?  dataModelLotDetail.getCurrentLotOb().getMin_price() : dataModelLotDetail.getCurrentLotOb().getWinning_bid())) {

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.CANCEL,ButtonType.OK);
            confirm.setTitle("CONFIRMATION");
            confirm.setHeaderText(null);
            confirm.setContentText("Do you want pay this bid?");
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.CANCEL) {
                return;
            }
            client.sendMessgase(createBidMess());
            synchronized (myListener) {
                try {
                    System.out.println("Waiting message from server ...");
                    myListener.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int command = myListener.getCommandMess();
            if (command == 3) {
                System.out.println(myListener.getReceiveMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                alert.setTitle("Information");

                // Header Text: null
                alert.setHeaderText(null);
                alert.setContentText("Bid successfully !!!");
                alert.show();
                errBid.setText("");
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                alert.setTitle("Information");

                alert.setHeaderText(null);
                alert.setContentText("Bid fail !!!");
            }
        } else {
            if (inputBidAmount.getText().isBlank()) {
                errBid.setText("This field is required *");
            }
            if (!inputBidAmount.getText().isBlank() && checkFloat(inputBidAmount.getText()) == 0) {
                errBid.setText("Invalid Value !!!");
            }
            if (Float.parseFloat(inputBidAmount.getText()) <= (dataModelLotDetail.getCurrentLotOb().getWinning_bid() == 0 ?  dataModelLotDetail.getCurrentLotOb().getMin_price() : dataModelLotDetail.getCurrentLotOb().getWinning_bid())) {
                errBid.setText("Bid amount > " + dataModelLotDetail.getCurrentLotOb().getWinning_bid() + " !!!");
            }
        }
    }

    public String createBidListMess(){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 8);
        messJson.addProperty("user_id", client.getUser_id());
        messJson.addProperty("lot_id", dataModelLotDetail.getCurrentLotOb().getLot_id());

        Gson gson = new GsonBuilder().create();
        String bidListMess = gson.toJson(messJson, JsonObject.class);
        return bidListMess;
    }

    public void bidListButtonOnAction(MouseEvent event){
        client.sendMessgase(createBidListMess());
        synchronized (myListener){
            try{
                System.out.println("Waiting message from server ...");
                myListener.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }}
        int command = myListener.getCommandMess();
        if (command == 8){
            Label secondLabel = new Label("I'm a Label on new Window");

            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(secondLabel);

            Scene secondScene = new Scene(secondaryLayout, 800, 760);

            // Một cửa sổ mới (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Second Stage");
            newWindow.setScene(secondScene);

            // Sét đặt vị trí cho cửa sổ thứ 2.
            // Có vị trí tương đối đối với cửa sổ chính.
    /*    newWindow.setX(scene.getX() + 200);
        newWindow.setY(scene.getY() + 100);*/


            TableView<Bid> table = new TableView<Bid>(dataModelLotDetail.getCurrentLotOb().getBitListOb());


            // Tạo cột bidder (Kiểu dữ liệu String)
            TableColumn<Bid, String> Bidder //
                    = new TableColumn<Bid, String>("Bidder");

            // Tạo cột bid_amount (Kiểu dữ liệu Float)
            TableColumn<Bid, Float> BidAmount//
                    = new TableColumn<Bid, Float>("Bid Amount");

            // Tạo cột bid_time (Kiểu dữ liệu String)
            TableColumn<Bid, String> BidTime//
                    = new TableColumn<Bid, String>("Bid Time");

            Bidder.setCellValueFactory(new PropertyValueFactory<>("bidder_user_id"));
            BidAmount.setCellValueFactory(new PropertyValueFactory<>("bid_amount"));
            BidTime.setCellValueFactory(new PropertyValueFactory<>("created"));

            BidAmount.setSortType(TableColumn.SortType.DESCENDING);

            table.getColumns().addAll( BidAmount, BidTime, Bidder);

            StackPane root = new StackPane();
            root.setPadding(new Insets(5));
            root.getChildren().add(table);

            newWindow.setTitle("Bid List");

            Scene scene = new Scene(root, 800, 760);
            newWindow.setScene(scene);

            newWindow.show();
            newWindow.centerOnScreen();

            dataModelLotDetail.getCurrentLotOb().getBitListOb().addListener(new ListChangeListener<Bid>() {
                @Override
                public void onChanged(Change<? extends Bid> change) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            root.getChildren().clear();
                            TableView<Bid> table = new TableView<Bid>(dataModelLotDetail.getCurrentLotOb().getBitListOb());


                            // Tạo cột bidder (Kiểu dữ liệu String)
                            TableColumn<Bid, String> Bidder //
                                    = new TableColumn<Bid, String>("Bidder");

                            // Tạo cột bid_amount (Kiểu dữ liệu Float)
                            TableColumn<Bid, Float> BidAmount//
                                    = new TableColumn<Bid, Float>("Bid Amount");

                            // Tạo cột bid_time (Kiểu dữ liệu String)
                            TableColumn<Bid, String> BidTime//
                                    = new TableColumn<Bid, String>("Bid Time");

                            Bidder.setCellValueFactory(new PropertyValueFactory<>("bidder_user_id"));
                            BidAmount.setCellValueFactory(new PropertyValueFactory<>("bid_amount"));
                            BidTime.setCellValueFactory(new PropertyValueFactory<>("created"));

                            BidAmount.setSortType(TableColumn.SortType.DESCENDING);

                            table.getColumns().addAll(BidAmount, BidTime, Bidder);

                            StackPane root = new StackPane();
                            root.setPadding(new Insets(5));
                            root.getChildren().add(table);

                            newWindow.setTitle("Bid List");

                            Scene scene = new Scene(root, 800, 760);
                            newWindow.setScene(scene);

                        }
                    });
                }
            });

        }
    }
}
