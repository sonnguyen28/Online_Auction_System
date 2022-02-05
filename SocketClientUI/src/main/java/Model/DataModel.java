package Model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DataModel {
    private ObservableList<Lot> lotListOb;

    private ObservableList<Lot> lotHistoryListOb;

    private ObjectProperty<Lot> currentLotOb = new SimpleObjectProperty<>(null);


    public ObservableList<Lot> getLotListOb() {
        return lotListOb;
    }

    public Lot getCurrentLotOb() {
        return currentLotOb.get();
    }

    public ObjectProperty<Lot> currentLotObProperty() {
        return currentLotOb;
    }

    public void setCurrentLotOb(Lot currentLotOb) {
        this.currentLotOb.set(currentLotOb);
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
