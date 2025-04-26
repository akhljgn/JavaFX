package com.minutesapp.dao;

import com.minutesapp.model.Attendee;
import com.minutesapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendeeDAO {

    /**
     * Adds a new attendee to the database
     * @param attendee The attendee to add
     * @return true if successful, false otherwise
     */
    public boolean addAttendee(Attendee attendee) {
        String query = "INSERT INTO attendees (name, email, department, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, attendee.getName());
            stmt.setString(2, attendee.getEmail());
            stmt.setString(3, attendee.getDepartment());
            stmt.setString(4, attendee.getRole());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all attendees from the database
     * @return List of all attendees
     */
    public List<Attendee> getAllAttendees() {
        List<Attendee> attendees = new ArrayList<>();
        String query = "SELECT id, name, email, department, role FROM attendees ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Attendee attendee = new Attendee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("role")
                );
                attendees.add(attendee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendees;
    }

    /**
     * Gets an attendee by their ID
     * @param id The ID of the attendee to retrieve
     * @return The attendee or null if not found
     */
    public Attendee getAttendeeById(int id) {
        String query = "SELECT id, name, email, department, role FROM attendees WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Attendee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("role")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updates an existing attendee in the database
     * @param attendee The attendee with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateAttendee(Attendee attendee) {
        String query = "UPDATE attendees SET name = ?, email = ?, department = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, attendee.getName());
            stmt.setString(2, attendee.getEmail());
            stmt.setString(3, attendee.getDepartment());
            stmt.setString(4, attendee.getRole());
            stmt.setInt(5, attendee.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an attendee from the database
     * @param id The ID of the attendee to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteAttendee(int id) {
        String query = "DELETE FROM attendees WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if an attendee with the given email already exists
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM attendees WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Searches for attendees by name, email, department or role
     * @param searchText The text to search for
     * @return List of matching attendees
     */
    public List<Attendee> searchAttendees(String searchText) {
        List<Attendee> attendees = new ArrayList<>();
        String query = "SELECT id, name, email, department, role FROM attendees " +
                "WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(department) LIKE ? OR LOWER(role) LIKE ? " +
                "ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchText.toLowerCase() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendee attendee = new Attendee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("role")
                    );
                    attendees.add(attendee);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendees;
    }
}