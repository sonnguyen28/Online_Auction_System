package Controller;

import Main.App;
import Model.Lot;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class LotController {

    @FXML
    private ImageView image;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    private Lot lot;

    public void setData(Lot lot){
        this.lot = lot;

        nameLabel.setText(lot.getTitle());
        if (lot.getMin_price() < lot.getWinning_bid()){
            priceLabel.setText(App.CURRENCY + lot.getWinning_bid());
        }else {
            priceLabel.setText(App.CURRENCY + lot.getMin_price());
        }


        String pathImageList = "src/main/resources/Main/image/" + lot.getLot_id() + "/";

        File folder = new File(pathImageList);
        File[] listOfFiles = folder.listFiles();

        Image img = new Image(String.valueOf(App.class.getResource("image/" + lot.getLot_id() + "/" + listOfFiles[0].getName())));
        image.setImage(img);
    }

}