package jobTrack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class LoginRegisterFrame extends JFrame {
    private JTabbedPane tabbedPane;
    // Login components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;
    private JLabel loginStatusLabel;

    // Register components
    private JComboBox<String> roleComboBox;
    private JPanel applicantPanel, managerPanel;
    private JTextField regUsernameField, regFullNameField, regAgeField, regEmailField, regPhoneField, regAddressField;
    private JComboBox<String> regQualificationField;
    private JTextField regSpecializationField, regPhotoField, regRecommendationField;
    private JTextField regManagerFullNameField, regManagerEmailField, regManagerCompanyField;
    private JTextField regManagerIndustryField, regManagerPhoneField;
    private JPasswordField regPasswordField;
    private JButton registerButton;
    private JLabel registerStatusLabel;
    private JPasswordField regManagerPassField;
    private static final String MANAGER_PASSWORD = "Manager2025";

    public LoginRegisterFrame() {
        setTitle("Job Application System - WELL COME TO LOGIN PAGE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        // --- Decoration Start ---
        getContentPane().setBackground(Color.BLACK);
        UIManager.put("TabbedPane.selected", new Color(255, 140, 0)); // Orange for selected tab
        UIManager.put("TabbedPane.background", new Color(70, 130, 180)); // Blue for tab background
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.unselectedBackground", new Color(255, 99, 71)); // Red for unselected tabs
        UIManager.put("TabbedPane.borderHightlightColor", new Color(255, 140, 0));
        UIManager.put("TabbedPane.contentAreaColor", new Color(240, 248, 255));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JButton aboutButton = new JButton("About");
        aboutButton.setBackground(new Color(70, 130, 180));
        aboutButton.setForeground(Color.WHITE);
        aboutButton.setFocusPainted(false);
        aboutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Job Application Tracking System \n" +
                            "WE ARE SECOND YEAR INFORMATION SYSTEM STUDENTS, " +
                            "OUR GROUP HAVE SIX MEMBERS \n" +
                            "\t1, Mebrie Awoke\n" +
                            "\t2, Sophonyas Bewuketu\n" +
                            "\t3, Aman Ataklety\n" +
                            "\t4, Yeabsira Getachew\n" +
                            "\t5, Honelgn Yohannes \n" +
                            "\t6,Solomon Melese\n" +
                            "CONTACTS: phone +251 922 545 447 \n" +
                            "\tEmail: mebrieawoke941@gmail.com\n" +
                            "\tTeg: @ze_meryma_21",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
        topPanel.add(aboutButton);
        add(topPanel, BorderLayout.SOUTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.addTab("HOME", createHomePanel());
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Signup", createRegisterPanel());
        add(tabbedPane);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.pink);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2;

        // Welcome Title
        JLabel welcomeTitle = new JLabel("Welcome to Job Application System", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeTitle.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeTitle, gbc);

        // Description
        JTextArea description = new JTextArea(
            ".\n\n" +
            "\n" +
            "• Apply jobs by your specialization\n" +
            "• Track applications and candidates\n" +
            "• Schedule interviews\n" +
            "• Manage hiring process\n\n" +
                    "\n"+
            "Please login or sign up to continue."
        );
        description.setEditable(false);
        description.setBackground(Color.PINK);
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(Color.BLACK);
        gbc.gridy = 1;
        panel.add(description, gbc);

        // Quick Links
        JPanel quickLinksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        quickLinksPanel.setBackground(Color.PINK);
        
        JButton loginLink = new JButton("Go to Login");
        JButton signupLink = new JButton("Go to Sign Up");
        
        loginLink.setBackground(new Color(70, 130, 180));
        loginLink.setForeground(Color.WHITE);
        signupLink.setBackground(new Color(70, 130, 180));
        signupLink.setForeground(Color.WHITE);
        
        loginLink.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        signupLink.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        quickLinksPanel.add(loginLink);
        quickLinksPanel.add(signupLink);
        
        gbc.gridy = 2;
        panel.add(quickLinksPanel, gbc);

        return panel;
    }

    private JPanel createLoginPanel() {
    	JPanel panel = new JPanel(new GridBagLayout()) {
    	    @Override
    	    protected void paintComponent(Graphics g) {
    	        super.paintComponent(g);
    	        Graphics2D g2d = (Graphics2D) g;
    	        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    	        Color color1 = new Color(0, 180, 219); // Teal blue
    	        Color color2 = new Color(0, 131, 176); // Darker teal
    	        GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
    	        g2d.setPaint(gp);
    	        g2d.fillRect(0, 0, getWidth(), getHeight());
    	    }
    	};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(20);
        panel.add(loginUsernameField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(20);
        panel.add(loginPasswordField, gbc);
        gbc.gridx = 1; gbc.gridy++;
        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);
        gbc.gridy++;
        loginStatusLabel = new JLabel("");
        panel.add(loginStatusLabel, gbc);

        loginButton.addActionListener(e -> handleLogin());
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 140, 0)); // Orange background for register panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 218, 185)); // Light orange for form
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Applicant", "Hiring Manager"});
        formPanel.add(roleComboBox, gbc);
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        regUsernameField = new JTextField(20);
        formPanel.add(regUsernameField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        regPasswordField = new JPasswordField(20);
        formPanel.add(regPasswordField, gbc);

        // Applicant panel
        applicantPanel = new JPanel(new GridBagLayout());
        applicantPanel.setBackground(new Color(240, 248, 255)); // Light blue for applicant panel
        GridBagConstraints agbc = new GridBagConstraints();
        agbc.insets = new Insets(2, 2, 2, 2);
        agbc.gridx = 0; agbc.gridy = 0;
        applicantPanel.add(new JLabel("Full Name:"), agbc);
        agbc.gridx = 1;
        regFullNameField = new JTextField(15);
        applicantPanel.add(regFullNameField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Age:"), agbc);
        agbc.gridx = 1;
        regAgeField = new JTextField(5);
        applicantPanel.add(regAgeField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Email:"), agbc);
        agbc.gridx = 1;
        regEmailField = new JTextField(15);
        applicantPanel.add(regEmailField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Phone:"), agbc);
        agbc.gridx = 1;
        regPhoneField = new JTextField(10);
        applicantPanel.add(regPhoneField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Address:"), agbc);
        agbc.gridx = 1;
        regAddressField = new JTextField(15);
        applicantPanel.add(regAddressField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Qualification Level:"), agbc);
        agbc.gridx = 1;
        regQualificationField = new JComboBox<>(new String[]{"High School", "Bachelor", "Master", "PhD"});
        applicantPanel.add(regQualificationField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Specialization:"), agbc);
        agbc.gridx = 1;
        regSpecializationField = new JTextField(10);
        applicantPanel.add(regSpecializationField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Photo (path):"), agbc);
        agbc.gridx = 1;
        regPhotoField = new JTextField(10);
        applicantPanel.add(regPhotoField, agbc);
        agbc.gridx = 0; agbc.gridy++;
        applicantPanel.add(new JLabel("Recommendation Letter (path):"), agbc);
        agbc.gridx = 1;
        regRecommendationField = new JTextField(10);
        applicantPanel.add(regRecommendationField, agbc);

        // Manager panel
        managerPanel = new JPanel(new GridBagLayout());
        managerPanel.setBackground(new Color(173, 216, 230)); // Lighter blue for manager panel
        GridBagConstraints mgbc = new GridBagConstraints();
        mgbc.insets = new Insets(2, 2, 2, 2);
        mgbc.gridx = 0; mgbc.gridy = 0;
        managerPanel.add(new JLabel("Full Name:"), mgbc);
        mgbc.gridx = 1;
        regManagerFullNameField = new JTextField(15);
        managerPanel.add(regManagerFullNameField, mgbc);
        mgbc.gridx = 0; mgbc.gridy++;
        managerPanel.add(new JLabel("Contact Email:"), mgbc);
        mgbc.gridx = 1;
        regManagerEmailField = new JTextField(15);
        managerPanel.add(regManagerEmailField, mgbc);
        mgbc.gridx = 0; mgbc.gridy++;
        managerPanel.add(new JLabel("Company Name:"), mgbc);
        mgbc.gridx = 1;
        regManagerCompanyField = new JTextField(15);
        managerPanel.add(regManagerCompanyField, mgbc);
        mgbc.gridx = 0; mgbc.gridy++;
        managerPanel.add(new JLabel("Industry:"), mgbc);
        mgbc.gridx = 1;
        regManagerIndustryField = new JTextField(15);
        managerPanel.add(regManagerIndustryField, mgbc);
        mgbc.gridx = 0; mgbc.gridy++;
        managerPanel.add(new JLabel("Phone:"), mgbc);
        mgbc.gridx = 1;
        regManagerPhoneField = new JTextField(15);
        managerPanel.add(regManagerPhoneField, mgbc);
        mgbc.gridx = 0; mgbc.gridy++;
        managerPanel.add(new JLabel("Manager Pass:"), mgbc);
        mgbc.gridx = 1;
        regManagerPassField = new JPasswordField(15);
        managerPanel.add(regManagerPassField, mgbc);

        JPanel dynamicPanel = new JPanel(new CardLayout());
        dynamicPanel.add(applicantPanel, "Applicant");
        dynamicPanel.add(managerPanel, "Hiring Manager");

        roleComboBox.addActionListener(e -> {
            CardLayout cl = (CardLayout) (dynamicPanel.getLayout());
            cl.show(dynamicPanel, (String) roleComboBox.getSelectedItem());
        });

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(dynamicPanel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        registerButton = new JButton("Register");
        formPanel.add(registerButton, gbc);
        gbc.gridy++;
        registerStatusLabel = new JLabel("");
        formPanel.add(registerStatusLabel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        registerButton.addActionListener(e -> handleRegister());
        return panel;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please enter username and password.");
            return;
        }
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");
                if ("hiring_manager".equals(role)) {
                    new ManagerDashboard(userId).setVisible(true);
                } else if ("applicant".equals(role)) {
                    new ApplicantDashboard(userId).setVisible(true);
                }
                this.dispose();
            } else {
                loginStatusLabel.setText("Invalid username or password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            loginStatusLabel.setText("Login failed: " + ex.getMessage());
        }
    }

    private void handleRegister() {
        String role = (String) roleComboBox.getSelectedItem();
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            registerStatusLabel.setText("Username and password required.");
            return;
        }
        if (role.equals("Applicant")) {
            String fullName = regFullNameField.getText().trim();
            String age = regAgeField.getText().trim();
            String email = regEmailField.getText().trim();
            String phone = regPhoneField.getText().trim();
            String address = regAddressField.getText().trim();
            String qualification = (String) regQualificationField.getSelectedItem();
            String specialization = regSpecializationField.getText().trim();
            String photo = regPhotoField.getText().trim();
            String recommendation = regRecommendationField.getText().trim();
            if (fullName.isEmpty() || age.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || specialization.isEmpty()) {
                registerStatusLabel.setText("Please fill all applicant fields.");
                return;
            }
            // Database registration logic for Applicant
            try (Connection conn = DBUtil.getConnection()) {
                // Insert into users
                String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'applicant')";
                PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, username);
                userStmt.setString(2, password);
                userStmt.executeUpdate();
                ResultSet userKeys = userStmt.getGeneratedKeys();
                int userId = -1;
                if (userKeys.next()) {
                    userId = userKeys.getInt(1);
                }
                // Insert into Applicant
                String appSql = "INSERT INTO Applicant (user_id, full_name, age, email, phone, address, qualification_level, specialization, photo, recommendation_letter) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement appStmt = conn.prepareStatement(appSql);
                appStmt.setInt(1, userId);
                appStmt.setString(2, fullName);
                appStmt.setInt(3, Integer.parseInt(age));
                appStmt.setString(4, email);
                appStmt.setString(5, phone);
                appStmt.setString(6, address);
                appStmt.setString(7, qualification);
                appStmt.setString(8, specialization);
                appStmt.setString(9, photo);
                appStmt.setString(10, recommendation);
                appStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Applicant registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setSelectedIndex(0); // Go back to login
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String fullName = regManagerFullNameField.getText().trim();
            String email = regManagerEmailField.getText().trim();
            String company = regManagerCompanyField.getText().trim();
            String industry = regManagerIndustryField.getText().trim();
            String phone = regManagerPhoneField.getText().trim();
            String managerPass = new String(regManagerPassField.getPassword());

            if (fullName.isEmpty() || email.isEmpty() || company.isEmpty() || industry.isEmpty() || phone.isEmpty() || managerPass.isEmpty()) {
                registerStatusLabel.setText("Please fill all manager fields");
                return;
            }

            // Validate manager password
            if (!managerPass.equalsIgnoreCase(MANAGER_PASSWORD)) {
                registerStatusLabel.setText("Invalid manager password");
                return;
            }

            // Database registration logic for Manager
            try (Connection conn = DBUtil.getConnection()) {
                // Insert into users
                String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'hiring_manager')";
                PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, username);
                userStmt.setString(2, password);
                userStmt.executeUpdate();
                ResultSet userKeys = userStmt.getGeneratedKeys();
                int userId = -1;
                if (userKeys.next()) {
                    userId = userKeys.getInt(1);
                }
                // Insert or get company
                int companyId = -1;
                String checkCompanySql = "SELECT company_id FROM Company WHERE name = ?";
                PreparedStatement checkCompanyStmt = conn.prepareStatement(checkCompanySql);
                checkCompanyStmt.setString(1, company);
                ResultSet companyRs = checkCompanyStmt.executeQuery();
                if (companyRs.next()) {
                    companyId = companyRs.getInt(1);
                } else {
                    String insertCompanySql = "INSERT INTO Company (name, industry, email, phone) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertCompanyStmt = conn.prepareStatement(insertCompanySql, Statement.RETURN_GENERATED_KEYS);
                    insertCompanyStmt.setString(1, company);
                    insertCompanyStmt.setString(2, industry);
                    insertCompanyStmt.setString(3, email);
                    insertCompanyStmt.setString(4, phone);
                    insertCompanyStmt.executeUpdate();
                    ResultSet companyKeys = insertCompanyStmt.getGeneratedKeys();
                    if (companyKeys.next()) {
                        companyId = companyKeys.getInt(1);
                    }
                }
                // Insert into HiringManager
                String mgrSql = "INSERT INTO HiringManager (user_id, company_id, full_name, contact_email) VALUES (?, ?, ?, ?)";
                PreparedStatement mgrStmt = conn.prepareStatement(mgrSql);
                mgrStmt.setInt(1, userId);
                mgrStmt.setInt(2, companyId);
                mgrStmt.setString(3, fullName);
                mgrStmt.setString(4, email);
                mgrStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Manager registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setSelectedIndex(0); // Go back to login
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginRegisterFrame().setVisible(true);
        });
    }
} 
