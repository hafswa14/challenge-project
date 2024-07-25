package com.challenge.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChallengeManager {
    private DatabaseManager dbManager;

    public ChallengeManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createChallenge(String name, String startDate, String endDate, String duration) throws SQLException {
        String query = "INSERT INTO Challenges (name, start_date, end_date, duration) VALUES (?, ?, ?, ?)";
        try (Connection connection = dbManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, startDate);
            statement.setString(3, endDate);
            statement.setString(4, duration);
            statement.executeUpdate();
        }
    }

    public List<Challenge> getChallenges() throws SQLException {
        List<Challenge> challenges = new ArrayList<>();
        String query = "SELECT * FROM Challenges";
        try (Connection connection = dbManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String startDate = resultSet.getString("start_date");
                String endDate = resultSet.getString("end_date");
                String duration = resultSet.getString("duration");
                challenges.add(new Challenge(id, name, startDate, endDate, duration));
            }
        }
        return challenges;
    }
}
