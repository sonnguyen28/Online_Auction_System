module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.socket.server to javafx.fxml;
    exports org.socket.server;
}
