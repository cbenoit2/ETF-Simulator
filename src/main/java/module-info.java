module org.example.etfbuilder {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    opens org.example.etfbuilder to javafx.fxml;
    exports org.example.etfbuilder;
    exports org.example.etfbuilder.controller;
    opens org.example.etfbuilder.controller to javafx.fxml;
    exports org.example.etfbuilder.interfaces;
    opens org.example.etfbuilder.interfaces to javafx.fxml;
}