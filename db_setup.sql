CREATE DATABASE IF NOT EXISTS emptrack;
USE emptrack;

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100),
    salary DOUBLE,
    email VARCHAR(100)
);

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    date DATE,
    status ENUM('Present', 'Absent'),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE performance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    remarks VARCHAR(255),
    review_date DATE,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);