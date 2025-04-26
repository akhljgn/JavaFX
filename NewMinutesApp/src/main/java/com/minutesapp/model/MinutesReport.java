package com.minutesapp.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MinutesReport {
    private int id;
    private String title;
    private String meetingType;
    private LocalDate meetingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String agenda;
    private String discussion;
    private String decisions;
    private String actionItems;
    private List<Attendee> attendees;

    // Constructors
    public MinutesReport() {}

    public MinutesReport(int id, String title, String meetingType, LocalDate meetingDate,
                         LocalTime startTime, LocalTime endTime, String location,
                         String agenda, String discussion, String decisions,
                         String actionItems, List<Attendee> attendees) {
        this.id = id;
        this.title = title;
        this.meetingType = meetingType;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.agenda = agenda;
        this.discussion = discussion;
        this.decisions = decisions;
        this.actionItems = actionItems;
        this.attendees = attendees;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMeetingType() { return meetingType; }
    public void setMeetingType(String meetingType) { this.meetingType = meetingType; }

    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAgenda() { return agenda; }
    public void setAgenda(String agenda) { this.agenda = agenda; }

    public String getDiscussion() { return discussion; }
    public void setDiscussion(String discussion) { this.discussion = discussion; }

    public String getDecisions() { return decisions; }
    public void setDecisions(String decisions) { this.decisions = decisions; }

    public String getActionItems() { return actionItems; }
    public void setActionItems(String actionItems) { this.actionItems = actionItems; }

    public List<Attendee> getAttendees() { return attendees; }
    public void setAttendees(List<Attendee> attendees) { this.attendees = attendees; }
}
