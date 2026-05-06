import java.sql.*;

public class DBHelper {

    private Connection conn;

    public DBHelper() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/school_db?useSSL=false&serverTimezone=UTC",
                "root",
                "Haile#122123#");
    }

    public Connection getConnection() {
        return conn;
    }

    public ResultSet getScores() throws Exception {
        String query = "SELECT s.StudentName, s.RollNo, q.QuizTitle, " +
                "sc.MarksObtained, q.TotalMarks " +
                "FROM Score sc " +
                "JOIN Student s ON sc.StudentRoll = s.RollNo " +
                "JOIN Quiz q ON sc.QuizCode = q.QuizCode";

        Statement st = conn.createStatement();
        return st.executeQuery(query);
    }

    public int insertStudent(String roll, String name, String section, String email) throws Exception {
        String sql = "INSERT INTO Student (RollNo, StudentName, Section, Email) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, roll);
        ps.setString(2, name);
        ps.setString(3, section);
        ps.setString(4, email);
        return ps.executeUpdate();
    }

    public int updateStudent(String roll, String name, String section, String email) throws Exception {
        String sql = "UPDATE Student SET StudentName=?, Section=?, Email=? WHERE RollNo=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, section);
        ps.setString(3, email);
        ps.setString(4, roll);
        return ps.executeUpdate();
    }

    public int deleteStudent(String roll) throws Exception {
        String sql = "DELETE FROM Student WHERE RollNo=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, roll);
        return ps.executeUpdate();
    }

    public boolean login(String username, String password) throws Exception {
        String sql = "SELECT * FROM User WHERE username=? AND password=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        return rs.next(); // true if found
    }

    public ResultSet getStudents() throws Exception {
        return conn.createStatement().executeQuery(
                "SELECT RollNo, StudentName FROM Student");
    }

    public ResultSet getQuizzes() throws Exception {
        return conn.createStatement().executeQuery(
                "SELECT QuizCode, QuizTitle FROM Quiz");
    }

    public int insertScore(String roll, String quizCode, int marks) throws Exception {
        String sql = "INSERT INTO Score (StudentRoll, QuizCode, MarksObtained) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, roll);
        ps.setString(2, quizCode);
        ps.setInt(3, marks);
        return ps.executeUpdate();
    }

    public int updateScore(String roll, String quizCode, int marks) throws Exception {
        String sql = "UPDATE Score SET MarksObtained=? WHERE StudentRoll=? AND QuizCode=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, marks);
        ps.setString(2, roll);
        ps.setString(3, quizCode);
        return ps.executeUpdate();
    }

    public int deleteScore(String roll, String quizCode) throws Exception {
        String sql = "DELETE FROM Score WHERE StudentRoll=? AND QuizCode=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, roll);
        ps.setString(2, quizCode);
        return ps.executeUpdate();
    }

    // Quiz
    public int insertQuiz(String code, String title, int total) throws Exception {
        String sql = "INSERT INTO Quiz (QuizCode, QuizTitle, TotalMarks) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, code);
        ps.setString(2, title);
        ps.setInt(3, total);
        return ps.executeUpdate();
    }

    public int updateQuiz(String code, String title, int total) throws Exception {
        String sql = "UPDATE Quiz SET QuizTitle=?, TotalMarks=? WHERE QuizCode=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, title);
        ps.setInt(2, total);
        ps.setString(3, code);
        return ps.executeUpdate();
    }

    public int deleteQuiz(String code) throws Exception {
        String sql = "DELETE FROM Quiz WHERE QuizCode=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, code);
        return ps.executeUpdate();
    }
}