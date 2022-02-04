package Controller;

import Main.App;
import Model.Lot;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class LotController {

    @FXML
    private ImageView image;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label labelTimeStop;

    private Lot lot;

    public void setData(int code, Lot lot) throws URISyntaxException {
        this.lot = lot;

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