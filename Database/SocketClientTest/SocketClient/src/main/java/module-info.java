module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens org.socket.client to javafx.fxml;
    exports org.socket.client;
}
