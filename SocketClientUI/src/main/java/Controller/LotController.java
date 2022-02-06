package Controller;

import Main.App;
import Model.DataModel;
import Model.Lot;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static Main.App.dataModel;
import static Main.App.scene;

public class LotController {

    @FXML
    private ImageView image;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label labelTimeStop;

    @FXML
    private DataModel dataModelLot;

    @FXML
    void clickLot(MouseEvent event) throws IOException {
        System.out.println("Hello");
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("lotdetail.fxml"));
        BorderPane borderPane = fxmlLoader.load();

        LotDetailController lotDetailController = fxmlLoader.getController();
        lotDetailController.setDataDetail(code, lot);
        scene.setRoot(borderPane);
    }

    private Lot lot;

    private int code;

    public void setData(int code, Lot lot) throws URISyntaxException {
        this.lot = lot;
        this.code = code;

        this.dataModelLot = dataModel;

        if(code == 1){
            labelTimeStop.setText(lot.getTime_stop());
        }
        nameLabel.setText(lot.getTitle());
        if (lot.getMin_price() < lot.getWinning_bid()){
            priceLabel.setText(App.CURRENCY + lot.getWinning_bid());
        }else {
            priceLabel.setText(App.CURRENCY + lot.getMin_price());
        }


        String pathImageList = "src/main/resources/Main/image/" + lot.getLot_id() + "/";

        File folder = new File(pathImageList);
        File[] listOfFiles = folder.listFiles();

        Image img = null;
        try {
            img = new Image(listOfFiles[0].toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        image.setImage(img);
    }

}