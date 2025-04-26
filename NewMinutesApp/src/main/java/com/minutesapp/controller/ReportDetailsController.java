package com.minutesapp.controller;

import com.minutesapp.model.Attendee;
import com.minutesapp.model.MinutesReport;
import com.minutesapp.dao.MinutesReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReportDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private ListView<Attendee> attendeesListView;

    @FXML
    private TextArea agendaTextArea;

    @FXML
    private TextArea discussionTextArea;

    @FXML
    private TextArea decisionsTextArea;

    @FXML
    private TextArea actionItemsTextArea;

    @FXML
    private Button backButton;

    private MinutesReport report;
    private MinutesReportDAO reportDAO = new MinutesReportDAO();
    private ObservableList<Attendee> attendees = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        // Setup attendees list view
        attendeesListView.setCellFactory(lv -> new ListCell<Attendee>() {
            @Override
            protected void updateItem(Attendee attendee, boolean empty) {
                super.updateItem(attendee, empty);
                setText(empty ? null : attendee.getName() + " (" + attendee.getRole() + " - " + attendee.getDepartment() + ")");
            }
        });
    }

    public void loadReport(int reportId) {
        this.report = reportDAO.getReportById(reportId);

        if (report != null) {
            displayReportDetails();
        }
    }

    private void displayReportDetails() {
        titleLabel.setText(report.getTitle());
        typeLabel.setText(report.getMeetingType());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateLabel.setText(report.getMeetingDate().format(dateFormatter));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        startTimeLabel.setText(report.getStartTime().format(timeFormatter));
        endTimeLabel.setText(report.getEndTime().format(timeFormatter));

        locationLabel.setText(report.getLocation());

        // Set attendees
        attendees.clear();
        attendees.addAll(report.getAttendees());
        attendeesListView.setItems(attendees);

        // Set content
        agendaTextArea.setText(report.getAgenda());
        discussionTextArea.setText(report.getDiscussion());
        decisionsTextArea.setText(report.getDecisions());
        actionItemsTextArea.setText(report.getActionItems());
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewReportsPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            Stage stage = (Stage) backButton.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();
            stage.setTitle("View Reports");
            stage.setScene(scene);
            Platform.runLater(() -> stage.setMaximized(isMaximized));  
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}