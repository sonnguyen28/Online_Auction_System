package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"), 780, 480);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8888);
        client = new Client(socket);
        client.ListenMessage();

        launch();
    }

}