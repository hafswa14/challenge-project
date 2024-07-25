package com.challenge.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Application started.");

        try {
            DatabaseManager dbManager = initializeDatabaseManager();
            FileManager fileManager = new FileManager();
            ChallengeManager challengeManager = new ChallengeManager(dbManager);
            EmailService emailService = new EmailService(); // Blank implementation

            challengeManager.createChallenge("Math Challenge 1", "2024-06-01", "2024-06-30", "01:00:00");
            int challengeId = getChallengeId(dbManager, "Math Challenge 1");

            List<Question> questions = fileManager.readQuestionsFromExcel("questions.xlsx");
            insertQuestions(dbManager, questions, challengeId);

            emailService.sendEmail("recipient@example.com", "Challenge Created", "A new challenge has been created.");

            startServerSocket(dbManager, 12345);

        } catch (SQLException | IOException e) {
            System.err.println("An error occurred:");
            e.printStackTrace();
        }

        System.out.println("Application finished.");
    }

    private static DatabaseManager initializeDatabaseManager() throws SQLException {
        System.out.println("Initializing DatabaseManager...");
        DatabaseManager dbManager = new DatabaseManager();
        System.out.println("DatabaseManager initialized.");
        return dbManager;
    }

    private static int getChallengeId(DatabaseManager dbManager, String challengeName) throws SQLException {
        String query = "SELECT id FROM Challenges WHERE name = ?";
        try (Connection connection = dbManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, challengeName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    private static void insertQuestions(DatabaseManager dbManager, List<Question> questions, int challengeId)
            throws SQLException {
        String query = "INSERT INTO Questions (challenge_id, question_text, answer, marks) VALUES (?, ?, ?, ?)";
        try (Connection connection = dbManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Question question : questions) {
                statement.setInt(1, challengeId);
                statement.setString(2, question.getQuestionText());
                statement.setString(3, question.getAnswer());
                statement.setInt(4, question.getMarks());
                statement.executeUpdate();
            }
        }
    }

    private static void startServerSocket(DatabaseManager dbManager, int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ServerClientHandler(socket, dbManager).start();
            }
        }
    }
}
