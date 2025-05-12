# STUDENT-INFORMATION-WEB

  This is a Java console application that connects to an Oracle database and provides a full-featured CRUD (Create, Read, Update, Delete) interface for managing student records.

# Features
  Add new student records with roll number, name, email, marks, and location
  Read student data:
  View all students
  Search by roll number, name, email, marks, or location
  Update student records selectively or fully
  Delete students based on roll number, name, email, marks, or location
  Uses JDBC with Oracle DB and prepared statements for secure database operations
  Structured and modular code for maintainability

# Technologies Used
   Java (JDK 8+)
   Oracle Database
   JDBC (Oracle Driver)
   Command-line interface

# Setup Instructions
   Ensure Oracle DB is running and accessible.
   Update your DB credentials in the following fields in the StudentManagementSystem.java file:
                   private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";                
                   private static final String USERNAME = "Username"; 
                   private static final String PASSWORD = "Password";
                   
# Compile and run the Java program:
       javac StudentManagementSystem.java
       java StudentManagementSystem
       
# Table Structure
Make sure your Oracle DB has a table named student with the following structure:
          CREATE TABLE student (rollno INT PRIMARY KEY,
                                name VARCHAR2(50),
                                email VARCHAR2(100),
                                marks NUMBER,
                                location VARCHAR2(100)
                               );
