package org.example;
import java.sql.*;

public class StudentManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgre";

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

    public void createDatabase(String dbName) {
        executeUpdate("SELECT create_database(?)", dbName);
    }

    public void dropDatabase(String dbName) {
        String url = "jdbc:postgresql://localhost:5432/postgres"; // Подключаемся к default-базе

        try (Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Завершаем все активные подключения к базе перед удалением
            stmt.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '" + dbName + "'");

            // Удаляем базу данных
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + dbName);
            System.out.println("База данных " + dbName + " удалена.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
