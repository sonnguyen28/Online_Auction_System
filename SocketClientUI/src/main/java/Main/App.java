package Main;

import Model.Client;
import Model.Lot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * JavaFX App
 **/
public class App extends Application {

    public static final String CURRENCY = "$";

    private static Scene scene;
    public static Client client;
    public static List<Lot> lotList;
    public static MyListener myListener;

    private static Stage stage;
    private static double decorationWidth;
    private static double decorationHeight;

    @Override
    public void start(Stage stage) throws IOException {

        final double initialSceneWidth = 800;
        final double initialSceneHeight = 560;
        this.stage = stage;
        scene = new Scene(loadFXML("login"), initialSceneWidth, initialSceneHeight);
        stage.setTitle("Auction");
        stage.setScene(scene);
        stage.show();

        this.decorationWidth = initialSceneWidth - scene.getWidth();
        this.decorationHeight = initialSceneHeight - scene.getHeight();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public static void resizeScene(double width, double height) {
        stage.setWidth(width + decorationWidth);
        stage.setHeight(height + decorationHeight);
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
        myListener = new MyListener(socket);
        myListener.start();

        launch();
    }

}