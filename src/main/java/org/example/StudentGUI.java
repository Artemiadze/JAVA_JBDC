package org.example;

import javax.swing.*;
import java.awt.*;

public class StudentGUI extends JFrame {
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField groupField;
    private final JTextField searchField;
    private final JTextArea outputArea;
    private final StudentManager dbManager;

    public StudentGUI() {
        setTitle("Управление");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

        dbManager = new StudentManager();

        nameField = new JTextField();
        emailField = new JTextField();
        groupField = new JTextField();
        searchField = new JTextField();
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);

        JButton addButton = new JButton("Добавить");
        JButton searchButton = new JButton("Найти");
        JButton updateButton = new JButton("Обновить");
        JButton deleteButton = new JButton("Удалить");
        JButton clearTableButton = new JButton("Очистить таблицу");
        JButton createDBButton = new JButton("Создать БД");
        JButton dropDBButton = new JButton("Удалить БД");

        addButton.addActionListener(e -> addStudent());
        searchButton.addActionListener(e -> searchStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearTableButton.addActionListener(e -> dbManager.truncateStudentTable());
        createDBButton.addActionListener(e -> dbManager.createDatabase("student"));
        dropDBButton.addActionListener(e -> dbManager.dropDatabase("student"));

        add(new JLabel("Имя:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Группа:"));
        add(groupField);
        add(new JLabel("Поиск (Имя):"));
        add(searchField);
        add(addButton);
        add(searchButton);
        add(updateButton);
        add(deleteButton);
        add(clearTableButton);
        add(createDBButton);
        add(dropDBButton);
        add(new JScrollPane(outputArea));

        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText();
        String email = emailField.getText();
        String group = groupField.getText();
        dbManager.addStudent(name, email, group);
        outputArea.setText("Студент добавлен: " + name);
    }

    private void searchStudent() {
        String name = searchField.getText();
        String result = dbManager.searchStudentByName(name);
        outputArea.setText(result);
    }

    private void updateStudent() {
        String name = nameField.getText();
        String email = emailField.getText();
        String group = groupField.getText();
        dbManager.updateStudent(name, email, group);
        outputArea.setText("Студент обновлён: " + name);
    }

    private void deleteStudent() {
        String name = searchField.getText();
        dbManager.deleteStudentByName(name);
        outputArea.setText("Студент удалён: " + name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentGUI::new);
    }
}