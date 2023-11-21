package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class PostgreSqlExample {

    public static void main(String[] args) {
        try {
            System.out.println("Loading PostgreSQL driver...");
            Class.forName("org.postgresql.Driver");

            Properties properties = loadProperties();
            String jdbcUrl = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            String dbName = properties.getProperty("db.database");

            System.out.println("Trying to connect to the database...");
            try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
                if (isDatabaseConnected(connection, dbName)) {
                    System.out.println("Connected to the database!");

                    // Continue with other operations
                    System.out.println("Creating table...");
                    createTable(connection);

                    System.out.println("Inserting data...");
                    insertData(connection, "John Doe", "john@example.com", 25);

                    System.out.println("Retrieving data...");
                    retrieveData(connection);

                    System.out.println("Printing tables...");
                    printTables(connection);
                } else {
                    System.out.println("Database does not exist or connection failed.");
                }
            } catch (SQLException e) {
                handleSQLException(e);
            }
        } catch (ClassNotFoundException e) {
            handleClassNotFoundException(e);
        }
    }

    private static boolean isDatabaseConnected(Connection connection, String dbName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getCatalogs();

        while (resultSet.next()) {
            String existingDbName = resultSet.getString("TABLE_CAT");
            if (existingDbName.equals(dbName)) {
                return true;
            }
        }

        return false;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = PostgreSqlExample.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Error: Unable to find db.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error: Unable to load db.properties");
            e.printStackTrace();
        }
        return properties;
    }

    private static boolean isDatabaseConnected(Connection connection) throws SQLException {
        return connection.isValid(5);
    }

    private static void createTable(Connection connection) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "age INT" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
            System.out.println("Table 'users' created successfully.");
        } catch (SQLException e) {
            handleSQLException(e);
            System.out.println("Error creating table. Query: " + createTableQuery);
        }
    }


    private static void insertData(Connection connection, String name, String email, int age) {
        String insertQuery = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setInt(3, age);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private static void retrieveData(Connection connection) {
        String selectQuery = "SELECT * FROM users";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                int age = resultSet.getInt("age");

                System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email + ", Age: " + age);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private static void printTables(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            System.out.println("List of Tables in the Database:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(tableName);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private static void handleSQLException(SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
        e.printStackTrace();
    }

    private static void handleClassNotFoundException(ClassNotFoundException e) {
        System.out.println("Error: PostgreSQL JDBC Driver not found.");
        e.printStackTrace();
    }
}
