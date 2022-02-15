package org.socket.client;

import com.google.gson.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private DataInputStream dataInputStream;
    private DataOutputStream out;
    public Client (Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            /*this.username = username;*/
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void  sendMessage(){
        try {
            Scanner scanner = new Scanner(System.in);
            String messageToSendv1 = "{\"command\":2,\"username\":\"luan\",\"password\":\"password\"}";
            System.out.println("Send to server: " + messageToSendv1);
            bufferedWriter.write(messageToSendv1);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            File file = new File("/Users/sonnguyen/IdeaProjects/Multiple_Clients_Chat/SocketClient/src/image.jpeg");
            File file2 = new File("/Users/sonnguyen/IdeaProjects/Multiple_Clients_Chat/SocketClient/src/image1.jpeg");
/*            String messageToSend = scanner.nextLine();
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();*/
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.flush();
                System.out.println("Send to server: " + messageToSend);
                JsonObject jsonObject = new Gson().fromJson(messageToSend, JsonObject.class);
                int command = jsonObject.get("command").getAsInt();
                if(command == 4){
                    byte[] bytes = new byte[16 * 1024];
                    InputStream in = new FileInputStream(file);

                    //OutputStream out = socket.getOutputStream();

                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                        out.flush();
                    }
                    in.close();

                    InputStream in2 = new FileInputStream(file2);
                    byte[] bytes2 = new byte[16 * 1024];
                    while ((count = in2.read(bytes2)) > 0) {
                        out.write(bytes2, 0, count);
                        out.flush();
                    }

                    //out.close();
                    in2.close();
                }
            }
            /*JsonObject rootObject = new JsonObject();
            rootObject.addProperty("command", 4);
            rootObject.addProperty("user_id", 1);
            rootObject.addProperty("title", "VINFAST-VF2");
            rootObject.addProperty("description", "Xe oto");
            rootObject.addProperty("min_price", 30);
            rootObject.addProperty("time_stop", "2022-01-30 08:54:00");
            JsonArray images = new JsonArray();

            JsonObject image1 = new JsonObject();

            String fileName = String.valueOf(file.getName());
            System.out.println("File name: " + fileName + " lenght: " + file.length());
            long fileSize = file.length();
            image1.addProperty("image_name", fileName);
            image1.addProperty("image_size", fileSize);

            JsonObject image2 = new JsonObject();

            String fileName2 = String.valueOf(file2.getName());
            System.out.println("File name: " + fileName2 + " lenght: " + file2.length());
            long fileSize2 = file2.length();
            image2.addProperty("image_name", fileName2);
            image2.addProperty("image_size", fileSize2);

            images.add(image1);
            images.add(image2);

            rootObject.add("images", images);

            Gson gson = new GsonBuilder().create();
            String test = gson.toJson(rootObject, JsonObject.class);
            System.out.println("Send to server create lot: " + test);
            bufferedWriter.write(test);
            bufferedWriter.newLine();
            bufferedWriter.flush();*/




        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
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

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()){
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println("Lenght message: " + msgFromGroupChat.length());
                        if(msgFromGroupChat == null) {
                            break;
                        };
                        System.out.println("\nMessage from sever: " + msgFromGroupChat);
                        /*sendMessage();*/
                        JsonObject jsonObject = new Gson().fromJson(msgFromGroupChat, JsonObject.class);
                        if(msgFromGroupChat.equals("{\"command\":-2}")){
                            System.out.println("Login fails");
                        }

                        if(jsonObject.get("command").getAsInt() == 4){
                            JsonArray images = jsonObject.getAsJsonObject("lot").getAsJsonArray("images");
                            int lotID = jsonObject.getAsJsonObject("lot").get("lot_id").getAsInt();
                            File theDir = new File("./src/image/"+lotID);
                            if (!theDir.exists()){
                                theDir.mkdirs();
                            }else {
                                deleteDir(theDir);
                                theDir.mkdirs();
                            }
                            for (JsonElement image : images) {
                                JsonObject objImage = (JsonObject) image;
                                String fileName = objImage.get("image_name").getAsString();
                                int fileSize = objImage.get("image_size").getAsInt();
                                System.out.println("|" + fileName + " - " + fileSize + "|");
                                int bytes = 0;
                                File file = new File("./src/image/" + lotID + "/" + fileName);
                                if(file.exists() && !file.isDirectory()) {
                                    file.delete();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream("./src/image/" + lotID + "/" + fileName);
                                long size = fileSize;
                                byte[] buffer = new byte[16 * 1024];
                                while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                    fileOutputStream.write(buffer, 0, bytes);
                                    size -= bytes;      // read upto file size
                                }
                                System.out.println("File OK new lot....");
                                fileOutputStream.close();
                            }
                        }
                        if(jsonObject.get("command").getAsInt() == 2){
                            JsonArray lots = jsonObject.getAsJsonArray("lots");
                            for (JsonElement lot : lots){
                                JsonObject lotObj = (JsonObject) lot;
                                int lotID = lotObj.get("lot_id").getAsInt();
                                JsonArray images = lotObj.getAsJsonArray("images");
                                File theDir = new File("./src/image/" + lotID);
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
                                    File file = new File("./src/image/" + lotID + "/" + fileName);
                                    if(file.exists() && !file.isDirectory()) {
                                        file.delete();
                                    }
                                    FileOutputStream fileOutputStream = new FileOutputStream("./src/image/" + lotID + "/" + fileName);
                                    long size = fileSize;
                                    byte[] buffer = new byte[16 * 1024];
                                    while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                        fileOutputStream.write(buffer, 0, bytes);
                                        size -= bytes;      // read upto file size
                                    }
                                    System.out.println("File OK current lot....");
                                    fileOutputStream.close();
                                }
                            }
                        }
                        if(jsonObject.get("command").getAsInt() == 6){
                            File pathDirImage = new File("./src/image");
                            File[] listFolder = pathDirImage.listFiles();
                            if(listFolder != null){
                                for (File path : listFolder){
                                    deleteDir(path);
                                }
                            }
                        }

                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        Socket socket = new Socket("localhost", 8888);
        Client client = new Client(socket);
        client.listenForMessage();
        client.sendMessage();

    }
}
