package Model;

public class Bid {
    private int bid_id;
    private int lot_id;
    private int bidder_user_id;
    private float bid_amount;
    private String created;

    public Bid(int bid_id, int lot_id, int bidder_user_id, float bid_amount, String created) {
        this.bid_id = bid_id;
        this.lot_id = lot_id;
        this.bidder_user_id = bidder_user_id;
        this.bid_amount = bid_amount;
        this.created = created;
    }

    public int getBid_id() {
        return bid_id;
    }

    public void setBid_id(int bid_id) {
        this.bid_id = bid_id;
    }

    public int getLot_id() {
        return lot_id;
    }

    public void setLot_id(int lot_id) {
        this.lot_id = lot_id;
    }

    public int getBidder_user_id() {
        return bidder_user_id;
    }

    public void setBidder_user_id(int bidder_user_id) {
        this.bidder_user_id = bidder_user_id;
    }

    public float getBid_amount() {
        return bid_amount;
    }

    public void setBid_amount(float bid_amount) {
        this.bid_amount = bid_amount;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
