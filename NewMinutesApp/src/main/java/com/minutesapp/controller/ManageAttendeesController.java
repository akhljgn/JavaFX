package com.minutesapp.controller;

import com.minutesapp.dao.AttendeeDAO;
import com.minutesapp.model.Attendee;
import com.minutesapp.util.AlertUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ManageAttendeesController {

    @FXML
    private TableView<Attendee> attendeeTableView;

    @FXML
    private TableColumn<Attendee, Integer> idColumn;

    @FXML
    private TableColumn<Attendee, String> nameColumn;

    @FXML
    private TableColumn<Attendee, String> emailColumn;

    @FXML
    private TableColumn<Attendee, String> departmentColumn;

    @FXML
    private TableColumn<Attendee, String> roleColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearSearchButton;

    private AttendeeDAO attendeeDAO = new AttendeeDAO();
    private ObservableList<Attendee> attendeeList = FXCollections.observableArrayList();
    private FilteredList<Attendee> filteredAttendees;

    @FXML
    void initialize() {
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Load initial attendee data
        loadAttendees();

        // Setup filtered list
        filteredAttendees = new FilteredList<>(attendeeList, p -> true);
        attendeeTableView.setItems(filteredAttendees);

        // Setup search functionality
        searchField.setPromptText("Search by name or email");
        searchButton.setOnAction(event -> performSearch());
        clearSearchButton.setOnAction(event -> clearSearch());

        // Also allow pressing Enter in the search field to perform search
        searchField.setOnAction(event -> performSearch());

        // Ensure the search field is not focused by default
        Platform.runLater(() -> {
            searchField.setFocusTraversable(false);
            attendeeTableView.requestFocus();
        });

        // Disable edit/delete buttons until selection
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable buttons when item is selected
        attendeeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }

    @FXML
    void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            filteredAttendees.setPredicate(p -> true); // Show all if search is empty
        } else {
            filteredAttendees.setPredicate(attendee -> {
                // Search by name or email (case insensitive)
                return attendee.getName().toLowerCase().contains(searchText) ||
                        attendee.getEmail().toLowerCase().contains(searchText) ||
                        (attendee.getDepartment() != null && attendee.getDepartment().toLowerCase().contains(searchText)) ||
                        (attendee.getRole() != null && attendee.getRole().toLowerCase().contains(searchText));
            });
        }

        // Update table selection and button states
        attendeeTableView.getSelectionModel().clearSelection();
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Show feedback if no results found
        if (filteredAttendees.isEmpty()) {
            AlertUtils.showInfoAlert("Search Results", "No Matching Attendees",
                    "No attendees found matching: " + searchText);
        }
    }

    @FXML
    void clearSearch() {
        searchField.clear();
        filteredAttendees.setPredicate(p -> true); // Show all attendees
    }

    @FXML
    void handleAddAttendee(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AttendeePage.fxml"));
            Parent root = loader.load();

            AttendeeController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Add New Attendee");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Show the dialog and wait for it to close
            stage.showAndWait();

            // Check if an attendee was added
            if (controller.isAddedSuccessfully()) {
                // Refresh attendee list
                loadAttendees();
            }

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error",
                    "Could not open the add attendee form. Please try again.");
        }
    }

    @FXML
    void handleEditAttendee(ActionEvent event) {
        // Get the selected attendee
        Attendee selectedAttendee = attendeeTableView.getSelectionModel().getSelectedItem();

        if (selectedAttendee == null) {
            AlertUtils.showErrorAlert("Selection Error", "No Attendee Selected",
                    "Please select an attendee to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AttendeePage.fxml"));
            Parent root = loader.load();

            AttendeeController controller = loader.getController();

            // Set the controller to edit mode with the selected attendee
            controller.setEditMode(selectedAttendee);

            Stage stage = new Stage();
            stage.setTitle("Edit Attendee");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Show the dialog and wait for it to close
            stage.showAndWait();

            // Check if an attendee was updated
            if (controller.isAddedSuccessfully()) {
                // Refresh attendee list
                loadAttendees();
            }

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorAlert("Error", "Navigation Error",
                    "Could not open the edit attendee form. Please try again.");
        }
    }

    @FXML
    void handleDeleteAttendee(ActionEvent event) {
        Attendee selectedAttendee = attendeeTableView.getSelectionModel().getSelectedItem();

        if (selectedAttendee == null) {
            AlertUtils.showErrorAlert("Selection Error", "No Attendee Selected",
                    "Please select an attendee to delete.");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmationAlert("Delete Attendee",
                "Confirm Deletion",
                "Are you sure you want to delete " + selectedAttendee.getName() + "?");

        if (confirmed) {
            boolean success = attendeeDAO.deleteAttendee(selectedAttendee.getId());

            if (success) {
                loadAttendees();
                AlertUtils.showInfoAlert("Success", "Attendee Deleted",
                        "Attendee has been successfully deleted.");
            } else {
                AlertUtils.showErrorAlert("Error", "Delete Failed",
                        "Failed to delete attendee. Please try again.");
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
            stage.setTitle("Meeting Minutes App");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAttendees() {
        List<Attendee> attendees = attendeeDAO.getAllAttendees();
        attendeeList.clear();
        attendeeList.addAll(attendees);
        if (filteredAttendees != null) {
            filteredAttendees.setPredicate(p -> true); // Reset filter
        }
    }
}