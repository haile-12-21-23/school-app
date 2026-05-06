
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainFrame extends JFrame {

    private DBHelper db;

    public MainFrame(DBHelper db) {
        this.db = db;
        setTitle("School System");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Students", createStudentPanel());
        tabs.add("Quizzes", createQuizPanel());
        tabs.add("Scores", createScorePanel());

        add(tabs, BorderLayout.CENTER);

        try {
            this.db = db;
        } catch (Exception e) {
            showError(e);
        }
    }

    private JPanel createStudentPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel studentModel = new DefaultTableModel();
        JTable studentTable = new JTable(studentModel);

        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();

        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");

        btnPanel.add(add);
        btnPanel.add(update);
        btnPanel.add(delete);
        btnPanel.add(refresh);

        panel.add(btnPanel, BorderLayout.SOUTH);

        // LOAD DATA
        Runnable loadStudents = () -> {
            try {
                studentModel.setRowCount(0);
                studentModel.setColumnCount(0);

                studentModel.addColumn("RollNo");
                studentModel.addColumn("Name");
                studentModel.addColumn("Section");
                studentModel.addColumn("Email");

                ResultSet rs = db.getConnection()
                        .createStatement()
                        .executeQuery("SELECT * FROM Student");

                while (rs.next()) {
                    studentModel.addRow(new Object[] {
                            rs.getString("RollNo"),
                            rs.getString("StudentName"),
                            rs.getString("Section"),
                            rs.getString("Email")
                    });
                }

            } catch (Exception e) {
                showError(e);
            }
        };

        refresh.addActionListener(e -> loadStudents.run());

        // ADD
        add.addActionListener(e -> openAddStudentDialog());

        // DELETE
        delete.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row == -1)
                return;

            String roll = studentModel.getValueAt(row, 0).toString();

            try {
                db.deleteStudent(roll);
                loadStudents.run();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // UPDATE
        update.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row == -1)
                return;

            String roll = studentModel.getValueAt(row, 0).toString();
            String name = studentModel.getValueAt(row, 1).toString();
            String section = studentModel.getValueAt(row, 2).toString();
            String email = studentModel.getValueAt(row, 3).toString();

            JTextField nameF = new JTextField(name);
            JTextField secF = new JTextField(section);
            JTextField emailF = new JTextField(email);

            Object[] fields = {
                    "Name:", nameF,
                    "Section:", secF,
                    "Email:", emailF
            };

            int ok = JOptionPane.showConfirmDialog(this, fields, "Update", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                try {
                    db.updateStudent(roll, nameF.getText(), secF.getText(), emailF.getText());
                    loadStudents.run();
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        loadStudents.run(); // auto load

        return panel;
    }

    // Quiz Tab
    private JPanel createQuizPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton load = new JButton("Refresh");
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");

        JPanel btnPanel = new JPanel();
        btnPanel.add(load);
        btnPanel.add(add);
        btnPanel.add(update);
        btnPanel.add(delete);

        panel.add(btnPanel, BorderLayout.SOUTH);

        // Load data
        Runnable loadQuizzes = () -> {
            try {
                model.setRowCount(0);
                model.setColumnCount(0);

                model.addColumn("QuizCode");
                model.addColumn("Title");
                model.addColumn("Total");

                ResultSet rs = db.getConnection()
                        .createStatement()
                        .executeQuery("SELECT * FROM Quiz");

                while (rs.next()) {
                    model.addRow(new Object[] {
                            rs.getString("QuizCode"),
                            rs.getString("QuizTitle"),
                            rs.getInt("TotalMarks")
                    });
                }

            } catch (Exception e) {
                showError(e);
            }
        };
        load.addActionListener(e -> loadQuizzes.run());
        // Add
        add.addActionListener(e -> openAddQuizDialog(model, loadQuizzes));
        // Update
        update.addActionListener(e -> updateQuizRow(table, model, loadQuizzes));
        // DELETE
        delete.addActionListener(e -> deleteQuizRow(table, model, loadQuizzes));
        loadQuizzes.run();
        return panel;
    }

    private JPanel createScorePanel() {

        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton load = new JButton("Load Scores");
        JButton addScore = new JButton("Add Score");
        JButton update = new JButton("Update Selected");
        JButton delete = new JButton("Delete Selected");

        JPanel btnPanel = new JPanel();
        btnPanel.add(load);
        btnPanel.add(addScore);
        btnPanel.add(update);
        btnPanel.add(delete);
        panel.add(btnPanel, BorderLayout.SOUTH);
        // Load Scores

        // private void loadScores(DefaultTableModel model) {
        // try {
        // model.setRowCount(0);
        // model.setColumnCount(0);

        // model.addColumn("Student");
        // model.addColumn("RollNo");
        // model.addColumn("Quiz");
        // model.addColumn("Marks");
        // model.addColumn("Total");

        // ResultSet rs = db.getScores();

        // while (rs.next()) {
        // model.addRow(new Object[] {
        // rs.getString("StudentName"),
        // rs.getString("RollNo"),
        // rs.getString("QuizTitle"),
        // rs.getInt("MarksObtained"),
        // rs.getInt("TotalMarks")
        // });
        // }
        Runnable loadScores = () -> {
            try {
                model.setRowCount(0);
                model.setColumnCount(0);

                model.addColumn("Student");
                model.addColumn("RollNo");
                model.addColumn("Quiz");
                model.addColumn("Marks");
                model.addColumn("Total");

                // ResultSet rs = db.getConnection()
                // .createStatement()
                // .executeQuery("SELECT * FROM Quiz");
                ResultSet rs = db.getScores();

                while (rs.next()) {
                    model.addRow(new Object[] {
                            rs.getString("StudentName"),
                            rs.getString("RollNo"),
                            rs.getString("QuizTitle"),
                            rs.getInt("MarksObtained"),
                            rs.getInt("TotalMarks")
                    });
                }

            } catch (Exception e) {
                showError(e);
            }
        };

        load.addActionListener(e -> loadScores.run());
        addScore.addActionListener(e -> openAddScoreDialog(model));

        update.addActionListener(e -> updateScoreRow(table, model));
        delete.addActionListener(e -> deleteScoreRow(table, model));
        loadScores.run();

        return panel;
    }

    private void loadScores(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            model.setColumnCount(0);

            model.addColumn("Student");
            model.addColumn("RollNo");
            model.addColumn("Quiz");
            model.addColumn("Marks");
            model.addColumn("Total");

            ResultSet rs = db.getScores();

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("StudentName"),
                        rs.getString("RollNo"),
                        rs.getString("QuizTitle"),
                        rs.getInt("MarksObtained"),
                        rs.getInt("TotalMarks")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void openAddScoreDialog(DefaultTableModel model) {

        JDialog dialog = new JDialog(this, "Add Score", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2));

        JComboBox<String> studentBox = new JComboBox<>();
        JComboBox<String> quizBox = new JComboBox<>();
        JTextField marksField = new JTextField();

        try {
            // Load students
            ResultSet rs1 = db.getStudents();
            while (rs1.next()) {
                studentBox.addItem(
                        rs1.getString("RollNo") + " - " + rs1.getString("StudentName"));
            }

            // Load quizzes
            ResultSet rs2 = db.getQuizzes();
            while (rs2.next()) {
                quizBox.addItem(
                        rs2.getString("QuizCode") + " - " + rs2.getString("QuizTitle"));
            }

        } catch (Exception e) {
            showError(e);
        }

        dialog.add(new JLabel("Student:"));
        dialog.add(studentBox);

        dialog.add(new JLabel("Quiz:"));
        dialog.add(quizBox);

        dialog.add(new JLabel("Marks:"));
        dialog.add(marksField);

        JButton save = new JButton("Save");
        dialog.add(new JLabel()); // spacer
        dialog.add(save);

        save.addActionListener(e -> {
            try {
                String student = studentBox.getSelectedItem().toString();
                String quiz = quizBox.getSelectedItem().toString();

                String roll = student.split(" - ")[0];
                String quizCode = quiz.split(" - ")[0];

                int marks = Integer.parseInt(marksField.getText());

                int rows = db.insertScore(roll, quizCode, marks);

                JOptionPane.showMessageDialog(this, rows + " score added");

                dialog.dispose();
                loadScores(model);

            } catch (Exception ex) {
                showError(ex);
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateScoreRow(JTable table, DefaultTableModel model) {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first!");
            return;
        }

        String roll = model.getValueAt(row, 1).toString();
        String quiz = model.getValueAt(row, 2).toString();

        // Extract quizCode (if format: CODE - TITLE)
        String quizCode = quiz.contains(" - ") ? quiz.split(" - ")[0] : quiz;

        String newMarksStr = JOptionPane.showInputDialog(this, "Enter new marks:");

        if (newMarksStr == null)
            return;

        try {
            int newMarks = Integer.parseInt(newMarksStr);

            int updated = db.updateScore(roll, quizCode, newMarks);

            JOptionPane.showMessageDialog(this, updated + " score updated");

            loadScores(model);

        } catch (Exception e) {
            showError(e);
        }
    }

    private void deleteScoreRow(JTable table, DefaultTableModel model) {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first!");
            return;
        }

        String roll = model.getValueAt(row, 1).toString();
        String quiz = model.getValueAt(row, 2).toString();

        String quizCode = quiz.contains(" - ") ? quiz.split(" - ")[0] : quiz;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this score?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int deleted = db.deleteScore(roll, quizCode);

                JOptionPane.showMessageDialog(this, deleted + " score deleted");

                loadScores(model);

            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void openAddStudentDialog() {

        JDialog dialog = new JDialog(this, "Add Student", true);
        dialog.setLocation(20, 10);
        dialog.setSize(260, 180);
        dialog.setLayout(new GridLayout(5, 2));

        JTextField roll = new JTextField();
        JTextField name = new JTextField();
        JTextField section = new JTextField();
        JTextField email = new JTextField();

        dialog.add(new JLabel("Roll No:"));
        dialog.add(roll);
        dialog.add(new JLabel("Name:"));
        dialog.add(name);
        dialog.add(new JLabel("Section:"));
        dialog.add(section);
        dialog.add(new JLabel("Email:"));
        dialog.add(email);

        JButton save = new JButton("Save");
        dialog.add(save);

        save.addActionListener(e -> {
            try {
                int rows = db.insertStudent(
                        roll.getText(),
                        name.getText(),
                        section.getText(),
                        email.getText());

                JOptionPane.showMessageDialog(this, rows + " student added");
                dialog.dispose();
                new MainFrame(db).setVisible(true);

            } catch (Exception ex) {
                showError(ex);
            }
        });

        dialog.setVisible(true);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
        e.printStackTrace();
    }

    // Scores
    private void openAddQuizDialog(DefaultTableModel model, Runnable reload) {

        JDialog dialog = new JDialog(this, "Add Quiz", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2));

        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField total = new JTextField();

        dialog.add(new JLabel("Quiz Code:"));
        dialog.add(code);

        dialog.add(new JLabel("Title:"));
        dialog.add(title);

        dialog.add(new JLabel("Total Marks:"));
        dialog.add(total);

        JButton save = new JButton("Save");
        dialog.add(new JLabel());
        dialog.add(save);

        save.addActionListener(e -> {
            try {
                int rows = db.insertQuiz(
                        code.getText(),
                        title.getText(),
                        Integer.parseInt(total.getText()));

                JOptionPane.showMessageDialog(this, rows + " quiz added");
                dialog.dispose();
                reload.run();

            } catch (Exception ex) {
                showError(ex);
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateQuizRow(JTable table, DefaultTableModel model, Runnable reload) {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first!");
            return;
        }

        String code = model.getValueAt(row, 0).toString();
        String title = model.getValueAt(row, 1).toString();
        int total = Integer.parseInt(model.getValueAt(row, 2).toString());

        JTextField titleF = new JTextField(title);
        JTextField totalF = new JTextField(String.valueOf(total));

        Object[] fields = {
                "Title:", titleF,
                "Total:", totalF
        };

        int ok = JOptionPane.showConfirmDialog(this, fields, "Update Quiz", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                int updated = db.updateQuiz(
                        code,
                        titleF.getText(),
                        Integer.parseInt(totalF.getText()));

                JOptionPane.showMessageDialog(this, updated + " updated");
                reload.run();

            } catch (Exception e) {
                showError(e);
            }
        }
    }

    private void deleteQuizRow(JTable table, DefaultTableModel model, Runnable reload) {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first!");
            return;
        }

        String code = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete quiz " + code + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int deleted = db.deleteQuiz(code);

                JOptionPane.showMessageDialog(this, deleted + " deleted");
                reload.run();

            } catch (Exception e) {
                showError(e);
            }
        }
    }

}
