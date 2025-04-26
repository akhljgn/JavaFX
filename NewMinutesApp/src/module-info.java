module NewMinutesApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;  // Add this for collections
    requires java.sql;

    opens main.java.com.minutesapp to javafx.fxml;
    opens main.java.com.minutesapp.controller to javafx.fxml;

    exports main.java.com.minutesapp;
    exports main.java.com.minutesapp.controller;
}