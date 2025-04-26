import java.time.LocalDateTime;

public class MinutesReport {
    private int id;
    private String title;
    private String content;
    private String attendees;
    private LocalDateTime meetingDate;
    private String decisions;
    private String actionItems;

    // Constructor
    public MinutesReport(int id, String title, String content, String attendees,
                         LocalDateTime meetingDate, String decisions, String actionItems) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.attendees = attendees;
        this.meetingDate = meetingDate;
        this.decisions = decisions;
        this.actionItems = actionItems;
    }

    // Empty constructor
    public MinutesReport() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAttendees() { return attendees; }
    public void setAttendees(String attendees) { this.attendees = attendees; }

    public LocalDateTime getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDateTime meetingDate) { this.meetingDate = meetingDate; }

    public String getDecisions() { return decisions; }
    public void setDecisions(String decisions) { this.decisions = decisions; }

    public String getActionItems() { return actionItems; }
    public void setActionItems(String actionItems) { this.actionItems = actionItems; }
}