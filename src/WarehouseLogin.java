import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class WarehouseManagementApp extends JFrame {
    private JTextField supplierNameField, productNameField, productPriceField, orderCustomerIdField;
    private JTextArea itemListArea;

    public WarehouseManagementApp() {
        setTitle("Warehouse Management System");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        supplierNameField = new JTextField(15);
        productNameField = new JTextField(15);
        productPriceField = new JTextField(10);
        orderCustomerIdField = new JTextField(10);
        JButton addSupplierButton = new JButton("Add Supplier");
        JButton addProductButton = new JButton("Add Product");
        JButton addOrderButton = new JButton("Add Order");
        JButton viewItemsButton = new JButton("View Items");
        itemListArea = new JTextArea(15, 40);
        itemListArea.setEditable(false);

        add(new JLabel("Supplier Name:"));
        add(supplierNameField);
        add(addSupplierButton);
        add(new JLabel("Product Name:"));
        add(productNameField);
        add(new JLabel("Product Price:"));
        add(productPriceField);
        add(addProductButton);
        add(new JLabel("Customer ID for Order:"));
        add(orderCustomerIdField);
        add(addOrderButton);
        add(viewItemsButton);
        add(new JScrollPane(itemListArea));

        addSupplierButton.addActionListener(e -> addSupplier());
        addProductButton.addActionListener(e -> addProduct());
        addOrderButton.addActionListener(e -> addOrder());
        viewItemsButton.addActionListener(e -> viewItems());
    }

    private void addSupplier() {
        String name = supplierNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier name cannot be empty.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Supplier (Supplier_Name) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
                supplierNameField.setText(""); // Clear the input field
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding supplier: " + e.getMessage());
        }
    }

    private void addProduct() {
        String name = productNameField.getText().trim();
        String priceText = productPriceField.getText().trim();
        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name and price cannot be empty.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Product (Product_Name, Price) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                productNameField.setText(""); // Clear the input fields
                productPriceField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage());
        }
    }

    private void addOrder() {
        String customerIdText = orderCustomerIdField.getText().trim();
        if (customerIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer ID cannot be empty.");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(customerIdText);
            if (customerId <= 0) {
                JOptionPane.showMessageDialog(this, "Customer ID must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Customer ID.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Orders (Customer_ID, Order_Date) VALUES (?, GETDATE())"; // Assuming you want to set the current date as the order date
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, customerId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Order added successfully!");
                orderCustomerIdField.setText(""); // Clear the input field
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding order: " + e.getMessage());
        }
    }

    private void viewItems() {
        itemListArea.setText(""); // Clear the text area
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Supplier"; // Example for viewing suppliers
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = "Supplier ID: " + rs.getInt("Supplier_ID") + ", Name: " + rs.getString("Supplier_Name");
                    itemListArea.append(item + "\n");
                }
            }

            // You can add more queries here to view Products, Orders, etc.
            sql = "SELECT * FROM Product"; // Example for viewing products
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = "Product ID: " + rs.getInt("Product_ID") + ", Name: " + rs.getString("Product_Name") + ", Price: " + rs.getDouble("Price");
                    itemListArea.append(item + "\n");
                }
            }

            sql = "SELECT * FROM Orders"; // Example for viewing orders
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = "Order ID: " + rs.getInt("Order_ID") + ", Customer ID: " + rs.getInt("Customer_ID") + ", Order Date: " + rs.getDate("Order_Date");
                    itemListArea.append(item + "\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving items: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WarehouseManagementApp app = new WarehouseManagementApp();
            app.setVisible(true);
        });
    }
}