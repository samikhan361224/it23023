module com.example.quizgame {
    // Requires the core JavaFX modules
    requires javafx.controls;
    requires javafx.graphics;

    // Requires the SQL module for JDBC/SQLite
    requires java.sql;

    // Opens the package to javafx.graphics so it can launch the Application
    opens com.example.quizgame to javafx.graphics;

    // Export the package if other modules need access (optional here)
    exports com.example.quizgame;
}