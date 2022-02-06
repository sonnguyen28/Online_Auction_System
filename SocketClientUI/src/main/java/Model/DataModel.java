package Model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DataModel {
    private ObservableList<Lot> lotListOb;

    private ObservableList<Lot> lotHistoryListOb;

    public ObservableList<Lot> getLotListOb() {
        return lotListOb;
    }

    public void setLotListOb(ArrayList<Lot> listLot){
        lotListOb = FXCollections.observableArrayList(listLot);
    }

    public void setlotHistoryListOb(ArrayList<Lot> lotHistoryList){
        lotHistoryListOb = FXCollections.observableArrayList(lotHistoryList);
    }

    public ObservableList<Lot> getLotHistoryListOb() {
        return lotHistoryListOb;
    }

}
