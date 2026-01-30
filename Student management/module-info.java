module com.example.studentmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    // Must match the package name used in StudentApp.java
    opens com.example.studentmanagement to javafx.fxml, javafx.graphics;

    exports com.example.studentmanagement;
}