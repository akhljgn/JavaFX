package com.minutesapp.dao;

import com.minutesapp.model.Attendee;
import com.minutesapp.model.MinutesReport;
import com.minutesapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MinutesReportDAO {
    private AttendeeDAO attendeeDAO = new AttendeeDAO();

    public boolean saveReport(MinutesReport report) {
        String query = "INSERT INTO minutes_reports (title, meeting_type, meeting_date, start_time, " +
                "end_time, location, agenda, discussion, decisions, action_items) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, report.getTitle());
            stmt.setString(2, report.getMeetingType());
            stmt.setDate(3, Date.valueOf(report.getMeetingDate()));
            stmt.setTime(4, Time.valueOf(report.getStartTime()));
            stmt.setTime(5, Time.valueOf(report.getEndTime()));
            stmt.setString(6, report.getLocation());
            stmt.setString(7, report.getAgenda());
            stmt.setString(8, report.getDiscussion());
            stmt.setString(9, report.getDecisions());
            stmt.setString(10, report.getActionItems());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int reportId = generatedKeys.getInt(1);

                    // Save the attendees relationship
                    String attendeeQuery = "INSERT INTO report_attendees (report_id, attendee_id) VALUES (?, ?)";
                    try (PreparedStatement attendeeStmt = conn.prepareStatement(attendeeQuery)) {
                        for (Attendee attendee : report.getAttendees()) {
                            attendeeStmt.setInt(1, reportId);
                            attendeeStmt.setInt(2, attendee.getId());
                            attendeeStmt.executeUpdate();
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing report in the database
     * @param report The report to update with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean updateReport(MinutesReport report) {
        String query = "UPDATE minutes_reports SET title = ?, meeting_type = ?, meeting_date = ?, " +
                "start_time = ?, end_time = ?, location = ?, agenda = ?, discussion = ?, " +
                "decisions = ?, action_items = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Use a transaction to ensure data consistency
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, report.getTitle());
                stmt.setString(2, report.getMeetingType());
                stmt.setDate(3, Date.valueOf(report.getMeetingDate()));
                stmt.setTime(4, Time.valueOf(report.getStartTime()));
                stmt.setTime(5, Time.valueOf(report.getEndTime()));
                stmt.setString(6, report.getLocation());
                stmt.setString(7, report.getAgenda());
                stmt.setString(8, report.getDiscussion());
                stmt.setString(9, report.getDecisions());
                stmt.setString(10, report.getActionItems());
                stmt.setInt(11, report.getId());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                // Update attendees - first delete existing relationships
                String deleteAttendees = "DELETE FROM report_attendees WHERE report_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteAttendees)) {
                    deleteStmt.setInt(1, report.getId());
                    deleteStmt.executeUpdate();
                }

                // Then insert new attendee relationships
                if (report.getAttendees() != null && !report.getAttendees().isEmpty()) {
                    String attendeeQuery = "INSERT INTO report_attendees (report_id, attendee_id) VALUES (?, ?)";
                    try (PreparedStatement attendeeStmt = conn.prepareStatement(attendeeQuery)) {
                        for (Attendee attendee : report.getAttendees()) {
                            attendeeStmt.setInt(1, report.getId());
                            attendeeStmt.setInt(2, attendee.getId());
                            attendeeStmt.executeUpdate();
                        }
                    }
                }

                // Commit the transaction
                conn.commit();
                return true;

            } catch (SQLException e) {
                // If there's an error, roll back the transaction
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                // Reset auto-commit to default
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReport(int id) {
        // First delete attendee relationships
        String deleteAttendees = "DELETE FROM report_attendees WHERE report_id = ?";
        String deleteReport = "DELETE FROM minutes_reports WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Use a transaction
            conn.setAutoCommit(false);

            try (PreparedStatement deleteAttendeeStmt = conn.prepareStatement(deleteAttendees);
                 PreparedStatement deleteReportStmt = conn.prepareStatement(deleteReport)) {

                deleteAttendeeStmt.setInt(1, id);
                deleteAttendeeStmt.executeUpdate();

                deleteReportStmt.setInt(1, id);
                int affectedRows = deleteReportStmt.executeUpdate();

                conn.commit();
                return affectedRows > 0;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MinutesReport> getAllReports() {
        List<MinutesReport> reports = new ArrayList<>();
        String query = "SELECT * FROM minutes_reports ORDER BY meeting_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                MinutesReport report = mapResultSetToReport(rs);
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    public MinutesReport getReportById(int id) {
        String query = "SELECT * FROM minutes_reports WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReport(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private MinutesReport mapResultSetToReport(ResultSet rs) throws SQLException {
        MinutesReport report = new MinutesReport();
        report.setId(rs.getInt("id"));
        report.setTitle(rs.getString("title"));
        report.setMeetingType(rs.getString("meeting_type"));
        report.setMeetingDate(rs.getDate("meeting_date").toLocalDate());
        report.setStartTime(rs.getTime("start_time").toLocalTime());
        report.setEndTime(rs.getTime("end_time").toLocalTime());
        report.setLocation(rs.getString("location"));
        report.setAgenda(rs.getString("agenda"));
        report.setDiscussion(rs.getString("discussion"));
        report.setDecisions(rs.getString("decisions"));
        report.setActionItems(rs.getString("action_items"));

        // Get the attendees for this report
        List<Attendee> attendees = getReportAttendees(report.getId());
        report.setAttendees(attendees);

        return report;
    }

    private List<Attendee> getReportAttendees(int reportId) {
        List<Attendee> attendees = new ArrayList<>();
        String query = "SELECT a.* FROM attendees a " +
                "JOIN report_attendees ra ON a.id = ra.attendee_id " +
                "WHERE ra.report_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reportId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendee attendee = new Attendee();
                    attendee.setId(rs.getInt("id"));
                    attendee.setName(rs.getString("name"));
                    attendee.setEmail(rs.getString("email"));
                    attendee.setDepartment(rs.getString("department"));
                    attendee.setRole(rs.getString("role"));
                    attendees.add(attendee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendees;
    }
}