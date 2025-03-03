package org.example;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class StudentManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgre";

    // Создание БД
    public void createDatabase() {
        System.out.println("Registering JDBC driver...");

        System.out.println("Connecting to DB...");

        System.out.println("Creating database...");

        String SQL = "CREATE DATABASE students";
        executeUpdate(SQL);
        System.out.println("Database successfully created...");

        createStudentTable();
    }

    // Создание таблицы student после создания БД
    private void createStudentTable() {
        String newDbURL = "jdbc:postgresql://localhost:5432/";

        String createTableSQL = "CREATE TABLE students (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "group_name VARCHAR(50) NOT NULL" +
                ");";


        try (Connection conn = DriverManager.getConnection(newDbURL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createTableSQL);
            System.out.println("Таблица students успешно создана в БД " + "student");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы student: " + e.getMessage());
        }
    }

    // Удаление БД
    public void dropDatabase() {
        String SQL = "DROP DATABASE students";
        executeUpdate(SQL);

        System.out.println("Database successfully created...");
    }

    public void addStudent(String name, String email, String group) {
        executeUpdate("{ call insert_student(?, ?, ?) }", name, email, group);
    }

    public String searchStudentByName(String name) {
        StringBuilder result = new StringBuilder();
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall("{ call search_student_by_name(?) }")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("id"))
                        .append(", Имя: ").append(rs.getString("name"))
                        .append(", Email: ").append(rs.getString("email"))
                        .append(", Группа: ").append(rs.getString("group_name"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public void updateStudent(String name, String email, String group) {
        executeUpdate("{ call update_student(?, ?, ?) }", name, email, group);
    }

    public void deleteStudentByName(String name) {
        executeUpdate("{ call delete_student_by_name(?) }", name);
    }

    public void truncateStudentTable() {
        executeUpdate("{ call truncate_student() }");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void executeUpdate(String query, String... params) {
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setString(i + 1, params[i]);
            }
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
