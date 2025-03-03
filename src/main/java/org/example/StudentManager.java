package org.example;
import java.sql.*;


public class StudentManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgre";

    // Создание БД
    public void createDatabase() {
        System.out.println("Registering JDBC driver...");

        System.out.println("Connecting to DB...");

        System.out.println("Creating database...");

        String SQL = "CREATE DATABASE student";
        executeUpdate(SQL);
        System.out.println("Database successfully created...");

        createStudentTable(); // Создание самой таблицы
        AddNecessaryFunction(); // Добавление на сервер хранимых функций
    }

    // Создание таблицы student после создания БД
    private void createStudentTable() {
        String newDbURL = "jdbc:postgresql://localhost:5432/student";

        String createTableSQL = "CREATE TABLE students (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "group_name VARCHAR(50) NOT NULL" +
                ");";


        try (Connection conn = getConnection(newDbURL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createTableSQL);
            System.out.println("Таблица students успешно создана в БД " + "student");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы student: " + e.getMessage());
        }
    }

    // Удаление БД
    public void dropDatabase() {
        String SQL = "DROP DATABASE student";
        executeUpdate(SQL);

        System.out.println("Database successfully deleted...");
    }

    // Добавление процедур на сервер
    public void AddNecessaryFunction(){
        String newURL = "jdbc:postgresql://localhost:5432/student";

        // Очистка таблицы Course
        String TruncateSQL = "CREATE OR REPLACE FUNCTION truncate_student() RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "    TRUNCATE TABLE students RESTART IDENTITY CASCADE;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";

        // Добавление студента
        String InsertSQL = "CREATE OR REPLACE FUNCTION insert_student(insert_name VARCHAR, insert_email VARCHAR, insert_group VARCHAR)" +
                "RETURNS VOID AS $$" +
                "BEGIN" +
                "    INSERT INTO students (name, email, group_name) VALUES (insert_name, insert_email, insert_group);" +
                "END;" +
                "$$ LANGUAGE plpgsql;";

        // Получение списка студентов по имени
        String SearchNameSQL = "CREATE OR REPLACE FUNCTION search_student_by_name(search_name VARCHAR)\n" +
                "RETURNS TABLE(id INT, name VARCHAR, email VARCHAR, group_name VARCHAR) AS $$\n" +
                "BEGIN\n" +
                "    RETURN QUERY \n" +
                "    SELECT students.id, students.name, students.email, students.group_name\n" +
                "    FROM students\n" +
                "    WHERE students.name ILIKE '%' || search_name || '%';\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;\n";

        // Обновление данных студента
        String UpdateSQL = "CREATE OR REPLACE FUNCTION update_student(update_id INT, update_name VARCHAR, update_email VARCHAR, update_group VARCHAR) \n" +
                "RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "    UPDATE students SET name = update_name, email = update_email, group_name = update_group WHERE id = update_id;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";

        // Удаление студента по email
        String DeleteSQL = "CREATE OR REPLACE FUNCTION delete_student_by_email(delete_email VARCHAR)" +
                "RETURNS VOID AS $$" +
                "BEGIN" +
                "    DELETE FROM students WHERE students.email = delete_email;" +
                "END;" +
                "$$ LANGUAGE plpgsql;";


        try (Connection conn = getConnection(newURL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(TruncateSQL);
            stmt.executeUpdate(InsertSQL);
            stmt.executeUpdate(SearchNameSQL);
            stmt.executeUpdate(UpdateSQL);
            stmt.executeUpdate(DeleteSQL);

            System.out.println("Процедуры успешно добавлены " + "student");

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлений процедур в student: " + e.getMessage());
        }
    }

    // Добавление данных о студенте в таблицу
    public void addStudent(String name, String email, String group) {
        String newURL = "jdbc:postgresql://localhost:5432/student";

        String callFunctionSQL = "SELECT insert_student(?, ?, ?);";
        try (Connection conn = getConnection(newURL)) {
            // Вызов функции для вставки данных
            try (PreparedStatement pstmt = conn.prepareStatement(callFunctionSQL)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, group);

                pstmt.execute();
                System.out.println("Student inserted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Поиск студента по имени
    public String searchStudentByName(String name) {
        String newURL = "jdbc:postgresql://localhost:5432/student";
        String callFunctionSQL = "SELECT * FROM search_student_by_name(?);";

        StringBuilder result = new StringBuilder();
        try (Connection conn = getConnection(newURL)) {
            // Вызов функции для обновления данных
            try (PreparedStatement pstmt = conn.prepareStatement(callFunctionSQL)) {
                pstmt.setString(1, name);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    result.append("ID: ").append(rs.getInt("id"))
                            .append(", Имя: ").append(rs.getString("name"))
                            .append(", Email: ").append(rs.getString("email"))
                            .append(", Группа: ").append(rs.getString("group_name"))
                            .append("\n");
                }
                System.out.println("Student was found successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    // Обновление данных о студенте
    public void updateStudent(String name, String email, String group) {
        String newURL = "jdbc:postgresql://localhost:5432/student";

        String callFunctionSQL = "SELECT update_student(?, ?, ?);";
        try (Connection conn = getConnection(newURL)) {
            // Вызов функции для обновления данных
            try (PreparedStatement pstmt = conn.prepareStatement(callFunctionSQL)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, group);

                pstmt.execute();
                System.out.println("Student was updated successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Удаление данных о студенте по почте
    public void deleteStudentByEmail(String name) {
        String newURL = "jdbc:postgresql://localhost:5432/student";

        String callFunctionSQL = "SELECT delete_student_by_email(?);";
        try (Connection conn = getConnection(newURL)) {
            // Вызов функции для вставки данных
            try (PreparedStatement pstmt = conn.prepareStatement(callFunctionSQL)) {
                pstmt.setString(1, name);

                pstmt.execute();
                System.out.println("Student was deleted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Очистка таблицы
    public void truncateStudentTable() {
        String newURL = "jdbc:postgresql://localhost:5432/student";

        String callFunctionSQL = "SELECT truncate_student();";
        try (Connection conn = getConnection(newURL)) {
            // Вызов функции для вставки данных
            try (PreparedStatement pstmt = conn.prepareStatement(callFunctionSQL)) {
                pstmt.execute();
                System.out.println("Table was cleaned successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url, USER, PASSWORD);
    }

    private void executeUpdate(String query, String... params) {
        try (Connection conn = getConnection(URL);
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
