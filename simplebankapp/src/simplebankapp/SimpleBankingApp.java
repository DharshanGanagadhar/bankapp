package simplebankapp;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Random;

// Account class to represent individual bank accounts
class Account {
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    
    // Constructor
    public Account(String accountHolderName) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = generateAccountNumber();
        this.balance = 0.0;
    }
    
    // Generate random account number
    private String generateAccountNumber() {
        Random random = new Random();
        return "ACC" + (100000 + random.nextInt(900000));
    }
    
    // Deposit money
    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }
    
    // Withdraw money
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    // Get current balance
    public double getBalance() {
        return balance;
    }
    
    // Get account details
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getAccountHolderName() {
        return accountHolderName;
    }
    
    // Display account information
    public void displayAccountInfo() {
        System.out.println("=== Account Information ===");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolderName);
        System.out.printf("Current Balance: $%.2f%n", balance);
        System.out.println("===========================");
    }
}

// Main Banking System class
public class SimpleBankingApp {
    private static HashMap<String, Account> accounts = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("🏦 Welcome to Simple Banking System 🏦");
        System.out.println("=====================================");
        
        while (true) {
            displayMenu();
            int choice = getValidChoice();
            
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    depositMoney();
                    break;
                case 3:
                    withdrawMoney();
                    break;
                case 4:
                    checkBalance();
                    break;
                case 5:
                    displayAccountInfo();
                    break;
                case 6:
                    listAllAccounts();
                    break;
                case 7:
                    System.out.println("Thank you for using Simple Banking System!");
                    System.out.println("Have a great day! 👋");
                    return;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    // Display main menu
    private static void displayMenu() {
        System.out.println("\n📋 Banking Menu:");
        System.out.println("1. Create New Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Check Balance");
        System.out.println("5. Display Account Information");
        System.out.println("6. List All Accounts");
        System.out.println("7. Exit");
        System.out.print("Enter your choice (1-7): ");
    }
    
    // Get valid menu choice with error handling
    private static int getValidChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return choice;
        } catch (Exception e) {
            scanner.nextLine(); // clear invalid input
            return -1; // return invalid choice
        }
    }
    
    // Create new account
    private static void createAccount() {
        System.out.println("\n🆕 Create New Account");
        System.out.println("====================");
        
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty!");
            return;
        }
        
        Account newAccount = new Account(name);
        accounts.put(newAccount.getAccountNumber(), newAccount);
        
        System.out.println("✅ Account created successfully!");
        System.out.println("Your account number is: " + newAccount.getAccountNumber());
        System.out.println("Please save this number for future transactions.");
    }
    
    // Deposit money
    private static void depositMoney() {
        System.out.println("\n💰 Deposit Money");
        System.out.println("=================");
        
        String accountNumber = getAccountNumber();
        if (accountNumber == null) return;
        
        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }
        
        System.out.print("Enter deposit amount: $");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline
            
            if (account.deposit(amount)) {
                System.out.printf("✅ Successfully deposited $%.2f%n", amount);
                System.out.printf("New balance: $%.2f%n", account.getBalance());
            } else {
                System.out.println("❌ Invalid deposit amount!");
            }
        } catch (Exception e) {
            scanner.nextLine(); // clear invalid input
            System.out.println("❌ Please enter a valid amount!");
        }
    }
    
    // Withdraw money
    private static void withdrawMoney() {
        System.out.println("\n💸 Withdraw Money");
        System.out.println("=================");
        
        String accountNumber = getAccountNumber();
        if (accountNumber == null) return;
        
        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }
        
        System.out.printf("Current balance: $%.2f%n", account.getBalance());
        System.out.print("Enter withdrawal amount: $");
        
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline
            
            if (account.withdraw(amount)) {
                System.out.printf("✅ Successfully withdrew $%.2f%n", amount);
                System.out.printf("Remaining balance: $%.2f%n", account.getBalance());
            } else {
                System.out.println("❌ Insufficient funds or invalid amount!");
            }
        } catch (Exception e) {
            scanner.nextLine(); // clear invalid input
            System.out.println("❌ Please enter a valid amount!");
        }
    }
    
    // Check balance
    private static void checkBalance() {
        System.out.println("\n💳 Check Balance");
        System.out.println("================");
        
        String accountNumber = getAccountNumber();
        if (accountNumber == null) return;
        
        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }
        
        System.out.printf("Account Balance: $%.2f%n", account.getBalance());
    }
    
    // Display full account information
    private static void displayAccountInfo() {
        System.out.println("\n📊 Account Information");
        System.out.println("======================");
        
        String accountNumber = getAccountNumber();
        if (accountNumber == null) return;
        
        Account account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("❌ Account not found!");
            return;
        }
        
        account.displayAccountInfo();
    }
    
    // List all accounts (for admin purposes)
    private static void listAllAccounts() {
        System.out.println("\n📋 All Accounts");
        System.out.println("===============");
        
        if (accounts.isEmpty()) {
            System.out.println("No accounts found in the system.");
            return;
        }
        
        System.out.printf("%-12s %-20s %-15s%n", "Account No.", "Account Holder", "Balance");
        System.out.println("---------------------------------------------------");
        
        for (Account account : accounts.values()) {
            System.out.printf("%-12s %-20s $%-14.2f%n", 
                account.getAccountNumber(), 
                account.getAccountHolderName(), 
                account.getBalance());
        }
        
        System.out.println("Total Accounts: " + accounts.size());
    }
    
    // Helper method to get account number from user
    private static String getAccountNumber() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine().trim().toUpperCase();
        
        if (accountNumber.isEmpty()) {
            System.out.println("❌ Account number cannot be empty!");
            return null;
        }
        
        return accountNumber;
    }
}