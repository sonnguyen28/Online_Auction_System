module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens Main to javafx.fxml;
    exports Main;
    exports Controller;
    opens Controller to javafx.fxml;
}
