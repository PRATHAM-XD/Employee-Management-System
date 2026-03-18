import java.sql.*;
import java.time.LocalDate;

public class DBHelper {

    private static final String URL      = "jdbc:mysql://localhost:3306/emptrack";
    private static final String USER     = "root";
    private static final String PASSWORD = "your_password_here";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addEmployee(String name, String dept, double salary, String email) {
        String sql = "INSERT INTO employees (name, department, salary, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setDouble(3, salary);
            ps.setString(4, email);
            ps.executeUpdate();
            System.out.println("Employee added successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAllEmployees() {
        String sql = "SELECT * FROM employees ORDER BY id";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            printEmployeeTable(rs);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void updateEmployee(int id, int field, String newValue) {
        String[] columns = {"", "name", "department", "salary", "email"};
        if (field < 1 || field > 4) {
            System.out.println("Invalid field choice.");
            return;
        }
        String sql = "UPDATE employees SET " + columns[field] + " = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (field == 3) ps.setDouble(1, Double.parseDouble(newValue));
            else ps.setString(1, newValue);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Employee updated successfully!");
            else System.out.println("No employee found with ID " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Employee deleted successfully!");
            else System.out.println("No employee found with ID " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void markAttendance(int empId, String status) {
        if (!status.equalsIgnoreCase("Present") && !status.equalsIgnoreCase("Absent")) {
            System.out.println("Invalid status. Use Present or Absent.");
            return;
        }
        String checkSql  = "SELECT id FROM attendance WHERE employee_id = ? AND date = ?";
        String insertSql = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
        String today = LocalDate.now().toString();

        try (Connection conn = connect();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, empId);
            checkPs.setString(2, today);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                System.out.println("Attendance already marked for Employee " + empId + " today.");
                return;
            }
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, empId);
                insertPs.setString(2, today);
                insertPs.setString(3, capitalize(status));
                insertPs.executeUpdate();
                System.out.println("Attendance marked: Employee " + empId + " -> " + capitalize(status) + " on " + today);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewMonthlyAttendance(int empId, int month, int year) {
        String sql = "SELECT date, status FROM attendance " +
                     "WHERE employee_id = ? AND MONTH(date) = ? AND YEAR(date) = ? ORDER BY date";

        String countSql = "SELECT " +
                          "SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present_days, " +
                          "SUM(CASE WHEN status='Absent'  THEN 1 ELSE 0 END) AS absent_days " +
                          "FROM attendance WHERE employee_id = ? AND MONTH(date) = ? AND YEAR(date) = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nAttendance Report - Employee ID: " + empId + " | " + month + "/" + year);
            System.out.println("-".repeat(30));
            System.out.printf("%-15s %-10s%n", "Date", "Status");
            System.out.println("-".repeat(30));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-15s %-10s%n", rs.getString("date"), rs.getString("status"));
            }
            if (!found) System.out.println("No records found.");
            System.out.println("-".repeat(30));

            try (PreparedStatement ps2 = conn.prepareStatement(countSql)) {
                ps2.setInt(1, empId);
                ps2.setInt(2, month);
                ps2.setInt(3, year);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    System.out.println("Present: " + rs2.getInt("present_days") + " day(s)");
                    System.out.println("Absent : " + rs2.getInt("absent_days")  + " day(s)");
                }
            }
            System.out.println("-".repeat(30));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void rateEmployee(int empId, int rating, String remarks) {
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
        String sql = "INSERT INTO performance (employee_id, rating, remarks, review_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, rating);
            ps.setString(3, remarks);
            ps.setString(4, LocalDate.now().toString());
            ps.executeUpdate();
            System.out.println("Rating saved! Employee " + empId + " rated " + rating + "/5");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void filterByRating(int minRating) {
        if (minRating < 1 || minRating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
        String sql = "SELECT e.id, e.name, e.department, e.salary, e.email, " +
                     "ROUND(AVG(p.rating), 1) AS avg_rating " +
                     "FROM employees e " +
                     "JOIN performance p ON e.id = p.employee_id " +
                     "GROUP BY e.id, e.name, e.department, e.salary, e.email " +
                     "HAVING AVG(p.rating) >= ? " +
                     "ORDER BY avg_rating DESC";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minRating);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nEmployees with Average Rating >= " + minRating);
            System.out.println("-".repeat(85));
            System.out.printf("%-5s %-20s %-15s %-10s %-25s %-8s%n",
                    "ID", "Name", "Department", "Salary", "Email", "Rating");
            System.out.println("-".repeat(85));

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-20s %-15s %-10.2f %-25s %-8.1f%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("salary"),
                        rs.getString("email"),
                        rs.getDouble("avg_rating"));
            }
            if (!found) System.out.println("No employees found with rating >= " + minRating);
            System.out.println("-".repeat(85));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printEmployeeTable(ResultSet rs) throws SQLException {
        System.out.println("-".repeat(85));
        System.out.printf("%-5s %-20s %-15s %-12s %-25s%n",
                "ID", "Name", "Department", "Salary", "Email");
        System.out.println("-".repeat(85));
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.printf("%-5d %-20s %-15s %-12.2f %-25s%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getDouble("salary"),
                    rs.getString("email"));
        }
        if (!found) System.out.println("No employees found.");
        System.out.println("-".repeat(85));
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}