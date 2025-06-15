package simplebankapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SimpleBankingApp extends JFrame {

    static final String DB_URL = "jdbc:mysql://localhost:3306/bank";
    static final String USER = "root";
    static final String PASS = "System";

    private JTextField nameField, amountField, idField;
    private JTextArea outputArea;
    private String loggedInUser = null;

    public SimpleBankingApp() {
        if (!loginDialog()) {
            JOptionPane.showMessageDialog(this, "Login failed or cancelled.");
            System.exit(0);
        }else {

        setTitle("Simple Bank App");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));

        panel.add(new JLabel("Account ID:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        panel.add(amountField);

        JButton createBtn = new JButton("Create Account");
        JButton balanceBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton viewHistoryBtn = new JButton("View Transactions");
        JButton exitBtn = new JButton("Exit");


        panel.add(createBtn);
        panel.add(balanceBtn);
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(viewHistoryBtn);
        panel.add(exitBtn);


        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            double initialDeposit = getAmount();
            if (name.isEmpty() || initialDeposit < 0) {
                showMessage("Invalid name or amount.");
                return;
            }
            createAccount(name, initialDeposit);
        });

        balanceBtn.addActionListener(e -> {
            int id = getId();
            if (id != -1) checkBalance(id);
        });

        depositBtn.addActionListener(e -> {
            int id = getId();
            double amount = getAmount();
            if (id != -1 && amount > 0) deposit(id, amount);
        });

        withdrawBtn.addActionListener(e -> {
            int id = getId();
            double amount = getAmount();
            if (id != -1 && amount > 0) withdraw(id, amount);
        });

        viewHistoryBtn.addActionListener(e -> {
            int id = getId();
            if (id != -1) viewTransactionHistory(id);
        });
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private boolean loginDialog() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] fields = {
                "Username:", userField,
                "Password:", passField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            return authenticate(username, password);
        }
        return false;
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUser = username;
                return true;
            }
        } catch (SQLException e) {
            showMessage("Authentication Error: " + e.getMessage());
        }
        return false;
    }

    private void createAccount(String name, double initialDeposit) {
        String sql = "INSERT INTO accounts (name, balance) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, initialDeposit);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                logTransaction(id, "Account Creation", initialDeposit);
                showMessage("Account created successfully with ID: " + id);
            }
        } catch (SQLException e) {
            showMessage("Error creating account: " + e.getMessage());
        }
    }

    private void checkBalance(int id) {
        String sql = "SELECT name, balance FROM accounts WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                showMessage("Account Holder: " + rs.getString("name") +
                        "\nBalance: $" + rs.getDouble("balance"));
            } else {
                showMessage("Account not found.");
            }
        } catch (SQLException e) {
            showMessage("Error checking balance: " + e.getMessage());
        }
    }

    private void deposit(int id, double amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            if (pstmt.executeUpdate() > 0) {
                logTransaction(id, "Deposit", amount);
                showMessage("Deposit successful.");
            } else {
                showMessage("Account not found.");
            }
        } catch (SQLException e) {
            showMessage("Error during deposit: " + e.getMessage());
        }
    }

    private void withdraw(int id, double amount) {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            pstmt.setDouble(3, amount);
            if (pstmt.executeUpdate() > 0) {
                logTransaction(id, "Withdraw", amount);
                showMessage("Withdrawal successful.");
            } else {
                showMessage("Insufficient balance or account not found.");
            }
        } catch (SQLException e) {
            showMessage("Error during withdrawal: " + e.getMessage());
        }
    }

    private void viewTransactionHistory(int id) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder history = new StringBuilder("Transaction History:\n");

            while (rs.next()) {
                history.append(rs.getTimestamp("timestamp"))
                        .append(" | ").append(rs.getString("type"))
                        .append(" | $").append(rs.getDouble("amount")).append("\n");
            }

            showMessage(history.toString());
        } catch (SQLException e) {
            showMessage("Error fetching history: " + e.getMessage());
        }
    }

    private void logTransaction(int accountId, String type, double amount) {
        String sql = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            showMessage("Transaction log error: " + e.getMessage());
        }
    }

    private int getId() {
        try {
            return Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid Account ID.");
            return -1;
        }
    }

    private double getAmount() {
        try {
            return Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid amount.");
            return -1;
        }
    }

    private void showMessage(String message) {
        outputArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new SimpleBankingApp().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Startup Error: " + e.getMessage());
            }
        });
    }
}