package Model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Lot {
    private int lot_id;
    private String title;
    private String description;
    private float min_price;
    private FloatProperty winning_bid = new SimpleFloatProperty();
    private int winning_bidder; //0
    private int owner_id;
    private String time_start; //2021-12-20 09:49:00
    private String time_stop; //2021-12-25 10:00:00
    private List<ImageLot> imageLots;
    private ObservableList<Bid> bitListOb = FXCollections.observableArrayList();

    public Lot(int lot_id, String title, String description, float min_price, float winning_bid, int winning_bidder, int owner_id, String time_start, String time_stop) {
        this.lot_id = lot_id;
        this.title = title;
        this.description = description;
        this.min_price = min_price;
        setWinning_bid(winning_bid);
        this.winning_bidder = winning_bidder;
        this.owner_id = owner_id;
        this.time_start = time_start;
        this.time_stop = time_stop;
    }



    public int getLot_id() {
        return lot_id;
    }

    public void setLot_id(int lot_id) {
        this.lot_id = lot_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getMin_price() {
        return min_price;
    }

    public void setMin_price(float min_price) {
        this.min_price = min_price;
    }

    public float getWinning_bid() {
        return winning_bid.get();
    }

    public FloatProperty winning_bidProperty() {
        return winning_bid;
    }

    public void setWinning_bid(float winning_bid) {
        this.winning_bid.set(winning_bid);
    }

    public int getWinning_bidder() {
        return winning_bidder;
    }

    public void setWinning_bidder(int winning_bidder) {
        this.winning_bidder = winning_bidder;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_stop() {
        return time_stop;
    }

    public void setTime_stop(String time_stop) {
        this.time_stop = time_stop;
    }

    public List<ImageLot> getImages() {
        return imageLots;
    }

    public void setImages(List<ImageLot> imageLots) {
        this.imageLots = imageLots;
    }

    public ObservableList<Bid> getBitListOb() {
        return bitListOb;
    }

    @Override
    public String toString() {
        return "Lot{" +
                "lot_id=" + lot_id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", min_price=" + min_price +
                ", winning_bid=" + winning_bid +
                ", winning_bidder=" + winning_bidder +
                ", owner_id=" + owner_id +
                ", time_start='" + time_start + '\'' +
                ", time_stop='" + time_stop + '\'' +
                '}';
    }
}
