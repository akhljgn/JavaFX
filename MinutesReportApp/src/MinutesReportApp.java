import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MinutesReportApp extends Application {
    private MinutesReportDAO dao = new MinutesReportDAO();
    private ObservableList<MinutesReport> reportsList = FXCollections.observableArrayList();
    private ListView<MinutesReport> reportsListView;

    // Form fields
    private TextField titleField;
    private DatePicker datePicker;
    private TextArea attendeesArea;
    private TextArea contentArea;
    private TextArea decisionsArea;
    private TextArea actionItemsArea;

    // Currently selected report
    private MinutesReport currentReport;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create the main layout
            BorderPane root = new BorderPane();

            // Create menu bar
            MenuBar menuBar = createMenuBar();
            root.setTop(menuBar);

            // Create side panel with list of reports
            reportsListView = new ListView<>();
            reportsListView.setPrefWidth(220);

            // Custom cell factory to display report titles
            reportsListView.setCellFactory(param -> new ListCell<MinutesReport>() {
                @Override
                protected void updateItem(MinutesReport item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });

            // Create form for editing reports
            VBox form = createReportForm();

            // Load reports from database
            loadReports();

            // Handle selection changes
            reportsListView.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            displayReport(newValue);
                        }
                    });

            // Add list and form to a split pane
            SplitPane splitPane = new SplitPane();
            splitPane.getItems().addAll(reportsListView, form);
            splitPane.setDividerPositions(0.3);

            root.setCenter(splitPane);

            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.setTitle("Minutes Report Application");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error",
                    "An error occurred while starting the application", e.getMessage());
        }
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New Report");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem deleteItem = new MenuItem("Delete Report");
        MenuItem exportItem = new MenuItem("Export as PDF");
        MenuItem exitItem = new MenuItem("Exit");

        // Event handlers
        newItem.setOnAction(e -> createNewReport());
        saveItem.setOnAction(e -> saveCurrentReport());
        deleteItem.setOnAction(e -> deleteCurrentReport());
        exitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(newItem, saveItem, deleteItem, exportItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private VBox createReportForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        Label titleLabel = new Label("Title:");
        titleField = new TextField();

        Label dateLabel = new Label("Meeting Date:");
        datePicker = new DatePicker();

        Label attendeesLabel = new Label("Attendees:");
        attendeesArea = new TextArea();
        attendeesArea.setPrefHeight(80);

        Label contentLabel = new Label("Meeting Content:");
        contentArea = new TextArea();
        contentArea.setPrefHeight(150);

        Label decisionsLabel = new Label("Decisions Made:");
        decisionsArea = new TextArea();
        decisionsArea.setPrefHeight(100);

        Label actionItemsLabel = new Label("Action Items:");
        actionItemsArea = new TextArea();
        actionItemsArea.setPrefHeight(100);

        HBox buttonBox = new HBox(10);
        Button saveButton = new Button("Save");
        Button clearButton = new Button("Clear");
        Button newButton = new Button("New Report");

        // Event handlers
        saveButton.setOnAction(e -> saveCurrentReport());
        clearButton.setOnAction(e -> clearForm());
        newButton.setOnAction(e -> createNewReport());

        buttonBox.getChildren().addAll(saveButton, newButton, clearButton);

        form.getChildren().addAll(
                titleLabel, titleField,
                dateLabel, datePicker,
                attendeesLabel, attendeesArea,
                contentLabel, contentArea,
                decisionsLabel, decisionsArea,
                actionItemsLabel, actionItemsArea,
                buttonBox
        );

        return form;
    }

    private void loadReports() {
        try {
            List<MinutesReport> reports = dao.getAllReports();
            reportsList.clear();
            reportsList.addAll(reports);
            reportsListView.setItems(reportsList);

            if (!reports.isEmpty()) {
                reportsListView.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load reports from database", e.getMessage());
        }
    }

    private void displayReport(MinutesReport report) {
        currentReport = report;

        titleField.setText(report.getTitle());
        datePicker.setValue(report.getMeetingDate().toLocalDate());
        attendeesArea.setText(report.getAttendees());
        contentArea.setText(report.getContent());
        decisionsArea.setText(report.getDecisions());
        actionItemsArea.setText(report.getActionItems());
    }

    private void saveCurrentReport() {
        try {
            if (titleField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Title is required", "Please enter a title for the report.");
                return;
            }

            if (datePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Date is required", "Please select a meeting date.");
                return;
            }

            boolean isNewReport = (currentReport == null || currentReport.getId() == 0);

            // Create or update report object
            if (isNewReport) {
                currentReport = new MinutesReport();
            }

            currentReport.setTitle(titleField.getText());
            currentReport.setAttendees(attendeesArea.getText());
            currentReport.setContent(contentArea.getText());
            currentReport.setDecisions(decisionsArea.getText());
            currentReport.setActionItems(actionItemsArea.getText());

            // Convert LocalDate to LocalDateTime
            LocalDateTime meetingDateTime = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.of(9, 0) // Default to 9:00 AM
            );
            currentReport.setMeetingDate(meetingDateTime);

            boolean success;
            if (isNewReport) {
                success = dao.createReport(currentReport);
                if (success) {
                    // Reload to get the generated ID
                    loadReports();

                    // Select the newly added item
                    for (int i = 0; i < reportsList.size(); i++) {
                        if (reportsList.get(i).getTitle().equals(currentReport.getTitle())) {
                            reportsListView.getSelectionModel().select(i);
                            break;
                        }
                    }
                }
            } else {
                success = dao.updateReport(currentReport);
                if (success) {
                    // Refresh the list
                    loadReports();

                    // Reselect the updated item
                    for (int i = 0; i < reportsList.size(); i++) {
                        if (reportsList.get(i).getId() == currentReport.getId()) {
                            reportsListView.getSelectionModel().select(i);
                            break;
                        }
                    }
                }
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Report saved successfully", null);
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Failed to save report", null);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "An error occurred while saving the report", e.getMessage());
        }
    }

    private void createNewReport() {
        clearForm();
        currentReport = null;
        reportsListView.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());  // Set today's date by default
        titleField.requestFocus();  // Focus on title field
    }

    private void deleteCurrentReport() {
        if (currentReport == null || currentReport.getId() == 0) {
            showAlert(Alert.AlertType.WARNING, "Warning",
                    "No report selected", "Please select a report to delete.");
            return;
        }

        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Report");
        alert.setContentText("Are you sure you want to delete the report: " + currentReport.getTitle() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = dao.deleteReport(currentReport.getId());
                if (success) {
                    loadReports();
                    clearForm();
                    currentReport = null;

                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Report deleted successfully", null);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to delete report", null);
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "An error occurred while deleting the report", e.getMessage());
            }
        }
    }

    private void clearForm() {
        titleField.clear();
        datePicker.setValue(null);
        attendeesArea.clear();
        contentArea.clear();
        decisionsArea.clear();
        actionItemsArea.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}