# EmpTrack - Employee Management System

EmpTrack | Java, JDBC, MySQL


## Features

– Built a console-based Java application to manage employee records using JDBC database connectivity.
– Designed MySQL database schema to store employee details and perform CRUD operations.
– Enabled adding new employees and viewing detailed employee lists
- Admin login (username: admin, password: admin123)
- Add, view, update, delete employees
- Mark and view monthly attendance
- Rate employees and filter by rating

## How to Run

1. Install MySQL and create the database using db_setup.sql
2. Update your MySQL password in DBHelper.java
3. Compile:
   javac -cp ".;mysql-connector-j-9.6.0.jar" Main.java DBHelper.java Employee.java
4. Run:
   java -cp ".;mysql-connector-j-9.6.0.jar" Main
