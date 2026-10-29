package com.gym;

import java.sql.*;

public class GymDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/gym_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; 

    public void addMember(String name, String plan, int duration) throws SQLException {
        String sql = "INSERT INTO members(name, plan, duration) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, plan);
            ps.setInt(3, duration);
            ps.executeUpdate();
        }
    }

    public String getMembers() throws SQLException {
        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT member_id, name, plan, duration FROM members ORDER BY member_id DESC";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(',');
                first = false;
                json.append("{\"id\":").append(rs.getInt("member_id"))
                    .append(",\"name\":\"").append(escape(rs.getString("name")))
                    .append("\",\"plan\":\"").append(escape(rs.getString("plan")))
                    .append("\",\"duration\":").append(rs.getInt("duration"))
                    .append('}');
            }
        }
        return json.append(']').toString();
    }

    private String escape(String s) { 
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); 
    }
}
