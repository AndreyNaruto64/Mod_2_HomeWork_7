module com.example.mod_2_homework_7 {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.mod_2_homework_7.client;
    opens com.example.mod_2_homework_7.client to javafx.fxml;
}