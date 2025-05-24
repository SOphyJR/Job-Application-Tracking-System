package jobTrack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ManagerDashboard extends JFrame {
    private int userId;
    private int managerId;
    private int companyId;
    private String fullName;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTable jobsTable;
    private DefaultTableModel jobsTableModel;
    private JTable applicationsTable;
    private DefaultTableModel applicationsTableModel;
    private DefaultTableModel interviewsTableModel;

    // Add color constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Blue
    private static final Color SECONDARY_COLOR = new Color(46, 204, 113); // Green
    private static final Color ACCENT_COLOR = new Color(231, 76, 60); // Red
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private static final Color TEXT_COLOR = new Color(44, 62, 80); // Dark Blue

    public ManagerDashboard(int userId) {
        this.userId = userId;
        loadManagerData();
        initializeUI();
    }

    private void loadManagerData() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT m.manager_id, m.company_id, m.full_name FROM HiringManager m WHERE m.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.managerId = rs.getInt("manager_id");
                this.companyId = rs.getInt("company_id");
                this.fullName = rs.getString("full_name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading manager data: " + ex.getMessage());
        }
    }

    private void initializeUI() {
        setTitle("Manager Dashboard - " + fullName);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Main panel with CardLayout
        mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Create different panels
        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createPostJobPanel(), "POST_JOB");
        mainPanel.add(createApplicationsPanel(), "APPLICATIONS");
        mainPanel.add(createScreeningPanel(), "SCREENING");
        mainPanel.add(createInterviewsPanel(), "INTERVIEWS");

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setBackground(PRIMARY_COLOR);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Style buttons
        JButton homeBtn = createStyledButton("Home");
        JButton postJobBtn = createStyledButton("Post New Job");
        JButton viewAppsBtn = createStyledButton("View Applications");
        JButton screeningBtn = createStyledButton("Screening Criteria");
        JButton interviewsBtn = createStyledButton("Manage Interviews");
        JButton logoutBtn = createStyledButton("Logout");

        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        postJobBtn.addActionListener(e -> cardLayout.show(mainPanel, "POST_JOB"));
        viewAppsBtn.addActionListener(e -> {
            refreshApplicationsTable();
            cardLayout.show(mainPanel, "APPLICATIONS");
        });
        screeningBtn.addActionListener(e -> cardLayout.show(mainPanel, "SCREENING"));
        interviewsBtn.addActionListener(e -> {
            refreshInterviewsTable();
            cardLayout.show(mainPanel, "INTERVIEWS");
        });
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginRegisterFrame().setVisible(true);
        });

        navPanel.add(homeBtn);
        navPanel.add(postJobBtn);
        navPanel.add(viewAppsBtn);
        navPanel.add(screeningBtn);
        navPanel.add(interviewsBtn);
        navPanel.add(logoutBtn);

        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }
        });

        return button;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Welcome message with system name
        JLabel systemLabel = new JLabel("Job Application System");
        systemLabel.setFont(new Font("Arial", Font.BOLD, 28));
        systemLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(systemLabel, gbc);

        // Welcome message with manager's name
        JLabel welcomeLabel = new JLabel("Welcome, " + fullName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(SECONDARY_COLOR);
        gbc.gridy = 1;
        panel.add(welcomeLabel, gbc);

        // Dashboard description
        JTextArea description = new JTextArea(
            "" +
            "• Post new job openings and manage existing ones\n" +
            "• Review and process job applications\n" +
            "• Track application status and make hiring decisions\n" +
            "• Set screening criteria for job positions\n\n" +
            "Use the navigation buttons above to access different features."
        );
        description.setEditable(false);
        description.setBackground(BACKGROUND_COLOR);
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(TEXT_COLOR);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        panel.add(description, gbc);

        return panel;
    }

    private JPanel createPostJobPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Style labels
        JLabel titleLabel = new JLabel("Job Title:");
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Job details fields
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        gbc.gridx = 1;
        JTextField titleField = new JTextField(20);
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(5, 20);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Specialization Required:"), gbc);
        gbc.gridx = 1;
        JTextField specField = new JTextField(20);
        panel.add(specField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Deadline:"), gbc);
        gbc.gridx = 1;
        JTextField deadlineField = new JTextField(20);
        panel.add(deadlineField, gbc);

        // Post button
        gbc.gridx = 1; gbc.gridy = 4;
        JButton postButton = createStyledButton("Post Job");
        panel.add(postButton, gbc);

        // Post button action
        postButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descArea.getText().trim();
            String specialization = specField.getText().trim();
            String deadline = deadlineField.getText().trim();

            if (title.isEmpty() || description.isEmpty() || specialization.isEmpty() || deadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO JobPosting (company_id, title, description, specialization_required, " +
                        "posting_date, deadline, status) VALUES (?, ?, ?, ?, GETDATE(), ?, 'open')";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, companyId);
                stmt.setString(2, title);
                stmt.setString(3, description);
                stmt.setString(4, specialization);
                stmt.setString(5, deadline);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Job posted successfully!");
                titleField.setText("");
                descArea.setText("");
                specField.setText("");
                deadlineField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error posting job: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        String[] columns = {"Application ID", "Job Title", "Applicant Name", "Submission Date", "Status", "Actions"};
        applicationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };
        applicationsTable = new JTable(applicationsTableModel);
        JScrollPane scrollPane = new JScrollPane(applicationsTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        refreshApplicationsTable();

        return panel;
    }

    private void refreshApplicationsTable() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT a.application_id, j.title, ap.full_name, " +
                    "a.submission_date, a.status " +
                    "FROM Application a " +
                    "JOIN JobPosting j ON a.job_id = j.job_id " +
                    "JOIN Applicant ap ON a.applicant_id = ap.applicant_id " +
                    "WHERE j.company_id = ? " +
                    "ORDER BY a.submission_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            applicationsTableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("application_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("full_name"));
                row.add(sdf.format(rs.getTimestamp("submission_date")));
                row.add(rs.getString("status"));

                // Add action buttons
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton reviewBtn = new JButton("Review");
                JButton scheduleBtn = new JButton("Schedule Interview");
                JButton updateBtn = new JButton("Update Status");

                int applicationId = rs.getInt("application_id");
                reviewBtn.addActionListener(e -> reviewApplication(applicationId));
                scheduleBtn.addActionListener(e -> scheduleInterview(applicationId));
                updateBtn.addActionListener(e -> updateApplicationStatus(applicationId));

                actionPanel.add(reviewBtn);
                actionPanel.add(scheduleBtn);
                actionPanel.add(updateBtn);

                row.add(actionPanel);
                applicationsTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading applications: " + ex.getMessage());
        }
    }

    private void reviewApplication(int applicationId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT a.cover_letter, ap.full_name, j.title " +
                    "FROM Application a " +
                    "JOIN Applicant ap ON a.applicant_id = ap.applicant_id " +
                    "JOIN JobPosting j ON a.job_id = j.job_id " +
                    "WHERE a.application_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String message = "Applicant: " + rs.getString("full_name") + "\n" +
                        "Position: " + rs.getString("title") + "\n\n" +
                        "Cover Letter:\n" + rs.getString("cover_letter");

                JOptionPane.showMessageDialog(this, message, "Application Review",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reviewing application: " + ex.getMessage());
        }
    }

    private void scheduleInterview(int applicationId) {
        JTextField dateField = new JTextField(20);
        JTextField timeField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField interviewerField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:mm):"));
        panel.add(timeField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Interviewer:"));
        panel.add(interviewerField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Schedule Interview", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO Interview (application_id, schedule_date, interviewer, location, outcome) " +
                        "VALUES (?, ?, ?, ?, 'pending')";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, applicationId);
                stmt.setString(2, dateField.getText() + " " + timeField.getText());
                stmt.setString(3, interviewerField.getText());
                stmt.setString(4, locationField.getText());
                stmt.executeUpdate();

                sql = "UPDATE Application SET status = 'interview_scheduled', last_updated = GETDATE() " +
                        "WHERE application_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, applicationId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Interview scheduled successfully!");
                refreshApplicationsTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error scheduling interview: " + ex.getMessage());
            }
        }
    }

    private void updateApplicationStatus(int applicationId) {
        String[] statuses = {"under_review", "interview_scheduled", "accepted", "rejected"};
        String status = (String) JOptionPane.showInputDialog(this,
                "Select new status:",
                "Update Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                statuses[0]);

        if (status != null) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "UPDATE Application SET status = ?, last_updated = GETDATE() " +
                        "WHERE application_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, status);
                stmt.setInt(2, applicationId);
                stmt.executeUpdate();
                refreshApplicationsTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage());
            }
        }
    }

    private JPanel createScreeningPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Style labels
        JLabel jobLabel = new JLabel("Select Job:");
        jobLabel.setForeground(PRIMARY_COLOR);
        jobLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Job selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(jobLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> jobComboBox = new JComboBox<>();
        panel.add(jobComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Criteria Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> criteriaType = new JComboBox<>(new String[]{"qualification", "specialization", "exam_score"});
        panel.add(criteriaType, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Required Value:"), gbc);
        gbc.gridx = 1;
        JTextField valueField = new JTextField(20);
        panel.add(valueField, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        JButton addButton = createStyledButton("Add Criteria");
        panel.add(addButton, gbc);
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT job_id, title FROM JobPosting WHERE company_id = ? AND status = 'open'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                jobComboBox.addItem(rs.getString("title") + " (ID: " + rs.getInt("job_id") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        addButton.addActionListener(e -> {
            String selectedJob = (String) jobComboBox.getSelectedItem();
            if (selectedJob == null) {
                JOptionPane.showMessageDialog(this, "Please select a job.");
                return;
            }
            String type = (String) criteriaType.getSelectedItem();
            String value = valueField.getText().trim();

            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a required value.");
                return;
            }
            int jobId = Integer.parseInt(selectedJob.substring(selectedJob.lastIndexOf("ID: ") + 4, selectedJob.length() - 1));

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO PriorityCriterion (job_id, criterion_type, required_value) " +
                        "VALUES (?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, jobId);
                stmt.setString(2, type);
                stmt.setString(3, value);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Screening criteria added successfully!");
                valueField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding criteria: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createInterviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Add top panel with "Schedule New Interview" button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BACKGROUND_COLOR);
        JButton newInterviewBtn = createStyledButton("Schedule New Interview");
        newInterviewBtn.addActionListener(e -> scheduleNewInterview());
        topPanel.add(newInterviewBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Interview ID", "Job Title", "Applicant Name", "Date & Time", "Location", "Interviewer", "Status", "Actions"};
        interviewsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };
        JTable interviewsTable = new JTable(interviewsTableModel);
        JScrollPane scrollPane = new JScrollPane(interviewsTable);

        // Style table
        interviewsTable.setBackground(Color.WHITE);
        interviewsTable.setForeground(TEXT_COLOR);
        interviewsTable.setGridColor(PRIMARY_COLOR);
        interviewsTable.setSelectionBackground(PRIMARY_COLOR);
        interviewsTable.setSelectionForeground(Color.WHITE);
        interviewsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Style table header
        interviewsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        interviewsTable.getTableHeader().setForeground(Color.WHITE);
        interviewsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshInterviewsTable();

        return panel;
    }

    private void refreshInterviewsTable() {
        if (interviewsTableModel == null) {
            return;
        }
        
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT i.interview_id, j.title, ap.full_name, " +
                    "i.schedule_date, i.location, i.interviewer, i.outcome " +
                    "FROM Interview i " +
                    "JOIN Application a ON i.application_id = a.application_id " +
                    "JOIN JobPosting j ON a.job_id = j.job_id " +
                    "JOIN Applicant ap ON a.applicant_id = ap.applicant_id " +
                    "WHERE j.company_id = ? " +
                    "ORDER BY i.schedule_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            interviewsTableModel.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("interview_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("full_name"));
                row.add(sdf.format(rs.getTimestamp("schedule_date")));
                row.add(rs.getString("location"));
                row.add(rs.getString("interviewer"));
                row.add(rs.getString("outcome"));

                // Add action buttons
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton rescheduleBtn = new JButton("Reschedule");
                JButton updateBtn = new JButton("Update Status");
                JButton cancelBtn = new JButton("Cancel");

                int interviewId = rs.getInt("interview_id");
                rescheduleBtn.addActionListener(e -> rescheduleInterview(interviewId));
                updateBtn.addActionListener(e -> updateInterviewStatus(interviewId));
                cancelBtn.addActionListener(e -> cancelInterview(interviewId));

                actionPanel.add(rescheduleBtn);
                actionPanel.add(updateBtn);
                actionPanel.add(cancelBtn);

                row.add(actionPanel);
                interviewsTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading interviews: " + ex.getMessage());
        }
    }

    private void rescheduleInterview(int interviewId) {
        JTextField dateField = new JTextField(20);
        JTextField timeField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField interviewerField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("New Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("New Time (HH:mm):"));
        panel.add(timeField);
        panel.add(new JLabel("New Location:"));
        panel.add(locationField);
        panel.add(new JLabel("New Interviewer:"));
        panel.add(interviewerField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Reschedule Interview", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "UPDATE Interview SET schedule_date = ?, location = ?, interviewer = ? WHERE interview_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, dateField.getText() + " " + timeField.getText());
                stmt.setString(2, locationField.getText());
                stmt.setString(3, interviewerField.getText());
                stmt.setInt(4, interviewId);
                stmt.executeUpdate();
                refreshInterviewsTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error rescheduling interview: " + ex.getMessage());
            }
        }
    }

    private void updateInterviewStatus(int interviewId) {
        String[] statuses = {"scheduled", "completed", "cancelled", "no_show"};
        String status = (String) JOptionPane.showInputDialog(this,
                "Select new status:",
                "Update Interview Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statuses,
                statuses[0]);

        if (status != null) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "UPDATE Interview SET outcome = ? WHERE interview_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, status);
                stmt.setInt(2, interviewId);
                stmt.executeUpdate();
                refreshInterviewsTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating interview status: " + ex.getMessage());
            }
        }
    }

    private void cancelInterview(int interviewId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this interview?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "UPDATE Interview SET outcome = 'cancelled' WHERE interview_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, interviewId);
                stmt.executeUpdate();
                refreshInterviewsTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error cancelling interview: " + ex.getMessage());
            }
        }
    }

    private void scheduleNewInterview() {
        // Create a dialog for scheduling a new interview
        JDialog dialog = new JDialog(this, "Schedule New Interview", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Job selection
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Job:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> jobComboBox = new JComboBox<>();
        dialog.add(jobComboBox, gbc);

        // Applicant selection
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Select Applicant:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> applicantComboBox = new JComboBox<>();
        dialog.add(applicantComboBox, gbc);

        // Date and time
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField(20);
        dialog.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        JTextField timeField = new JTextField(20);
        dialog.add(timeField, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        JTextField locationField = new JTextField(20);
        dialog.add(locationField, gbc);

        // Interviewer
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Interviewer:"), gbc);
        gbc.gridx = 1;
        JTextField interviewerField = new JTextField(20);
        dialog.add(interviewerField, gbc);

        // Load jobs and applicants
        try (Connection conn = DBUtil.getConnection()) {
            // Load jobs
            String jobSql = "SELECT j.job_id, j.title FROM JobPosting j WHERE j.company_id = ? AND j.status = 'open'";
            PreparedStatement jobStmt = conn.prepareStatement(jobSql);
            jobStmt.setInt(1, companyId);
            ResultSet jobRs = jobStmt.executeQuery();
            while (jobRs.next()) {
                jobComboBox.addItem(jobRs.getString("title") + " (ID: " + jobRs.getInt("job_id") + ")");
            }

            // Load applicants
            String applicantSql = "SELECT DISTINCT a.applicant_id, ap.full_name " +
                    "FROM Application a " +
                    "JOIN Applicant ap ON a.applicant_id = ap.applicant_id " +
                    "JOIN JobPosting j ON a.job_id = j.job_id " +
                    "WHERE j.company_id = ? AND a.status = 'under_review'";
            PreparedStatement applicantStmt = conn.prepareStatement(applicantSql);
            applicantStmt.setInt(1, companyId);
            ResultSet applicantRs = applicantStmt.executeQuery();
            while (applicantRs.next()) {
                applicantComboBox.addItem(applicantRs.getString("full_name") + " (ID: " + applicantRs.getInt("applicant_id") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading data: " + ex.getMessage());
            dialog.dispose();
            return;
        }

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton scheduleButton = createStyledButton("Schedule");
        JButton cancelButton = createStyledButton("Cancel");

        scheduleButton.addActionListener(e -> {
            String selectedJob = (String) jobComboBox.getSelectedItem();
            String selectedApplicant = (String) applicantComboBox.getSelectedItem();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String location = locationField.getText().trim();
            String interviewer = interviewerField.getText().trim();

            if (selectedJob == null || selectedApplicant == null || date.isEmpty() || 
                time.isEmpty() || location.isEmpty() || interviewer.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
                return;
            }

            try (Connection conn = DBUtil.getConnection()) {
                // Get application ID
                int jobId = Integer.parseInt(selectedJob.substring(selectedJob.lastIndexOf("ID: ") + 4, selectedJob.length() - 1));
                int applicantId = Integer.parseInt(selectedApplicant.substring(selectedApplicant.lastIndexOf("ID: ") + 4, selectedApplicant.length() - 1));
                
                String appSql = "SELECT application_id FROM Application WHERE job_id = ? AND applicant_id = ?";
                PreparedStatement appStmt = conn.prepareStatement(appSql);
                appStmt.setInt(1, jobId);
                appStmt.setInt(2, applicantId);
                ResultSet appRs = appStmt.executeQuery();

                if (appRs.next()) {
                    int applicationId = appRs.getInt("application_id");
                    String sql = "INSERT INTO Interview (application_id, schedule_date, interviewer, location, outcome) " +
                            "VALUES (?, ?, ?, ?, 'scheduled')";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, applicationId);
                    stmt.setString(2, date + " " + time);
                    stmt.setString(3, interviewer);
                    stmt.setString(4, location);
                    stmt.executeUpdate();
                    sql = "UPDATE Application SET status = 'interview_scheduled', last_updated = GETDATE() " +
                            "WHERE application_id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, applicationId);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Interview scheduled successfully!");
                    dialog.dispose();
                    refreshInterviewsTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "No application found for the selected job and applicant.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error scheduling interview: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(scheduleButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

}
