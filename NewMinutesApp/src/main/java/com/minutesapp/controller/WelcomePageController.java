package com.minutesapp.controller;

import com.minutesapp.dao.MinutesReportDAO;
import com.minutesapp.model.Attendee;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.minutesapp.util.AlertUtils;
import javafx.stage.Modality;

import java.io.IOException;

public class WelcomePageController {

    @FXML
    private Button createReportButton;

    @FXML
    private Button viewReportsButton;

    @FXML
    void initialize() {
        // Initialize if needed
    }

    @FXML
    void handleCreateReport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateReportPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) createReportButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("Create Minutes Report");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));  
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewReportsPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) viewReportsButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();

            stage.setTitle("View Reports");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));  
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleManageAttendees(ActionEvent event) {
        try {
            // Navigate to the Attendee Management page instead of showing a dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ManageAttendeesPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) createReportButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("Manage Attendees");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error",
                    "Could not open the attendees management page. Please try again.");
        }
    }

}
