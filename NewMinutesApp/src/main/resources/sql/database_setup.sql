-- Create the database
CREATE DATABASE IF NOT EXISTS minutesdb;
USE minutesdb;

-- Create attendees table
CREATE TABLE IF NOT EXISTS attendees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create minutes_reports table
CREATE TABLE IF NOT EXISTS minutes_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    meeting_type VARCHAR(100) NOT NULL,
    meeting_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    agenda TEXT,
    discussion TEXT,
    decisions TEXT,
    action_items TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create report_attendees junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS report_attendees (
    report_id INT NOT NULL,
    attendee_id INT NOT NULL,
    PRIMARY KEY (report_id, attendee_id),
    FOREIGN KEY (report_id) REFERENCES minutes_reports(id) ON DELETE CASCADE,
    FOREIGN KEY (attendee_id) REFERENCES attendees(id) ON DELETE CASCADE
);

-- Insert sample attendees
INSERT INTO attendees (name, email, department, role) VALUES
('John Smith', 'john.smith@company.com', 'Engineering', 'Senior Developer'),
('Sarah Johnson', 'sarah.johnson@company.com', 'Marketing', 'Marketing Manager'),
('Michael Brown', 'michael.brown@company.com', 'Finance', 'Financial Analyst'),
('Emily Davis', 'emily.davis@company.com', 'Human Resources', 'HR Director'),
('Robert Wilson', 'robert.wilson@company.com', 'Engineering', 'Project Manager'),
('Linda Martinez', 'linda.martinez@company.com', 'Sales', 'Sales Representative'),
('David Anderson', 'david.anderson@company.com', 'IT', 'IT Support Specialist'),
('Jennifer Taylor', 'jennifer.taylor@company.com', 'Product', 'Product Owner'),
('James Thomas', 'james.thomas@company.com', 'Engineering', 'QA Engineer'),
('Patricia Garcia', 'patricia.garcia@company.com', 'Legal', 'Legal Counsel');

-- Insert sample minutes report
INSERT INTO minutes_reports (title, meeting_type, meeting_date, start_time, end_time, location, agenda, discussion, decisions, action_items) VALUES
('Weekly Project Status Meeting', 'Project Review', '2025-04-01', '09:00:00', '10:30:00', 'Conference Room A',
 'Review progress on current sprint\nDiscuss blockers\nPlan for next sprint',
 'Team reported progress on all major tasks. API integration is 75% complete. Frontend UI is 80% complete.\nBlockers: Waiting for design team to finalize certain UI elements.',
 'Continue with current sprint plan\nSchedule meeting with design team for Thursday',
 'John to follow up with design team\nSarah to prepare demo for stakeholders\nMichael to update project timeline'),

('Quarterly Budget Review', 'Board Meeting', '2025-04-05', '14:00:00', '16:00:00', 'Executive Suite',
 'Q1 Financial Results\nBudget Adjustments for Q2\nForecast for remainder of the year',
 'Q1 results were 5% above projections. Marketing department requested additional budget for new campaign. IT department reported cost savings from cloud migration.',
 'Approved additional budget for marketing\nReallocated IT savings to R&D initiatives',
 'Emily to update departmental budgets\nRobert to prepare presentation for shareholders\nLinda to submit revised marketing plan');

-- Link attendees to reports
INSERT INTO report_attendees (report_id, attendee_id) VALUES
(1, 1), -- John Smith attended meeting 1
(1, 5), -- Robert Wilson attended meeting 1
(1, 8), -- Jennifer Taylor attended meeting 1
(1, 9), -- James Thomas attended meeting 1
(2, 3), -- Michael Brown attended meeting 2
(2, 4), -- Emily Davis attended meeting 2
(2, 6), -- Linda Martinez attended meeting 2
(2, 10); -- Patricia Garcia attended meeting 2