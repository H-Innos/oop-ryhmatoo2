module com.example.oopryhmatoo2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.oopryhmatoo2 to javafx.fxml;
    exports com.example.oopryhmatoo2;
}