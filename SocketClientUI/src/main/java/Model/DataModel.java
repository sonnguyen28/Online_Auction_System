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

    private Lot currentLotOb;

    public ObservableList<Lot> getLotListOb() {
        return lotListOb;
    }

    public void setLotListOb(ArrayList<Lot> listLot){
        lotListOb = FXCollections.observableArrayList(lot -> new Observable[] {lot.winning_bidProperty()});
        for (int i = 0; i < listLot.size(); i++) {
            lotListOb.add(listLot.get(i));
        }
    }

    public void setlotHistoryListOb(ArrayList<Lot> lotHistoryList){
        lotHistoryListOb = FXCollections.observableArrayList(lotHistoryList);
    }

    public ObservableList<Lot> getLotHistoryListOb() {
        return lotHistoryListOb;
    }

    public Lot getCurrentLotOb() {
        return currentLotOb;
    }

    public void setCurrentLotOb(Lot currentLotOb) {
        this.currentLotOb = currentLotOb;
    }
}
