package com.minutesapp.controller;

import com.minutesapp.dao.AttendeeDAO;
import com.minutesapp.model.Attendee;
import com.minutesapp.util.AlertUtils;
import com.minutesapp.util.ValidationUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AttendeeController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private AttendeeDAO attendeeDAO = new AttendeeDAO();
    private boolean isAddedSuccessfully = false;

    // For editing mode
    private boolean editMode = false;
    private Attendee currentAttendee;

    @FXML
    void initialize() {
        // Initialize department combo box with common departments
        departmentComboBox.getItems().addAll(
                "Engineering",
                "Marketing",
                "Sales",
                "Human Resources",
                "Finance",
                "Operations",
                "Research & Development",
                "Customer Support",
                "Legal",
                "Executive",
                "Other"
        );

        // Initialize role combo box with common roles
        roleComboBox.getItems().addAll(
                "Manager",
                "Team Lead",
                "Developer",
                "Designer",
                "QA Engineer",
                "Product Owner",
                "Scrum Master",
                "Business Analyst",
                "Stakeholder",
                "Client",
                "Other"
        );

        // Default selection
        departmentComboBox.setValue("Other");
        roleComboBox.setValue("Other");

        // Add listeners for validation
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateFields();
        });

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateFields();
        });

        departmentComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateFields();
        });

        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateFields();
        });

        // Run initial validation
        validateFields();
    }

    /**
     * Set the controller to edit mode and populate fields with existing attendee data
     * @param attendee The attendee to edit
     */
    public void setEditMode(Attendee attendee) {
        this.editMode = true;
        this.currentAttendee = attendee;

        // Populate fields with existing data
        nameField.setText(attendee.getName());
        emailField.setText(attendee.getEmail());

        // Set department, add if not in list
        String department = attendee.getDepartment();
        if (!departmentComboBox.getItems().contains(department)) {
            departmentComboBox.getItems().add(department);
        }
        departmentComboBox.setValue(department);

        // Set role, add if not in list
        String role = attendee.getRole();
        if (!roleComboBox.getItems().contains(role)) {
            roleComboBox.getItems().add(role);
        }
        roleComboBox.setValue(role);

        // Update save button text
        saveButton.setText("Update");
    }

    private void validateFields() {
        boolean nameValid = !nameField.getText().trim().isEmpty();
        boolean emailValid = !emailField.getText().trim().isEmpty() &&
                ValidationUtils.isValidEmail(emailField.getText().trim());
        boolean departmentValid = departmentComboBox.getValue() != null;
        boolean roleValid = roleComboBox.getValue() != null;

        // For debugging
        System.out.println("Validation status: Name=" + nameValid +
                ", Email=" + emailValid +
                ", Department=" + departmentValid +
                ", Role=" + roleValid);

        boolean isValid = nameValid && emailValid && departmentValid && roleValid;
        saveButton.setDisable(!isValid);
    }

    @FXML
    void handleSave(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentComboBox.getValue();
        String role = roleComboBox.getValue();

        // Additional validation
        if (name.isEmpty() || email.isEmpty() || department == null || role == null) {
            AlertUtils.showErrorAlert("Validation Error", "Missing Information",
                    "Please fill in all required fields.");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            AlertUtils.showErrorAlert("Validation Error", "Invalid Email",
                    "Please enter a valid email address.");
            return;
        }

        // Check if email already exists (skip this check if we're in edit mode and the email hasn't changed)
        if (!editMode || !email.equals(currentAttendee.getEmail())) {
            if (attendeeDAO.emailExists(email)) {
                AlertUtils.showErrorAlert("Validation Error", "Duplicate Email",
                        "An attendee with this email address already exists.");
                return;
            }
        }

        boolean success;

        if (editMode) {
            // Update existing attendee
            currentAttendee.setName(name);
            currentAttendee.setEmail(email);
            currentAttendee.setDepartment(department);
            currentAttendee.setRole(role);

            success = attendeeDAO.updateAttendee(currentAttendee);

            if (success) {
                isAddedSuccessfully = true;
                AlertUtils.showInfoAlert("Success", "Attendee Updated",
                        "Attendee information has been successfully updated.");
                closeStage();
            } else {
                AlertUtils.showErrorAlert("Error", "Update Failed",
                        "Failed to update attendee. Please try again.");
            }
        } else {
            // Create and save new attendee
            Attendee attendee = new Attendee();
            attendee.setName(name);
            attendee.setEmail(email);
            attendee.setDepartment(department);
            attendee.setRole(role);

            success = attendeeDAO.addAttendee(attendee);

            if (success) {
                isAddedSuccessfully = true;
                AlertUtils.showInfoAlert("Success", "Attendee Added",
                        "Attendee has been successfully added to the database.");
                closeStage();
            } else {
                AlertUtils.showErrorAlert("Error", "Save Failed",
                        "Failed to add attendee. Please try again.");
            }
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Returns whether the attendee was successfully added or updated
     * @return true if operation was successful, false otherwise
     */
    public boolean isAddedSuccessfully() {
        return isAddedSuccessfully;
    }
}