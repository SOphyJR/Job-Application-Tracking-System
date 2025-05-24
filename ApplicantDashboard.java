package jobTrack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class ApplicantDashboard extends JFrame {
    private int userId;
    private int applicantId;
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

    public ApplicantDashboard(int userId) {
        this.userId = userId;
        loadApplicantData();
        initializeUI();
    }

    private void loadApplicantData() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT a.applicant_id, a.full_name FROM Applicant a WHERE a.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.applicantId = rs.getInt("applicant_id");
                this.fullName = rs.getString("full_name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading applicant data: " + ex.getMessage());
        }
    }

    private void initializeUI() {
        setTitle("Applicant Dashboard - " + fullName);
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
        mainPanel.add(createJobsPanel(), "JOBS");
        mainPanel.add(createApplicationsPanel(), "APPLICATIONS");
        mainPanel.add(createApplyJobPanel(), "APPLY");
        mainPanel.add(createInterviewsPanel(), "INTERVIEWS");

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setBackground(PRIMARY_COLOR);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Style buttons
        JButton homeBtn = createStyledButton("Home");
        JButton viewJobsBtn = createStyledButton("View Jobs");
        JButton viewApplicationsBtn = createStyledButton("My Applications");
        JButton applyJobBtn = createStyledButton("Apply for Job");
        JButton interviewsBtn = createStyledButton("My Interviews");
        JButton logoutBtn = createStyledButton("Logout");

        homeBtn.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        viewJobsBtn.addActionListener(e -> {
            refreshJobsTable();
            cardLayout.show(mainPanel, "JOBS");
        });
        viewApplicationsBtn.addActionListener(e -> {
            refreshApplicationsTable();
            cardLayout.show(mainPanel, "APPLICATIONS");
        });
        applyJobBtn.addActionListener(e -> cardLayout.show(mainPanel, "APPLY"));
        interviewsBtn.addActionListener(e -> {
            refreshInterviewsTable();
            cardLayout.show(mainPanel, "INTERVIEWS");
        });
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginRegisterFrame().setVisible(true);
        });

        navPanel.add(homeBtn);
        navPanel.add(viewJobsBtn);
        navPanel.add(viewApplicationsBtn);
        navPanel.add(applyJobBtn);
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

        // Welcome message with applicant's name
        JLabel welcomeLabel = new JLabel("Welcome, " + fullName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(SECONDARY_COLOR);
        gbc.gridy = 1;
        panel.add(welcomeLabel, gbc);

        // Dashboard description
        JTextArea description = new JTextArea(
            "Welcome to your job application dashboard!\n\n" +
            "• Browse and search for available jobs\n" +
            "• Submit applications with cover letters\n" +
            "• Track your application status\n" +
            "• View your application history\n\n" +
            "Use the navigation buttons above to get started."
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

    private JPanel createJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(PRIMARY_COLOR);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));

        JComboBox<String> searchType = new JComboBox<>(new String[]{"All", "By Title", "By Company", "By Specialization"});
        searchType.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton searchBtn = createStyledButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchType);
        searchPanel.add(searchBtn);

        // Table
        String[] columns = {"Job ID", "Title", "Company", "Specialization", "Posting Date", "Deadline", "Status"};
        jobsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jobsTable = new JTable(jobsTableModel);
        JScrollPane scrollPane = new JScrollPane(jobsTable);

        // Style table
        jobsTable.setBackground(Color.WHITE);
        jobsTable.setForeground(TEXT_COLOR);
        jobsTable.setGridColor(PRIMARY_COLOR);
        jobsTable.setSelectionBackground(PRIMARY_COLOR);
        jobsTable.setSelectionForeground(Color.WHITE);
        jobsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Style table header
        jobsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        jobsTable.getTableHeader().setForeground(Color.WHITE);
        jobsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Add components
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add search functionality
        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            String searchTypeStr = (String) searchType.getSelectedItem();
            searchJobs(searchText, searchTypeStr);
        });

        // Initial load
        refreshJobsTable();

        return panel;
    }

    private void searchJobs(String searchText, String searchType) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT j.job_id, j.title, c.name as company_name, j.specialization_required, " +
                    "j.posting_date, j.deadline, j.status " +
                    "FROM JobPosting j " +
                    "JOIN Company c ON j.company_id = c.company_id " +
                    "WHERE j.status = 'open' ";

            if (!searchText.isEmpty()) {
                switch (searchType) {
                    case "By Title":
                        sql += "AND j.title LIKE ? ";
                        break;
                    case "By Company":
                        sql += "AND c.name LIKE ? ";
                        break;
                    case "By Specialization":
                        sql += "AND j.specialization_required LIKE ? ";
                        break;
                }
            }

            sql += "ORDER BY j.posting_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!searchText.isEmpty()) {
                stmt.setString(1, "%" + searchText + "%");
            }

            ResultSet rs = stmt.executeQuery();
            jobsTableModel.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("job_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("company_name"));
                row.add(rs.getString("specialization_required"));
                row.add(sdf.format(rs.getTimestamp("posting_date")));
                row.add(sdf.format(rs.getTimestamp("deadline")));
                row.add(rs.getString("status"));
                jobsTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching jobs: " + ex.getMessage());
        }
    }

    private void refreshJobsTable() {
        searchJobs("", "All");
    }

    private JPanel createApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        String[] columns = {"Application ID", "Job Title", "Company", "Submission Date", "Status", "Last Updated"};
        applicationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        applicationsTable = new JTable(applicationsTableModel);
        JScrollPane scrollPane = new JScrollPane(applicationsTable);

        // Style table
        applicationsTable.setBackground(Color.WHITE);
        applicationsTable.setForeground(TEXT_COLOR);
        applicationsTable.setGridColor(PRIMARY_COLOR);
        applicationsTable.setSelectionBackground(PRIMARY_COLOR);
        applicationsTable.setSelectionForeground(Color.WHITE);
        applicationsTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Style table header
        applicationsTable.getTableHeader().setBackground(PRIMARY_COLOR);
        applicationsTable.getTableHeader().setForeground(Color.WHITE);
        applicationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(scrollPane, BorderLayout.CENTER);
        refreshApplicationsTable();

        return panel;
    }

    private void refreshApplicationsTable() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT a.application_id, j.title, c.name as company_name, " +
                    "a.submission_date, a.status, a.last_updated " +
                    "FROM Application a " +
                    "JOIN JobPosting j ON a.job_id = j.job_id " +
                    "JOIN Company c ON j.company_id = c.company_id " +
                    "WHERE a.applicant_id = ? " +
                    "ORDER BY a.submission_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            ResultSet rs = stmt.executeQuery();

            applicationsTableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("application_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("company_name"));
                row.add(sdf.format(rs.getTimestamp("submission_date")));
                row.add(rs.getString("status"));
                row.add(sdf.format(rs.getTimestamp("last_updated")));
                applicationsTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading applications: " + ex.getMessage());
        }
    }

    private JPanel createApplyJobPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Style labels
        JLabel jobLabel = new JLabel("Select Job:");
        jobLabel.setForeground(PRIMARY_COLOR);
        jobLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel coverLetterLabel = new JLabel("Cover Letter:");
        coverLetterLabel.setForeground(PRIMARY_COLOR);
        coverLetterLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Job selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(jobLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> jobComboBox = new JComboBox<>();
        jobComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(jobComboBox, gbc);

        // Cover letter
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(coverLetterLabel, gbc);
        gbc.gridx = 1;
        JTextArea coverLetterArea = new JTextArea(5, 30);
        coverLetterArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane coverLetterScroll = new JScrollPane(coverLetterArea);
        panel.add(coverLetterScroll, gbc);

        // Apply button
        gbc.gridx = 1; gbc.gridy = 2;
        JButton applyButton = createStyledButton("Submit Application");
        panel.add(applyButton, gbc);

        // Load available jobs
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT j.job_id, j.title, c.name as company_name " +
                    "FROM JobPosting j " +
                    "JOIN Company c ON j.company_id = c.company_id " +
                    "WHERE j.status = 'open' " +
                    "AND j.deadline > NOW() " +
                    "AND j.job_id NOT IN (SELECT job_id FROM Application WHERE applicant_id = ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                jobComboBox.addItem(rs.getString("title") + " - " + rs.getString("company_name") +
                        " (ID: " + rs.getInt("job_id") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading jobs: " + ex.getMessage());
        }

        // Apply button action
        applyButton.addActionListener(e -> {
            String selectedJob = (String) jobComboBox.getSelectedItem();
            if (selectedJob == null) {
                JOptionPane.showMessageDialog(this, "Please select a job to apply for.");
                return;
            }

            String coverLetter = coverLetterArea.getText().trim();
            if (coverLetter.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please write a cover letter.");
                return;
            }

            // Extract job ID from the selected item
            int jobId = Integer.parseInt(selectedJob.substring(selectedJob.lastIndexOf("ID: ") + 4, selectedJob.length() - 1));

            try (Connection conn = DBUtil.getConnection()) {
                String sql = "INSERT INTO Application (applicant_id, job_id, submission_date, cover_letter, status, last_updated) " +
                        "VALUES (?, ?, NOW(), ?, 'applied', NOW())";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, applicantId);
                stmt.setInt(2, jobId);
                stmt.setString(3, coverLetter);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Application submitted successfully!");
                coverLetterArea.setText("");
                refreshApplicationsTable();
                cardLayout.show(mainPanel, "APPLICATIONS");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error submitting application: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createInterviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        String[] columns = {"Interview ID", "Job Title", "Company", "Date & Time", "Location", "Interviewer", "Status"};
        interviewsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
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
            String sql = "SELECT i.interview_id, j.title, c.name as company_name, " +
                    "i.schedule_date, i.location, i.interviewer, i.outcome " +
                    "FROM Interview i " +
                    "JOIN Application a ON i.application_id = a.application_id " +
                    "JOIN JobPosting j ON a.job_id = j.job_id " +
                    "JOIN Company c ON j.company_id = c.company_id " +
                    "WHERE a.applicant_id = ? " +
                    "ORDER BY i.schedule_date DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, applicantId);
            ResultSet rs = stmt.executeQuery();

            interviewsTableModel.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("interview_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("company_name"));
                row.add(sdf.format(rs.getTimestamp("schedule_date")));
                row.add(rs.getString("location"));
                row.add(rs.getString("interviewer"));
                row.add(rs.getString("outcome"));
                interviewsTableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading interviews: " + ex.getMessage());
        }
    }
}
