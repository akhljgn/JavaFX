package com.minutesapp.controller;


import com.minutesapp.dao.AttendeeDAO;
import com.minutesapp.dao.MinutesReportDAO;
import com.minutesapp.model.Attendee;
import com.minutesapp.model.MinutesReport;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreateReportPageController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<String> meetingTypeComboBox;

    @FXML
    private DatePicker meetingDatePicker;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea agendaTextArea;

    @FXML
    private TextArea discussionTextArea;

    @FXML
    private TextArea decisionsTextArea;

    @FXML
    private TextArea actionItemsTextArea;

    @FXML
    private TableView<Attendee> attendeesTable;

    @FXML
    private TableColumn<Attendee, String> nameColumn;

    @FXML
    private TableColumn<Attendee, String> roleColumn;

    @FXML
    private TableColumn<Attendee, String> departmentColumn;

    @FXML
    private ListView<Attendee> selectedAttendeesListView;

    @FXML
    private Button manageAttendeesButton;

    @FXML
    private Button removeAttendeeButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private AttendeeDAO attendeeDAO = new AttendeeDAO();
    private MinutesReportDAO reportDAO = new MinutesReportDAO();
    private ObservableList<Attendee> allAttendees = FXCollections.observableArrayList();
    private ObservableList<Attendee> selectedAttendees = FXCollections.observableArrayList();

    private boolean isUpdateMode = false;
    private int reportIdToUpdate;

    @FXML
    void initialize() {
        // Setup meeting types
        ObservableList<String> meetingTypes = FXCollections.observableArrayList(
                "Department Meeting", "Project Review", "Board Meeting",
                "Team Status", "Client Meeting", "Other"
        );
        meetingTypeComboBox.setItems(meetingTypes);

        // Set today's date as default
        meetingDatePicker.setValue(LocalDate.now());

        // Setup time formatters for validation
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Setup columns for attendees table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Load all attendees from database
        loadAttendees();

        // Setup selected attendees list view
        selectedAttendeesListView.setItems(selectedAttendees);
        selectedAttendeesListView.setCellFactory(lv -> new ListCell<Attendee>() {
            @Override
            protected void updateItem(Attendee attendee, boolean empty) {
                super.updateItem(attendee, empty);
                setText(empty ? null : attendee.getName() + " (" + attendee.getRole() + ")");
            }
        });

        attendeesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Loads an existing report for updating
     * @param reportId ID of the report to update
     */
    public void loadReportForUpdate(int reportId) {
        // Set form to update mode
        this.isUpdateMode = true;
        this.reportIdToUpdate = reportId;

        // Load the report from database
        MinutesReport report = reportDAO.getReportById(reportId);

        if (report != null) {
            // Update form title
            if (titleLabel != null) {
                titleLabel.setText("Update Meeting Minutes");
            }

            // Update save button text
            saveButton.setText("Update Report");

            // Populate form fields with report data
            titleField.setText(report.getTitle());
            meetingTypeComboBox.setValue(report.getMeetingType());
            meetingDatePicker.setValue(report.getMeetingDate());

            // Format times
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            startTimeField.setText(report.getStartTime().format(timeFormatter));
            endTimeField.setText(report.getEndTime().format(timeFormatter));

            locationField.setText(report.getLocation());
            agendaTextArea.setText(report.getAgenda());
            discussionTextArea.setText(report.getDiscussion());
            decisionsTextArea.setText(report.getDecisions());
            actionItemsTextArea.setText(report.getActionItems());

            // Load attendees
            selectedAttendees.clear();
            if (report.getAttendees() != null) {
                selectedAttendees.addAll(report.getAttendees());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Report Not Found",
                    "Could not find the selected report. Please try again.");
        }
    }

    private void loadAttendees() {
        allAttendees.clear();
        allAttendees.addAll(attendeeDAO.getAllAttendees());
        attendeesTable.setItems(allAttendees);
    }

    @FXML
    void handleAddAttendee(ActionEvent event) {
        Attendee selectedAttendee = attendeesTable.getSelectionModel().getSelectedItem();
        if (selectedAttendee != null && !selectedAttendees.contains(selectedAttendee)) {
            selectedAttendees.add(selectedAttendee);
        }
    }

    @FXML
    void handleRemoveAttendee(ActionEvent event) {
        Attendee selectedAttendee = selectedAttendeesListView.getSelectionModel().getSelectedItem();
        if (selectedAttendee != null) {
            selectedAttendees.remove(selectedAttendee);
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (validateInputs()) {
            MinutesReport report = new MinutesReport();

            // If updating, set the report ID
            if (isUpdateMode) {
                report.setId(reportIdToUpdate);
            }

            report.setTitle(titleField.getText());
            report.setMeetingType(meetingTypeComboBox.getValue());
            report.setMeetingDate(meetingDatePicker.getValue());

            // Parse time strings to LocalTime
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            report.setStartTime(LocalTime.parse(startTimeField.getText(), timeFormatter));
            report.setEndTime(LocalTime.parse(endTimeField.getText(), timeFormatter));

            report.setLocation(locationField.getText());
            report.setAgenda(agendaTextArea.getText());
            report.setDiscussion(discussionTextArea.getText());
            report.setDecisions(decisionsTextArea.getText());
            report.setActionItems(actionItemsTextArea.getText());

            // Set attendees
            List<Attendee> attendees = new ArrayList<>(selectedAttendees);
            report.setAttendees(attendees);

            boolean success;

            // Save or update based on mode
            if (isUpdateMode) {
                success = reportDAO.updateReport(report);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Report Updated",
                            "The minutes report has been updated successfully.");
                    navigateToViewReportsPage();  // Navigate to view reports after update
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Update Failed",
                            "Failed to update the minutes report. Please try again.");
                }
            } else {
                success = reportDAO.saveReport(report);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Report Saved",
                            "The minutes report has been saved successfully.");
                    navigateToWelcomePage();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Save Failed",
                            "Failed to save the minutes report. Please try again.");
                }
            }
        }
    }

    private boolean validateInputs() {
        StringBuilder errorMessages = new StringBuilder();

        if (titleField.getText().isEmpty()) {
            errorMessages.append("- Title is required\n");
        }

        if (meetingTypeComboBox.getValue() == null) {
            errorMessages.append("- Meeting type is required\n");
        }

        if (meetingDatePicker.getValue() == null) {
            errorMessages.append("- Meeting date is required\n");
        }

        String startTime = startTimeField.getText();
        if (startTime.isEmpty()) {
            errorMessages.append("- Start time is required\n");
        } else {
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime.parse(startTime, timeFormatter);
            } catch (Exception e) {
                errorMessages.append("- Start time format should be HH:MM (24-hour)\n");
            }
        }

        String endTime = endTimeField.getText();
        if (endTime.isEmpty()) {
            errorMessages.append("- End time is required\n");
        } else {
            try {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime.parse(endTime, timeFormatter);
            } catch (Exception e) {
                errorMessages.append("- End time format should be HH:MM (24-hour)\n");
            }
        }

        if (locationField.getText().isEmpty()) {
            errorMessages.append("- Location is required\n");
        }

        if (selectedAttendees.isEmpty()) {
            errorMessages.append("- At least one attendee must be selected\n");
        }

        if (errorMessages.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please correct the following errors:",
                    errorMessages.toString());
            return false;
        }

        return true;
    }

    @FXML
    void handleCancel(ActionEvent event) {
        if (isUpdateMode) {
            navigateToViewReportsPage();
        } else {
            navigateToWelcomePage();
        }
    }

    private void navigateToWelcomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WelcomePage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("Minutes Report Application");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToViewReportsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewReportsPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("View Reports");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation Error",
                    "Could not navigate to view reports page. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}