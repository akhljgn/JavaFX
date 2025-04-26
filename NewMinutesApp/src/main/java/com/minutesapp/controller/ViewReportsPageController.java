package com.minutesapp.controller;

import com.minutesapp.dao.MinutesReportDAO;
import com.minutesapp.model.MinutesReport;
import com.minutesapp.util.AlertUtils;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ViewReportsPageController {
    @FXML
    private TableView<MinutesReport> reportsTable;

    @FXML
    private TableColumn<MinutesReport, Integer> idColumn;

    @FXML
    private TableColumn<MinutesReport, String> titleColumn;

    @FXML
    private TableColumn<MinutesReport, String> typeColumn;

    @FXML
    private TableColumn<MinutesReport, LocalDate> dateColumn;

    @FXML
    private TableColumn<MinutesReport, String> locationColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearSearchButton;

    @FXML
    private Button viewButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    private MinutesReportDAO reportDAO = new MinutesReportDAO();
    private ObservableList<MinutesReport> reports = FXCollections.observableArrayList();
    private FilteredList<MinutesReport> filteredReports;

    @FXML
    void initialize() {
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("meetingType"));

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("meetingDate"));
        dateColumn.setCellFactory(column -> new TableCell<MinutesReport, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? "" : formatter.format(date));
            }
        });

        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Load reports
        loadReports();

        // Setup filtered list
        filteredReports = new FilteredList<>(reports, p -> true);
        reportsTable.setItems(filteredReports);

        // Setup search functionality
        searchField.setPromptText("Search by title");
        searchButton.setOnAction(event -> performSearch());
        clearSearchButton.setOnAction(event -> clearSearch());

        // Also allow pressing Enter in the search field to perform search
        searchField.setOnAction(event -> performSearch());

        // Ensure the search field is not focused by default
        Platform.runLater(() -> {
            searchField.setFocusTraversable(false);
            reportsTable.requestFocus();
        });

        // Add listener for button enable/disable based on selection
        reportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            viewButton.setDisable(!hasSelection);
            updateButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        // Disable buttons initially
        viewButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        reportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            filteredReports.setPredicate(p -> true); // Show all if search is empty
        } else {
            filteredReports.setPredicate(report -> {
                // If report title contains search text (case insensitive)
                return report.getTitle().toLowerCase().contains(searchText);
            });
        }

        // Update table selection and button states
        reportsTable.getSelectionModel().clearSelection();
        viewButton.setDisable(true);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // Show feedback if no results found
        if (filteredReports.isEmpty()) {
            AlertUtils.showInfoAlert("Search Results", "No Matching Reports",
                    "No reports found with titles containing: " + searchText);
        }
    }

    @FXML
    void clearSearch() {
        searchField.clear();
        filteredReports.setPredicate(p -> true); // Show all reports
    }

    private void loadReports() {
        reports.clear();
        List<MinutesReport> allReports = reportDAO.getAllReports();
        reports.addAll(allReports);
        if (filteredReports != null) {
            filteredReports.setPredicate(p -> true); // Reset filter
        }
    }

    @FXML
    void handleViewReport(ActionEvent event) {
        MinutesReport selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReportDetailsPage.fxml"));
                Parent root = loader.load();

                ReportDetailsController controller = loader.getController();
                controller.loadReport(selectedReport.getId());

                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

                Stage stage = (Stage) viewButton.getScene().getWindow();
                boolean isMaximized = stage.isMaximized();
                stage.setTitle("Report Details - " + selectedReport.getTitle());
                stage.setScene(scene);
                Platform.runLater(() -> stage.setMaximized(isMaximized));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                AlertUtils.showErrorAlert("Error", "Navigation Error",
                        "Could not open report details. Please try again.");
            }
        }
    }

    @FXML
    void handleUpdateReport(ActionEvent event) {
    MinutesReport selectedReport = reportsTable.getSelectionModel().getSelectedItem();
    if (selectedReport != null) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateReportPage.fxml"));
            Parent root = loader.load();

            CreateReportPageController controller = loader.getController();
            controller.loadReportForUpdate(selectedReport.getId());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) updateButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("Update Report - " + selectedReport.getTitle());
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error",
                    "Could not open update form. Please try again.");
            }
        }
    }

    @FXML
    void handleDeleteReport(ActionEvent event) {
        MinutesReport selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            // Ask for confirmation before deleting
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Report");
            confirmDialog.setContentText("Are you sure you want to delete the report \"" +
                    selectedReport.getTitle() + "\"? This action cannot be undone.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = reportDAO.deleteReport(selectedReport.getId());

                if (deleted) {
                    loadReports(); // Refresh the table
                    AlertUtils.showInfoAlert("Success", "Report Deleted",
                            "The report has been deleted successfully.");
                } else {
                    AlertUtils.showErrorAlert("Error", "Delete Failed",
                            "Failed to delete the report. Please try again.");
                }
            }
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WelcomePage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) backButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("Minutes Report Application");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error",
                    "Could not navigate to the welcome page. Please try again.");
        }
    }
}