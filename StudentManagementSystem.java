import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class StudentManagementSystem 
{

    // Database connection details

    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe"; // Replace with your database URL

    private static final String USERNAME = "scott";       // Replace with your database username

    private static final String PASSWORD = "tiger";     // Replace with your database password


    public static void main(String[] args) 
     {
        // Declare the Scanner outside the try block

        Scanner scanner = new Scanner(System.in);
        Connection connection = null; // Declare connection outside the try block for broader scope

        try 
          {
            // Load the Oracle JDBC driver.  This is necessary for the code to work.
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish a database connection
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database.");

            // Main loop to display the menu and handle user input
            while (true) 
              {
                displayMenu();
                int choice = scanner.nextInt(); // Get the user's menu choice
                scanner.nextLine(); // Consume the newline character after reading the integer.  Important!

                switch (choice) 
                  {
                    case 1:
                        // Insert a new student record
                        insertStudent(connection, scanner);
                        break;
                    case 2:
                        // Read student records
                        selectStudents(connection);
                        break;
                    case 3:
                        // Update an existing student record
                        updateStudent(connection, scanner);
                        break;
                    case 4:
                        // Delete a student record
                        deleteStudent(connection);
                        break;
                    case 5:
                        // Exit the program
                        System.out.println("Exiting the program.");
                        return;
                    default:
                        // Handle invalid menu choices
                        System.out.println("Invalid choice. Please try again.");
                 }
             }
        } 
     catch (ClassNotFoundException e) 
         {
            // Handle the case where the JDBC driver is not found
            System.err.println("Error: Oracle JDBC driver not found.  Make sure you have the correct driver JAR file in your classpath.");
            e.printStackTrace(); // Print the stack trace to help with debugging
         } 
           catch (SQLException e) 
              {
           	 // Handle database errors
           	 System.err.println("Error: An error occurred while accessing the database.");
           	 e.printStackTrace(); // Print the stack trace to help with debugging
              } 
              finally 
                  {
           	      // Close the database connection in a finally block to ensure it's always closed
                     if (connection != null) 
                         {
                              try 
                               {
                    		      connection.close();
                    		      System.out.println("Disconnected from the database."); //Inform
                               } 
                            catch (SQLException e) 
                               {
                  		     System.err.println("Error: Could not close the database connection.");
                                     e.printStackTrace();
               		       }
                       }
                   scanner.close(); // Close the scanner to prevent resource leaks
             }
    }

    // Method to display the main menu
    private static void displayMenu() 
   {
        System.out.println("\n\t\t\t\t Operation Menu");
        System.out.println("\t\t\t1 -> Create (Insert) Operation");
        System.out.println("\t\t\t2 -> Read (Select) Operation");
        System.out.println("\t\t\t3 -> Update Operation");
        System.out.println("\t\t\t4 -> Delete Operation");
        System.out.println("\t\t\t5 -> Exit");
        System.out.print("\t\tSelect Operation (1 to 5) = ");
    }

    // Method to insert a new student record
    private static void insertStudent(Connection connection, Scanner scanner) throws SQLException 
   {
        System.out.println("\n\tInsert Operation");
        System.out.print("\tEnter Student RollNo: ");
        int rollno = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("\tEnter Student Name: ");
        String name = scanner.nextLine();
        System.out.print("\tEnter Student Email ID: ");
        String email = scanner.nextLine();
        System.out.print("\tEnter Student Marks: ");
        double marks = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("\tEnter Student Location: ");
        String location = scanner.nextLine();

        // Use a PreparedStatement to prevent SQL injection
        String insertSql = "INSERT INTO student (rollno, name, email, marks, location) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) 
         {
            preparedStatement.setInt(1, rollno);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setDouble(4, marks);
            preparedStatement.setString(5, location);
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println(rowsInserted + " row(s) inserted.");
         }  
       catch (SQLException e) 
         {
            System.err.println("Error inserting record: " + e.getMessage()); //More specific error
            throw e; // Re-throw the exception to be handled by the calling method
         }
    }

    // Method to select student records
    private static void selectStudents(Connection connection) throws SQLException 
     {
        Scanner scanner = new Scanner(System.in); //create new scanner
        while (true) 
         {
            System.out.println("\n\t\t\t===Select Menu for Read======");
            System.out.println("\t\t1 -> Select All Students");
            System.out.println("\t\t2 -> Select by Roll No");
            System.out.println("\t\t3 -> Select by Name");
            System.out.println("\t\t4 -> Select by Email");
            System.out.println("\t\t5 -> Select by Marks");
            System.out.println("\t\t6 -> Select by Location");
            System.out.println("\t\t7 -> Exit");
            System.out.print("\t\tSelect Operation: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) 
             {
                case 1:
                    selectAllStudents(connection);
                    break;
                case 2:
                    selectStudentByRollNo(connection, scanner);
                    break;
                case 3:
                    selectStudentByName(connection, scanner);
                    break;
                case 4:
                    selectStudentByEmail(connection, scanner);
                    break;
                case 5:
                    selectStudentByMarks(connection, scanner);
                    break;
                case 6:
                    selectStudentByLocation(connection, scanner);
                    break;
                case 7:
                    return; // Exit the select menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void selectAllStudents(Connection connection) throws SQLException 
   {
        String selectAllSql = "SELECT rollno, name, email, marks, location FROM student";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectAllSql);
             ResultSet resultSet = preparedStatement.executeQuery()) 
        { //Use try-with-resources
            System.out.println("\nAll Student Records:");
            while (resultSet.next()) 
            {
                displayStudentDetails(resultSet);
            }
        }
    }

    // Method to select a student record by Roll No
    private static void selectStudentByRollNo(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Roll No: ");
        int rollno = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        String selectSql = "SELECT rollno, name, email, marks, location FROM student WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            preparedStatement.setInt(1, rollno);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                displayStudentDetails(resultSet);
            } else {
                System.out.println("No student found with Roll No " + rollno);
            }
        }
    }

    // Method to select a student record by Name
    private static void selectStudentByName(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Name: ");
        String name = scanner.nextLine();

        String selectSql = "SELECT rollno, name, email, marks, location FROM student WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                displayStudentDetails(resultSet);
            } else {
                System.out.println("No student found with Name " + name);
            }
        }
    }

    // Method to select a student record by Email
    private static void selectStudentByEmail(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Email: ");
        String email = scanner.nextLine();

        String selectSql = "SELECT rollno, name, email, marks, location FROM student WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                displayStudentDetails(resultSet);
            } else {
                System.out.println("No student found with Email " + email);
            }
        }
    }

    // Method to select a student record by Marks
    private static void selectStudentByMarks(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Marks: ");
        double marks = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        String selectSql = "SELECT rollno, name, email, marks, location FROM student WHERE marks = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            preparedStatement.setDouble(1, marks);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                displayStudentDetails(resultSet);
            } else {
                System.out.println("No student found with Marks " + marks);
            }
        }
    }

    // Method to select a student record by Location
    private static void selectStudentByLocation(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Location: ");
        String location = scanner.nextLine();

        String selectSql = "SELECT rollno, name, email, marks, location FROM student WHERE location = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
            preparedStatement.setString(1, location);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("\nStudent Details:");
                displayStudentDetails(resultSet);
            } else {
                System.out.println("No student found with Location " + location);
            }
        }
    }

    // Helper method to display student details from a ResultSet
    private static void displayStudentDetails(ResultSet resultSet) throws SQLException {
        int rollno = resultSet.getInt("rollno");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        double marks = resultSet.getDouble("marks");
        String location = resultSet.getString("location");
        System.out.println("Roll No: " + rollno + ", Name: " + name + ", Email: " + email + ", Marks: " + marks + ", Location: " + location);
    }

    // Method to update an existing student record
    private static void updateStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Roll No of the student to update: ");
        int rollnoToUpdate = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Check if the student exists before attempting to update
        if (!isStudentExists(connection, rollnoToUpdate)) {
            System.out.println("Student with Roll No " + rollnoToUpdate + " not found.");
            return;
        }

        while (true) {
            System.out.println("\n\t\t\t===Update Menu======");
            System.out.println("\t\t1 -> Update All Fields");
            System.out.println("\t\t2 -> Update Name");
            System.out.println("\t\t3 -> Update Email");
            System.out.println("\t\t4 -> Update Marks");
            System.out.println("\t\t5 -> Update Location");
            System.out.println("\t\t6 -> Exit Update Menu");
            System.out.print("\t\tSelect Field to Update: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    updateAllFields(connection, scanner, rollnoToUpdate);
                    return; // Exit the update menu after updating
                case 2:
                    updateName(connection, scanner, rollnoToUpdate);
                    break;
                case 3:
                    updateEmail(connection, scanner, rollnoToUpdate);
                    break;
                case 4:
                    updateMarks(connection, scanner, rollnoToUpdate);
                    break;
                case 5:
                    updateLocation(connection, scanner, rollnoToUpdate);
                    break;
                case 6:
                    return; // Exit the update menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Helper method to check if a student exists
    private static boolean isStudentExists(Connection connection, int rollno) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM student WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkSql)) {
            preparedStatement.setInt(1, rollno);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }

    private static void updateAllFields(Connection connection, Scanner scanner, int rollnoToUpdate) throws SQLException {
        System.out.print("\tEnter New Student Name: ");
        String newName = scanner.nextLine();
        System.out.print("\tEnter New Student Email ID: ");
        String newEmail = scanner.nextLine();
        System.out.print("\tEnter New Student Marks: ");
        double newMarks = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("\tEnter New Student Location: ");
        String newLocation = scanner.nextLine();

        String updateSql = "UPDATE student SET name = ?, email = ?, marks = ?, location = ? WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newEmail);
            preparedStatement.setDouble(3, newMarks);
            preparedStatement.setString(4, newLocation);
            preparedStatement.setInt(5, rollnoToUpdate);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        }
    }

    private static void updateName(Connection connection, Scanner scanner, int rollnoToUpdate) throws SQLException {
        System.out.print("\tEnter New Student Name: ");
        String newName = scanner.nextLine();
        String updateSql = "UPDATE student SET name = ? WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, rollnoToUpdate);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        }
    }

    private static void updateEmail(Connection connection, Scanner scanner, int rollnoToUpdate) throws SQLException {
        System.out.print("\tEnter New Student Email: ");
        String newEmail = scanner.nextLine();
        String updateSql = "UPDATE student SET email = ? WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, newEmail);
            preparedStatement.setInt(2, rollnoToUpdate);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        }
    }

    private static void updateMarks(Connection connection, Scanner scanner, int rollnoToUpdate) throws SQLException {
        System.out.print("\tEnter New Student Marks: ");
        double newMarks = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        String updateSql = "UPDATE student SET marks = ? WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setDouble(1, newMarks);
            preparedStatement.setInt(2, rollnoToUpdate);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        }
    }

    private static void updateLocation(Connection connection, Scanner scanner, int rollnoToUpdate) throws SQLException {
        System.out.print("\tEnter New Student Location: ");
        String newLocation = scanner.nextLine();
        String updateSql = "UPDATE student SET location = ? WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, newLocation);
            preparedStatement.setInt(2, rollnoToUpdate);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " row(s) updated.");
        }
    }

    // Method to delete a student record
    private static void deleteStudent(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in); //Local scanner
        while (true) {
            System.out.println("\n\t\t\t===Delete Menu======");
            System.out.println("\t\t1 -> Delete by Roll No");
            System.out.println("\t\t2 -> Delete by Name");
            System.out.println("\t\t3 -> Delete by Email");
            System.out.println("\t\t4 -> Delete by Marks");
            System.out.println("\t\t5 -> Delete by Location");
            System.out.println("\t\t6 -> Exit Delete Menu");
            System.out.print("\t\tSelect Criteria to Delete: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    deleteStudentByRollNo(connection, scanner);
                    return; // Exit delete menu
                case 2:
                    deleteStudentByName(connection, scanner);
                    return;
                case 3:
                    deleteStudentByEmail(connection, scanner);
                    return;
                case 4:
                    deleteStudentByMarks(connection, scanner);
                    return;
                case 5:
                    deleteStudentByLocation(connection, scanner);
                    return;
                case 6:
                    return; // Exit the delete menu
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void deleteStudentByRollNo(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter Roll No of the student to delete: ");
        int rollno = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        String deleteSql = "DELETE FROM student WHERE rollno = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.setInt(1, rollno);
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
        }  catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteStudentByName(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("\nEnter the name of the student to delete: ");
        String name = scanner.nextLine();
        String deleteSql = "DELETE FROM student WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.setString(1, name);
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteStudentByEmail(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the email of the student to delete: ");
        String email = scanner.nextLine();
        String deleteSql = "DELETE FROM student WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.setString(1, email);
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
        }  catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteStudentByMarks(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the marks of the student to delete: ");
        double marks = scanner.nextDouble();
        scanner.nextLine();
        String deleteSql = "DELETE FROM student WHERE marks = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.setDouble(1, marks);
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            throw e;
        }
    }

    private static void deleteStudentByLocation(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the location of the student to delete: ");
        String location = scanner.nextLine();
        String deleteSql = "DELETE FROM student WHERE location = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.setString(1, location);
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println(rowsDeleted + " row(s) deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            throw e;
        }
    }
}

