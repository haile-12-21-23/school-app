import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MainGUI {

    private JTable table;
    private DefaultTableModel model;
    private Connection conn;
    JFrame frame = new JFrame();

    public MainGUI() {

        frame.setTitle("School Database System");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Table setup
        model = new DefaultTableModel();
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();

        JButton btnInit = new JButton("Initialize DB");
        JButton btnShow = new JButton("Show Data");
        JButton btnUpdate = new JButton("Update Section");
        JButton btnDelete = new JButton("Delete Low Score");
        JButton btnAddStudent = new JButton("Add student");

        panel.add(btnInit);
        panel.add(btnShow);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnAddStudent);

        frame.add(panel, BorderLayout.SOUTH);

        // Button Action
        btnInit.addActionListener(e -> initializeDataBase());
        btnShow.addActionListener(e -> displayData());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> DeleteLowScore());
        btnAddStudent.addActionListener(e -> openAddStudentForm());

        connectDB();

    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/school_db?useSSL=false&serverTimezone=UTC",
                    "root",
                    "Haile#122123#");
            showMessage("Connected to database!\n");
        } catch (Exception e) {
            showError(e);

        }
    }

    private void initializeDataBase() {
        try {
            Statement st = conn.createStatement();

            // Create tables

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Student (" +
                    "StudentID int AUTO_INCREMENT PRIMARY KEY, " +
                    "RollNo varchar(20) UNIQUE, " +
                    "StudentName varchar(50), " +
                    "Section varchar(5), Email varchar(50))");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Quiz (" +
                    "QuizID int AUTO_INCREMENT PRIMARY KEY, " +
                    "QuizCode varchar(20) UNIQUE, " +
                    "QuizTitle varchar(100), TotalMarks int)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS Score (" +
                    "ScoreID int AUTO_INCREMENT PRIMARY KEY, " +
                    "StudentRoll varchar(20), QuizCode varchar(20), " +
                    "MarksObtained int)");
            // Insert sample data
            st.executeUpdate("INSERT IGNORE INTO Student (RollNo, StudentName, Section, Email) VALUES " +
                    "('RCD2015','Selamawit','A','s@email.com')," +
                    "('RCD2016','Abenezer','A','a@email.com')");

            // st.executeUpdate("INSERT INTO Quiz (QuizCode, QuizTitle, TotalMarks)
            // VALUES\n" + //
            // "('PROG003', 'Programming Fundamentals', 60),\n" + //
            // "('DATABASE002', 'Database Fundamentals', 60),\n" + //
            // "('SE022', 'Software Engineering', 60);");

            st.executeUpdate("INSERT INTO Score (StudentRoll, QuizCode, MarksObtained) VALUES\n" +
                    "('RCD2017', 'PROG001', 45);");
            showMessage("Database initialized!\n");
        } catch (Exception e) {
            showError(e);
        }
    }

    private void openAddStudentForm() {
        JTextField rollField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField sectionField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] fields = {
                "Roll No:", rollField,
                "Name:", nameField,
                "Section:", sectionField,
                "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(
                nameField,
                fields,
                "Add New Student",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String roll = rollField.getText();
            String name = nameField.getText();
            String section = sectionField.getText();
            String email = emailField.getText();

            if (roll.isEmpty() || name.isEmpty() || section.isEmpty() || email.isEmpty()) {
                showMessage("All fields are required.");
                return;
            }

            insertStudent(roll, name, section, email);

        }
    }

    private void insertStudent(String roll, String name, String section, String email) {
        try {
            String sql = "INSERT INTO Student (RollNO,studentName,Section, Email) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, roll);
            ps.setString(2, name);
            ps.setString(3, section);
            ps.setString(4, email);

            int rows = ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, rows + "student added");
            displayData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayData() {
        try {
            Statement st = conn.createStatement();

            // Clear table first
            model.setRowCount(0);
            model.setColumnCount(0);

            String query = "SELECT s.StudentName, s.RollNo, q.QuizTitle, " +
                    "sc.MarksObtained, q.TotalMarks " +
                    "FROM Score sc " +
                    "JOIN Student s ON sc.StudentRoll = s.RollNo " +
                    "JOIN Quiz q ON sc.QuizCode = q.QuizCode";
            ResultSet rs = st.executeQuery(query);

            JLabel label = new JLabel("STUDENT SCORES:\n\n");
            frame.add(label);
            // Add Columns
            model.addColumn("Student Name");
            model.addColumn("Roll No");
            model.addColumn("Quiz");
            model.addColumn("Marks");
            model.addColumn("Total");

            // Add rows
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("StudentName"),
                        rs.getString("RollNo"),
                        rs.getString("QuizTitle"),
                        rs.getString("MarksObtained"),
                        rs.getString("TotalMarks"),
                });

            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void updateStudent() {
        try {
            Statement st = conn.createStatement();

            int updated = st.executeUpdate("UPDATE Student SET Section='A+' WHERE RollNo='RCD2015'");

            showMessage(updated + " student updated!\n");

        } catch (Exception e) {
            showError(e);
        }
    }

    private void DeleteLowScore() {

        try {
            Statement st = conn.createStatement();
            int deleted = st.executeUpdate("DELETE FROM Score WHERE MarksObtained<40");

            showMessage(deleted + " record deleted!\n");
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage());

        e.printStackTrace();
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().frame.setVisible(true);
        });

    }

}
