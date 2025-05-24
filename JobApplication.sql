CREATE DATABASE JOBAPPLICATION;
USE JOBAPPLICATION;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT chk_role CHECK (role IN ('applicant', 'hiring_manager'))
);

CREATE TABLE Applicant (
    applicant_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    full_name VARCHAR(100),
    age INT,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    qualification_level VARCHAR(20),
    specialization VARCHAR(100),
    photo VARCHAR(255),
    recommendation_letter VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_qualification CHECK (qualification_level IN ('High School', 'Bachelor', 'Master', 'PhD'))
);

CREATE TABLE AcademicCredential (
    credential_id INT AUTO_INCREMENT PRIMARY KEY,
    applicant_id INT NOT NULL,
    diploma_name VARCHAR(100),
    transcript VARCHAR(255),
    exam_score DECIMAL(4,2),
    institution VARCHAR(100),
    date_obtained DATE,
    FOREIGN KEY (applicant_id) REFERENCES Applicant(applicant_id)
);

CREATE TABLE Company (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    industry VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255)
);

CREATE TABLE HiringManager (
    manager_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    company_id INT,
    full_name VARCHAR(100),
    contact_email VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (company_id) REFERENCES Company(company_id)
);

CREATE TABLE JobPosting (
    job_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT,
    title VARCHAR(100),
    description TEXT,
    specialization_required VARCHAR(100),
    posting_date DATETIME,
    deadline DATETIME,
    status VARCHAR(20),
    FOREIGN KEY (company_id) REFERENCES Company(company_id),
    CONSTRAINT chk_status CHECK (status IN ('open', 'closed'))
);

CREATE TABLE PriorityCriterion (
    criterion_id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT,
    criterion_type VARCHAR(50),
    required_value VARCHAR(100),
    FOREIGN KEY (job_id) REFERENCES JobPosting(job_id),
    CONSTRAINT chk_criterion_type CHECK (criterion_type IN ('qualification', 'specialization', 'exam_score'))
);

CREATE TABLE Application (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    applicant_id INT,
    job_id INT,
    submission_date DATETIME,
    cover_letter TEXT,
    status VARCHAR(20),
    last_updated DATETIME,
    FOREIGN KEY (applicant_id) REFERENCES Applicant(applicant_id),
    FOREIGN KEY (job_id) REFERENCES JobPosting(job_id),
    CONSTRAINT chk_app_status CHECK (status IN ('applied', 'under_review', 'interview_scheduled', 'accepted', 'rejected'))
);

CREATE TABLE Interview (
    interview_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT,
    schedule_date DATETIME,
    interviewer VARCHAR(100),
    location VARCHAR(255),
    outcome VARCHAR(20),
    FOREIGN KEY (application_id) REFERENCES Application(application_id),
    CONSTRAINT chk_outcome CHECK (outcome IN ('pending', 'passed', 'failed'))
);

CREATE TABLE ScreeningResult (
    screening_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT,
    criterion_id INT,
    meets_criteria TINYINT(1),
    FOREIGN KEY (application_id) REFERENCES Application(application_id),
    FOREIGN KEY (criterion_id) REFERENCES PriorityCriterion(criterion_id)
);