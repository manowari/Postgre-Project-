package org.example;
import java.sql.*;

public class DatabaseManager {

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public DatabaseManager(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public void insertData(String title, String content) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String insertQuery = "INSERT INTO posts (title, content) VALUES (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, content);

                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println(rowsAffected + " row(s) inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void retrieveData() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            String selectQuery = "SELECT * FROM posts";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");

                    System.out.println("ID: " + id + ", Title: " + title + ", Content: " + content);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
