import java.sql.*;
import java.util.Scanner;

public class IDEndTermProject {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/idproject1";

    static final String USER = "root";
    static final String PASS = "**********"; 

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully...");

            stmt = conn.createStatement();

            // Menu-driven program
            int choice;
            Scanner scanner = new Scanner(System.in);
            do {
                System.out.println("\n***** Banking Management System *****");
                System.out.println("1. Show Customer Records");
                System.out.println("2. Add Customer Record");
                System.out.println("3. Delete Customer Record");
                System.out.println("4. Update Customer Information");
                System.out.println("5. Show Account Details of a Customer");
                System.out.println("6. Show Loan Details of a Customer");
                System.out.println("7. Deposit Money to an Account");
                System.out.println("8. Withdraw Money from an Account");
                System.out.println("9. Exit");

                System.out.print("Enter your choice (1-9): ");
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        showCustomerRecords(stmt);
                        break;
                    case 2:
                        addCustomerRecord(stmt, scanner);
                        break;
                    case 3:
                        deleteCustomerRecord(stmt, scanner);
                        break;
                    case 4:
                        updateCustomerInformation(stmt, scanner);
                        break;
                    case 5:
                        showAccountDetails(stmt, scanner);
                        break;
                    case 6:
                        showLoanDetails(stmt, scanner);
                        break;
                    case 7:
                        depositMoney(stmt, scanner);
                        break;
                    case 8:
                        withdrawMoney(stmt, scanner);
                        break;
                    case 9:
                        System.out.println("Exiting the program...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                }
            } while (choice != 9);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private static void showCustomerRecords(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM CUSTOMERS");

        System.out.println("Customer Records:");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-15s | %-10s\n", "Customer No", "Name", "Phone", "City");
        System.out.println("-----------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-10s | %-20s | %-15s | %-10s\n",
                    rs.getString("CUST_NO"),
                    rs.getString("NAME"),
                    rs.getString("PHONE"),
                    rs.getString("CITY")); 
        }
        System.out.println("-----------------------------------------------------------------");
        rs.close();
    }

    private static void addCustomerRecord(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer Number: ");
        String custNo = scanner.nextLine();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter City: ");
        String city = scanner.nextLine();

        String sql = "INSERT INTO CUSTOMERS (CUST_NO, NAME, PHONE, CITY) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql);
        pstmt.setString(1, custNo);
        pstmt.setString(2, name);
        pstmt.setString(3, phone);
        pstmt.setString(4, city);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Customer added successfully.");
        } else {
            System.out.println("Failed to add customer.");
        }
        pstmt.close();
    }

    private static void deleteCustomerRecord(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer Number to delete: ");
        String custNo = scanner.nextLine();

        String sql = "DELETE FROM CUSTOMERS WHERE CUST_NO = ?";
        PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql);
        pstmt.setString(1, custNo);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Customer deleted successfully.");
        } else {
            System.out.println("Customer not found or deletion failed.");
        }
        pstmt.close();
    }

    private static void updateCustomerInformation(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer Number to update: ");
        String custNo = scanner.nextLine();
        System.out.println("Choose attribute to update:");
        System.out.println("1. Name");
        System.out.println("2. Phone Number");
        System.out.println("3. City");
        System.out.print("Enter your choice (1-3): ");
        int attributeChoice = Integer.parseInt(scanner.nextLine());

        String attributeToUpdate = null;
        switch (attributeChoice) {
            case 1:
                attributeToUpdate = "NAME";
                break;
            case 2:
                attributeToUpdate = "PHONE";
                break;
            case 3:
                attributeToUpdate = "CITY";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.print("Enter new value for " + attributeToUpdate + ": ");
        String newValue = scanner.nextLine();

        String sql = "UPDATE CUSTOMERS SET " + attributeToUpdate + " = ? WHERE CUST_NO = ?";
        PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql);
        pstmt.setString(1, newValue);
        pstmt.setString(2, custNo);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Customer information updated successfully.");
        } else {
            System.out.println("Customer not found or update failed.");
        }
        pstmt.close();
    }

    private static void showAccountDetails(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer Number: ");
        String custNo = scanner.nextLine();

        String sql = "SELECT * FROM ACCOUNTS WHERE CUST_NO = ?";
        PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql);
        pstmt.setString(1, custNo);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Account Number | Type | Balance | Branch Code | Branch Name | Branch City");
        System.out.println("---------------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-15s | %-4s | %-8s | %-11s | %-12s | %-11s%n",
                    rs.getString("ACCOUNT_NO"),
                    rs.getString("TYPE"),
                    rs.getString("BALANCE"),
                    rs.getString("BRANCH_CODE"),
                    rs.getString("BRANCH_NAME"),
                    rs.getString("BRANCH_CITY"));
        }
        System.out.println("---------------------------------------------------------------------------------");

        rs.close();
        pstmt.close();
    }


    private static void showLoanDetails(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Customer Number: ");
        String custNo = scanner.nextLine();

        String sql = "SELECT * FROM LOANS WHERE CUST_NO = ?";
        PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql);
        pstmt.setString(1, custNo);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Loan Number | Loan Amount | Branch Code |     Branch Name     | Branch City");
        System.out.println("----------------------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-11s | %-11s | %-11s | %-12s | %-11s%n",
                    rs.getString("LOAN_NO"),
                    rs.getString("LOAN_AMOUNT"),
                    rs.getString("BRANCH_CODE"),
                    rs.getString("BRANCH_NAME"),
                    rs.getString("BRANCH_CITY"));
        }
        System.out.println("----------------------------------------------------------------------------");

        rs.close();
        pstmt.close();
    }


    private static void depositMoney(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Account Number: ");
        String accountNo = scanner.nextLine();
        System.out.print("Enter Amount to Deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());

        String sql = "UPDATE ACCOUNTS SET BALANCE = BALANCE + ? WHERE ACCOUNT_NO = ?";
        PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql);
        pstmt.setDouble(1, amount);
        pstmt.setString(2, accountNo);
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Money deposited successfully.");
        } else {
            System.out.println("Failed to deposit money.");
        }
        pstmt.close();
    }

    private static void withdrawMoney(Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter Account Number: ");
        String accountNo = scanner.nextLine();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());

        String sql = "SELECT BALANCE FROM ACCOUNTS WHERE ACCOUNT_NO = ?";
        PreparedStatement balanceStmt = stmt.getConnection().prepareStatement(sql);
        balanceStmt.setString(1, accountNo);
        ResultSet rs = balanceStmt.executeQuery();
        if (rs.next()) {
            double currentBalance = rs.getDouble("BALANCE");
            if (currentBalance >= amount) {
                sql = "UPDATE ACCOUNTS SET BALANCE = BALANCE - ? WHERE ACCOUNT_NO = ?";
                PreparedStatement withdrawStmt = stmt.getConnection().prepareStatement(sql);
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, accountNo);
                int rowsAffected = withdrawStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Money withdrawn successfully.");
                } else {
                    System.out.println("Failed to withdraw money.");
                }
                withdrawStmt.close();
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Account not found.");
        }
        rs.close();
        balanceStmt.close();
    }
}
