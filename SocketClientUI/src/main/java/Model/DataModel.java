package Model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

import static Main.App.lotList;

public class DataModel {
    private ObservableList<Lot> lotListOb = FXCollections.observableArrayList();

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

    public void loadData(){
        for (int i = 0; i < lotList.size(); i++) {
            lotListOb.add(lotList.get(i));
        }
    }

}
