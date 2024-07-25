package com.challenge.server;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ServerClientHandler extends Thread {
    private Socket socket;
    private DatabaseManager dbManager;
    private ChallengeManager challengeManager;

    public ServerClientHandler(Socket socket, DatabaseManager dbManager) {
        this.socket = socket;
        this.dbManager = dbManager;
        this.challengeManager = new ChallengeManager(dbManager);
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)) {

            String text;
            while ((text = reader.readLine()) != null) {
                System.out.println("Received from client: " + text);
                String[] parts = text.split(":", 2);

                switch (parts[0]) {
                    case "REGISTER":
                        handleRegister(parts[1], writer);
                        break;
                    case "VIEW_CHALLENGES":
                        handleViewChallenges(writer);
                        break;
                    case "ATTEMPT_CHALLENGE":
                        handleAttemptChallenge(parts[1], writer);
                        break;
                    case "CONFIRM_APPLICANT":
                        handleConfirmApplicant(parts[1], writer);
                        break;
                    default:
                        writer.println("Unknown command");
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegister(String details, PrintWriter writer) {
        String[] data = details.split(",");
        if (data.length < 7) {
            writer.println("Invalid registration data");
            return;
        }

        String username = data[0];
        String firstName = data[1];
        String lastName = data[2];
        String email = data[3];
        String dob = data[4];
        String schoolRegNum = data[5];
        String imageFilePath = data[6];

        String query = "INSERT INTO Participants (username, first_name, last_name, email, date_of_birth, school_reg_num, image_path, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";
        try (Connection connection = dbManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, email);
            statement.setString(5, dob);
            statement.setString(6, schoolRegNum);
            statement.setString(7, imageFilePath);
            statement.executeUpdate();
            writer.println("Registration successful for " + username);
        } catch (SQLException e) {
            writer.println("Registration failed: " + e.getMessage());
        }
    }

    private void handleViewChallenges(PrintWriter writer) {
        try {
            List<Challenge> challenges = challengeManager.getChallenges();
            for (Challenge challenge : challenges) {
                writer.println("ID: " + challenge.getId() + ", Name: " + challenge.getName() +
                        ", Start Date: " + challenge.getStartDate() + ", End Date: " +
                        challenge.getEndDate() + ", Duration: " + challenge.getDuration());
            }
            writer.println("END_OF_CHALLENGES");
        } catch (SQLException e) {
            writer.println("Error retrieving challenges");
            e.printStackTrace();
        }
    }

    private void handleAttemptChallenge(String details, PrintWriter writer) {
        try {
            String[] parts = details.split(",");
            if (parts.length < 3) {
                writer.println("Invalid attempt data");
                return;
            }

            int challengeId = Integer.parseInt(parts[0]);
            String username = parts[1];
            int score = Integer.parseInt(parts[2]);
            String attemptQuery = "INSERT INTO Attempts (challenge_id, username, score) VALUES (?, ?, ?)";
            try (Connection connection = dbManager.getConnection();
                    PreparedStatement statement = connection.prepareStatement(attemptQuery)) {
                statement.setInt(1, challengeId);
                statement.setString(2, username);
                statement.setInt(3, score);
                statement.executeUpdate();
                writer.println("Challenge " + challengeId + " attempt processed for " + username);
            }
        } catch (SQLException | NumberFormatException e) {
            writer.println("Error processing challenge attempt: " + e.getMessage());
        }
    }

    private void handleConfirmApplicant(String username, PrintWriter writer) {
        String query = "UPDATE Participants SET status = 'Confirmed' WHERE username = ?";
        try (Connection connection = dbManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                writer.println("Applicant " + username + " confirmed");
            } else {
                writer.println("Applicant " + username + " not found");
            }
        } catch (SQLException e) {
            writer.println("Error confirming applicant: " + e.getMessage());
        }
    }
}
