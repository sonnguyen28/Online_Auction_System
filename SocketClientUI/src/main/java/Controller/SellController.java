package Controller;

import Main.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static Main.App.*;

public class SellController implements Initializable {

    @FXML
    private Label btnAddImage;

    @FXML
    private Label btnHistoryPage;

    @FXML
    private Label btnHomePage;

    @FXML
    private Label btnLogout;

    @FXML
    private Label btnSell;

    @FXML
    private Label errDescriptionField;

    @FXML
    private Label errImage;

    @FXML
    private Label errTimeField;

    @FXML
    private Label errTitleField;

    @FXML
    private DatePicker inputDate;

    @FXML
    private TextArea inputDescription;

    @FXML
    private Label errMinPrice;

    @FXML
    private ComboBox<String> inputHour;

    @FXML
    private ComboBox<String> inputMinutes;

    @FXML
    private ComboBox<String> inputSecondes;

    @FXML
    private TextField inputTitle;

    @FXML
    private Label labelUserName;

    @FXML
    private TextArea listImage;

    @FXML
    private TextField inputMinPrice;

    private List<File> fileList;


    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getName() + "\n");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final FileChooser fileChooser = new FileChooser();

        labelUserName.setText(client.getUser_name());

        ObservableList<String> hoursList = FXCollections.observableArrayList();
        ObservableList<String> minutesAndSecondsList = FXCollections.observableArrayList();
        for (int i = 0; i <= 60 ; i++) {
            if(0 <= i && i <= 24){
                hoursList.add(String.format("%02d", i));
            }
            minutesAndSecondsList.add(String.format("%02d", i));
        }
        inputHour.setItems(hoursList);
        inputHour.setValue("12");

        inputMinutes.setItems(minutesAndSecondsList);
        inputMinutes.setValue("00");

        inputSecondes.setItems(minutesAndSecondsList);
        inputSecondes.setValue("00");

        btnAddImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                errImage.setText("");
                listImage.clear();
                fileList = fileChooser.showOpenMultipleDialog(getStage());

                printLog(listImage, fileList);
            }
        });

    }

    public void changeHomePage() throws IOException {
        App.setRoot("homepage");
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

    public String createHistoryMess(){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 5);
        messJson.addProperty("user_id", client.getUser_id());

        Gson gson = new GsonBuilder().create();
        String historyMess = gson.toJson(messJson, JsonObject.class);
        return historyMess;
    }

    public void sellButtonOnAction(MouseEvent event) throws IOException, ParseException {
        String strTime = inputDate.getValue() + " " + inputHour.getValue() + ":" +
                inputMinutes.getValue() + ":" + inputSecondes.getValue();
        if (!inputTitle.getText().isBlank() && !inputDescription.getText().isBlank() &&
                (!inputMinPrice.getText().isBlank() && checkFloat(inputMinPrice.getText()))&&
                (inputDate.getValue()!= null && checkValidTime(strTime))
                && (fileList != null && fileList.size()>0 && checkFileList(fileList) == 1) ){
//            System.out.println(inputDate.getValue() + " " + inputHour.getValue() + ":" +
//                    inputMinutes.getValue() + ":" + inputSecondes.getValue());
            client.sendMessgase(createSellItemMess());
            sendImages();
            synchronized (myListener){
                try{
                    System.out.println("Waiting message from server ...");
                    myListener.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }}
            int command = myListener.getCommandMess();
            if (command == 4){
                System.out.println(myListener.getReceiveMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                alert.setTitle("Information");

                // Header Text: null
                alert.setHeaderText(null);
                alert.setContentText("Successfully !!!");

                alert.showAndWait();
                App.setRoot("homepage");
            }
        }else{
            if(inputTitle.getText().isBlank()){
                errTitleField.setText("This field is required *");
            }
            if(inputMinPrice.getText().isBlank()){
                errMinPrice.setText("This field is required *");
            }
            if(!inputMinPrice.getText().isBlank() && !checkFloat(inputMinPrice.getText())){
                errMinPrice.setText("Invalid Value !!!");
            }
            if(inputDescription.getText().isBlank()){
                errDescriptionField.setText("This field is required *");
            }
            if(inputDate.getValue() == null){
                errTimeField.setText("This field is required *");
            }
            if(inputDate.getValue() != null &&
                    !checkValidTime(strTime)){
                errTimeField.setText("Invalid Time !!!");
            }
            if(fileList == null){
                errImage.setText("Add at least one photo !!!");
            }
        }
    }

    public String createSellItemMess(){
        JsonObject messJson = new JsonObject();
        messJson.addProperty("command", 4);
        messJson.addProperty("user_id", client.getUser_id());
        messJson.addProperty("title", inputTitle.getText());
        messJson.addProperty("description", inputDescription.getText());
        messJson.addProperty("min_price", Float.parseFloat(inputMinPrice.getText()));
        messJson.addProperty("time_stop", inputDate.getValue() + " " + inputHour.getValue() + ":" +
                inputMinutes.getValue() + ":" + inputSecondes.getValue());

        JsonArray images = new JsonArray();
        for (int i = 0; i < fileList.size(); i++) {
            JsonObject image = new JsonObject();
            String fileName = String.valueOf(fileList.get(i).getName());
            System.out.println("File name: " + fileName + " size: " + fileList.get(i).length());
            long fileSize = fileList.get(i).length();
            image.addProperty("image_name", fileName);
            image.addProperty("image_size", fileSize);

            images.add(image);
        }
        messJson.add("images", images);
        Gson gson = new GsonBuilder().create();
        String SellItemMess = gson.toJson(messJson, JsonObject.class);
        System.out.println(SellItemMess);
        return SellItemMess;
    }

    public void sendImages() {
        try {
            OutputStream out = client.getSocket().getOutputStream();
            for (int i = 0; i < fileList.size(); i++) {
                byte[] bytes = new byte[16 * 1024];
                InputStream in = new FileInputStream(fileList.get(i));

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                    out.flush();
                }
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkFloat(String str){
        try{
            if(Float.parseFloat(str) <= 0){
                return false;
            }
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    public static boolean isImage(File file) {
        boolean b = false;
        try {
            b = (ImageIO.read(file) != null);
        } catch (IOException e) {
        }
        return b;
    }
    public int checkFileList(List<File> files){
        int a = 1;
        String str = "";
        for (File file : files) {
            if(!isImage(file)){
                str += file.getName() + " ";
            }
        }
        if(str != ""){
            a = 0;
            errImage.setText(str + " is not image!!!");
        }
        return a;
    }
    public static boolean checkValidTime(String str) throws ParseException {
        Date date = new Date(); // This object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);

        Date strTime = formatter.parse(str);
        if(strTime.after(date)){
            return true;
        }else {
            return false;
        }
    }
    public void setOnClickInputTitle(MouseEvent event){
        errTitleField.setText("");
    }
    public void setOnClickInputMinPrice(MouseEvent event){
        errMinPrice.setText("");
    }
    public void setOnClickInputDescription(MouseEvent event){
        errDescriptionField.setText("");
    }
    public void setOnClickPickTime(MouseEvent event){
        errTimeField.setText("");
    }
}
