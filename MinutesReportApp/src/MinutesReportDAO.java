import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MinutesReportDAO {

    // Create a new report
    public boolean createReport(MinutesReport report) {
        String sql = "INSERT INTO minutes_reports (title, content, attendees, meeting_date, decisions, action_items) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getTitle());
            stmt.setString(2, report.getContent());
            stmt.setString(3, report.getAttendees());
            stmt.setTimestamp(4, Timestamp.valueOf(report.getMeetingDate()));
            stmt.setString(5, report.getDecisions());
            stmt.setString(6, report.getActionItems());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all reports
    public List<MinutesReport> getAllReports() {
        List<MinutesReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM minutes_reports ORDER BY meeting_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MinutesReport report = new MinutesReport();
                report.setId(rs.getInt("id"));
                report.setTitle(rs.getString("title"));
                report.setContent(rs.getString("content"));
                report.setAttendees(rs.getString("attendees"));
                report.setMeetingDate(rs.getTimestamp("meeting_date").toLocalDateTime());
                report.setDecisions(rs.getString("decisions"));
                report.setActionItems(rs.getString("action_items"));

                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    // Get a report by ID
    public MinutesReport getReportById(int id) {
        String sql = "SELECT * FROM minutes_reports WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MinutesReport report = new MinutesReport();
                    report.setId(rs.getInt("id"));
                    report.setTitle(rs.getString("title"));
                    report.setContent(rs.getString("content"));
                    report.setAttendees(rs.getString("attendees"));
                    report.setMeetingDate(rs.getTimestamp("meeting_date").toLocalDateTime());
                    report.setDecisions(rs.getString("decisions"));
                    report.setActionItems(rs.getString("action_items"));

                    return report;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update a report
    public boolean updateReport(MinutesReport report) {
        String sql = "UPDATE minutes_reports SET title = ?, content = ?, attendees = ?, " +
                "meeting_date = ?, decisions = ?, action_items = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getTitle());
            stmt.setString(2, report.getContent());
            stmt.setString(3, report.getAttendees());
            stmt.setTimestamp(4, Timestamp.valueOf(report.getMeetingDate()));
            stmt.setString(5, report.getDecisions());
            stmt.setString(6, report.getActionItems());
            stmt.setInt(7, report.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a report
    public boolean deleteReport(int id) {
        String sql = "DELETE FROM minutes_reports WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}