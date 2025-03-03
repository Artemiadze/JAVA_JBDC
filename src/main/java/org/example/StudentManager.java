package org.example;
import java.sql.*;

public class StudentManager  {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgre";

    // Метод подключения к БД
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 1. Создание базы данных
    public void createDatabase(String dbName) {
        String sql = "SELECT create_database(?)";
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dbName);
            stmt.execute();
            System.out.println("База данных " + dbName + " создана.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Удаление базы данных
    public void dropDatabase(String dbName) {
        String sql = "SELECT drop_database(?)";
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dbName);
            stmt.execute();
            System.out.println("База данных " + dbName + " удалена.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Очистка таблицы Course
    public void truncateStudent() {
        String sql = "{CALL truncate_student()}";
        try (Connection conn = connect();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.execute();
            System.out.println("Таблица Student очищена.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 1. Добавление студента
    public void insertStudent(String name, String email, String group) {
        String sql = "{CALL insert_student(?, ?, ?)}";
        try (Connection conn = connect();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, group);
            stmt.execute();
            System.out.println("Студент добавлен: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Поиск студента по имени
    public void searchStudentByName(String name) {
        String sql = "{CALL search_student_by_name(?)}";
        try (Connection conn = connect();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Имя: " + rs.getString("name") +
                        ", Email: " + rs.getString("email") +
                        ", Группа: " + rs.getString("group_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Обновление данных студента
    public void updateStudent(int id, String name, String email, String group) {
        String sql = "{CALL update_student(?, ?, ?, ?)}";
        try (Connection conn = connect();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, group);
            stmt.execute();
            System.out.println("Студент обновлен: ID " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Удаление студента по email
    public void deleteStudentByEmail(String email) {
        String sql = "{CALL delete_student_by_email(?)}";
        try (Connection conn = connect();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, email);
            stmt.execute();
            System.out.println("Студент с email " + email + " удален.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Тестирование
    public static void main(String[] args) {
        StudentManager sm = new StudentManager();

        // Создание и удаление БД
        sm.createDatabase("student");
        sm.insertStudent("Иван Иванов", "ivan@example.com", "Группа A");
        sm.searchStudentByName("Иван");
        sm.updateStudent(1, "Иван Петров", "ivanpetrov@example.com", "Группа B");
        sm.deleteStudentByEmail("ivanpetrov@example.com");
        sm.dropDatabase("student");

        sm.truncateStudent();
    }
}
