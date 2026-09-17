-- ============================================================
-- Reference schema for the Student Course & Grade Management System
-- This file is for DOCUMENTATION only — the running application
-- creates/updates these tables automatically via Hibernate
-- (spring.jpa.hibernate.ddl-auto=update). You do not need to run
-- this script manually, but it shows exactly what gets created.
-- ============================================================

CREATE DATABASE IF NOT EXISTS sms_db;
USE sms_db;

CREATE TABLE students (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number   VARCHAR(20)  NOT NULL UNIQUE,
    first_name            VARCHAR(60)  NOT NULL,
    last_name             VARCHAR(60)  NOT NULL,
    email                 VARCHAR(120) NOT NULL UNIQUE,
    date_of_birth         DATE,
    major                 VARCHAR(100),
    enrollment_date       DATE
);

CREATE TABLE courses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code   VARCHAR(20)  NOT NULL UNIQUE,
    title         VARCHAR(150) NOT NULL,
    credit_hours  INT          NOT NULL,
    department    VARCHAR(100),
    description   VARCHAR(500)
);

CREATE TABLE enrollments (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id   BIGINT      NOT NULL,
    course_id    BIGINT      NOT NULL,
    semester     VARCHAR(20) NOT NULL,
    grade        VARCHAR(20) NOT NULL DEFAULT 'NOT_GRADED',
    enrolled_on  DATE,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course  FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE,
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_id, semester)
);

-- Sample seed data (optional, for demoing the app / video recording)
INSERT INTO students (registration_number, first_name, last_name, email, major, enrollment_date) VALUES
('REG-2026-001', 'Amara', 'Nwosu', 'amara.nwosu@campus.edu', 'Computer Science', '2024-09-01'),
('REG-2026-002', 'Liam', 'Fernandez', 'liam.fernandez@campus.edu', 'Data Science', '2024-09-01'),
('REG-2026-003', 'Priya', 'Raman', 'priya.raman@campus.edu', 'Software Engineering', '2025-01-15');

INSERT INTO courses (course_code, title, credit_hours, department, description) VALUES
('CS-301', 'Database Systems', 3, 'Computer Science', 'Relational and NoSQL database design and implementation.'),
('CS-210', 'Data Structures & Algorithms', 4, 'Computer Science', 'Core algorithms and complexity analysis.'),
('CS-405', 'Software Architecture', 3, 'Computer Science', 'Backend architecture patterns and API design.');

INSERT INTO enrollments (student_id, course_id, semester, grade, enrolled_on) VALUES
(1, 1, 'Fall2026', 'A', '2026-09-01'),
(1, 2, 'Fall2026', 'B_PLUS', '2026-09-01'),
(2, 1, 'Fall2026', 'A_MINUS', '2026-09-01'),
(3, 3, 'Fall2026', 'NOT_GRADED', '2026-09-01');
