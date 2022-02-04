package Main;

import Model.ImageLot;
import Model.Lot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static Main.App.*;

public class MyListener extends Thread{
    private Socket socket;
    private BufferedReader bufferedReader;
    private DataInputStream dataInputStream;
    private String receiveMessage;

    public String getReceiveMessage() {
        return receiveMessage;
    }

    public void setReceiveMessage(String receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    public MyListener(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.receiveMessage = new String();
        } catch (IOException e) {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(socket != null){
                socket.close();
            }
        }
    }

    public int getCommandMess(){
        JsonObject recvMessJson = new Gson().fromJson(receiveMessage, JsonObject.class);
        int command = recvMessJson.get("command").getAsInt();
        return command;
    }

    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public void autoHandleMessage(){
        JsonObject recvMessJson = new Gson().fromJson(receiveMessage, JsonObject.class);
        int command = getCommandMess();
        switch (command){
            case 2:
                int clientID = recvMessJson.get("user_id").getAsInt();
                client.setUser_id(clientID);
                lotList = new ArrayList<Lot>();
                JsonArray lots = recvMessJson.getAsJsonArray("lots");
                for (JsonElement lot : lots){
                    JsonObject lotObj = (JsonObject) lot;
                    int lotID = lotObj.get("lot_id").getAsInt();
                    String title = lotObj.get("title").getAsString();
                    String description = lotObj.get("description").getAsString();
                    float min_price = lotObj.get("min_price").getAsFloat();
                    float winning_bid = lotObj.get("winning_bid").getAsFloat();
                    int winning_bidder = lotObj.get("owner_id").getAsInt();
                    int owner_id = lotObj.get("owner_id").getAsInt();
                    String time_start = lotObj.get("time_start").getAsString();
                    String time_stop = lotObj.get("time_stop").getAsString();
                    Lot newLot = new Lot(lotID, title, description,min_price, winning_bid, winning_bidder,owner_id, time_start, time_stop);
                    List<ImageLot> newImageListLot = new ArrayList<ImageLot>();
                    JsonArray images = lotObj.getAsJsonArray("images");
                    File theDir = new File("src/main/resources/Main/image/" + lotID);
                    if (!theDir.exists()){
                        theDir.mkdirs();
                    }else{
                        deleteDir(theDir);
                        theDir.mkdirs();
                    }
                    for (JsonElement image : images) {
                        JsonObject objImage = (JsonObject) image;
                        String fileName = objImage.get("image_name").getAsString();
                        int fileSize = objImage.get("image_size").getAsInt();
                        System.out.println("|" + fileName + " - " + lotID + " - " + fileSize + "|");
                        int bytes = 0;
                        File file = new File("src/main/resources/Main/image/" + lotID + "/" + fileName);
                        if(file.exists() && !file.isDirectory()) {
                            file.delete();
                        }
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Main/image/" + lotID + "/" + fileName);
                            long size = fileSize;
                            byte[] buffer = new byte[16 * 1024];
                            while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                fileOutputStream.write(buffer, 0, bytes);
                                size -= bytes;      // read upto file size
                            }
                            System.out.println("File OK current lot....");
                            ImageLot img = new ImageLot(fileName, fileSize, "src/main/resources/Main/image/" + lotID + "/" + fileName);
                            newImageListLot.add(img);
                            fileOutputStream.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    newLot.setImages(newImageListLot);
                    lotList.add(newLot);
                }
                for (Lot lot: lotList) {
                    System.out.println(lot.toString());
                    for (ImageLot img: lot.getImages()) {
                        System.out.println(img.toString());
                    }
                }
                break;
            case -2:
                break;

            case 5:
                lotHistoryList = new ArrayList<Lot>();
                JsonArray lotsHistory = recvMessJson.getAsJsonArray("lots");
                if (lotsHistory.size() != 0) {
                    for (JsonElement lot : lotsHistory){
                        JsonObject lotObj = (JsonObject) lot;
                        int lotID = lotObj.get("lot_id").getAsInt();
                        String title = lotObj.get("title").getAsString();
                        String description = lotObj.get("description").getAsString();
                        float min_price = lotObj.get("min_price").getAsFloat();
                        float winning_bid = lotObj.get("winning_bid").getAsFloat();
                        int winning_bidder = lotObj.get("owner_id").getAsInt();
                        int owner_id = lotObj.get("owner_id").getAsInt();
                        String time_start = lotObj.get("time_start").getAsString();
                        String time_stop = lotObj.get("time_stop").getAsString();
                        Lot newLot = new Lot(lotID, title, description,min_price, winning_bid, winning_bidder,owner_id, time_start, time_stop);
                        List<ImageLot> newImageListLot = new ArrayList<ImageLot>();
                        JsonArray images = lotObj.getAsJsonArray("images");
                        File theDir = new File("src/main/resources/Main/image/" + lotID);
                        if (!theDir.exists()){
                            theDir.mkdirs();
                        }else{
                            deleteDir(theDir);
                            theDir.mkdirs();
                        }
                        for (JsonElement image : images) {
                            JsonObject objImage = (JsonObject) image;
                            String fileName = objImage.get("image_name").getAsString();
                            int fileSize = objImage.get("image_size").getAsInt();
                            System.out.println("|" + fileName + " - " + lotID + " - " + fileSize + "|");
                            int bytes = 0;
                            File file = new File("src/main/resources/Main/image/" + lotID + "/" + fileName);
                            if(file.exists() && !file.isDirectory()) {
                                file.delete();
                            }
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Main/image/" + lotID + "/" + fileName);
                                long size = fileSize;
                                byte[] buffer = new byte[16 * 1024];
                                while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                    fileOutputStream.write(buffer, 0, bytes);
                                    size -= bytes;      // read upto file size
                                }
                                System.out.println("File OK current lot....");
                                ImageLot img = new ImageLot(fileName, fileSize, "src/main/resources/Main/image/" + lotID + "/" + fileName);
                                newImageListLot.add(img);
                                fileOutputStream.close();
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        newLot.setImages(newImageListLot);
                        lotHistoryList.add(newLot);
                    }
                    for (Lot lot: lotHistoryList) {
                        System.out.println(lot.toString());
                        for (ImageLot img: lot.getImages()) {
                            System.out.println(img.toString());
                        }
                    }
                }
                break;
            case -5:
                break;
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()){
            try {
                receiveMessage = bufferedReader.readLine();
                System.out.println("\nMessage from sever: " + receiveMessage);
                autoHandleMessage();
                synchronized (this){
                    notify();
                }
            } catch (IOException e) {
                closeListener(socket, bufferedReader);
            }
        }
    }

    public void closeListener(Socket socket, BufferedReader bufferedReader){
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
