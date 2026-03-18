import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Welcome to EmpTrack ===");

        if (!adminLogin(sc)) {
            System.out.println("Too many failed attempts. Exiting.");
            sc.close();
            return;
        }

        while (true) {
            printMenu();
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Department: ");
                    String dept = sc.nextLine();
                    System.out.print("Enter Salary: ");
                    double salary = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    DBHelper.addEmployee(name, dept, salary, email);
                    break;

                case 2:
                    DBHelper.viewAllEmployees();
                    break;

                case 3:
                    System.out.print("Enter Employee ID to update: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();
                    System.out.println("What to update? 1.Name  2.Department  3.Salary  4.Email");
                    System.out.print("Choose field: ");
                    int field = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter new value: ");
                    String newVal = sc.nextLine();
                    DBHelper.updateEmployee(updateId, field, newVal);
                    break;

                case 4:
                    System.out.print("Enter Employee ID to delete: ");
                    int delId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Are you sure? (yes/no): ");
                    String confirm = sc.nextLine();
                    if (confirm.equalsIgnoreCase("yes")) DBHelper.deleteEmployee(delId);
                    else System.out.println("Deletion cancelled.");
                    break;

                case 5:
                    System.out.print("Enter Employee ID: ");
                    int attEmpId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Status (Present / Absent): ");
                    String status = sc.nextLine();
                    DBHelper.markAttendance(attEmpId, status);
                    break;

                case 6:
                    System.out.print("Enter Employee ID: ");
                    int viewEmpId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Month (1-12): ");
                    int month = sc.nextInt();
                    System.out.print("Enter Year (e.g. 2025): ");
                    int year = sc.nextInt();
                    sc.nextLine();
                    DBHelper.viewMonthlyAttendance(viewEmpId, month, year);
                    break;

                case 7:
                    System.out.print("Enter Employee ID: ");
                    int rateId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Rating (1-5): ");
                    int rating = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Remarks: ");
                    String remarks = sc.nextLine();
                    DBHelper.rateEmployee(rateId, rating, remarks);
                    break;

                case 8:
                    System.out.print("Filter by minimum rating (1-5): ");
                    int minRating = sc.nextInt();
                    sc.nextLine();
                    DBHelper.filterByRating(minRating);
                    break;

                case 9:
                    System.out.println("Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid option. Try 1-9.");
            }
        }
    }

    private static boolean adminLogin(Scanner sc) {
        final String ADMIN_USER = "admin";
        final String ADMIN_PASS = "admin123";
        int attempts = 0;

        System.out.println("--- Admin Login ---");

        while (attempts < 3) {
            System.out.print("Username: ");
            String user = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
                System.out.println("Login successful! Welcome, Admin.");
                return true;
            } else {
                attempts++;
                int left = 3 - attempts;
                System.out.println("Wrong credentials. " + (left > 0 ? left + " attempt(s) left." : ""));
            }
        }
        return false;
    }

    private static void printMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Add New Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Update Employee");
        System.out.println("4. Delete Employee");
        System.out.println("5. Mark Attendance");
        System.out.println("6. View Monthly Attendance");
        System.out.println("7. Rate Employee");
        System.out.println("8. Filter by Rating");
        System.out.println("9. Exit");
        System.out.println("=================");
    }
}