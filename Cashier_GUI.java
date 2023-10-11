import java.sql.*;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Cashier_GUI extends JFrame {
    private static Connection conn = null;
    private static ArrayList<MenuItem> orderSummary = new ArrayList<>();
    private static JPanel orderSummaryPanel;
    private static JLabel totalLabel;
    public static void main(String[] args)
    {
        //Building the connection
        try {
        conn = DriverManager.getConnection(
            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
            "csce315_909_rahul_2003",
            "Rs03252003#");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null,"Opened database successfully");

        JFrame frame = new JFrame("Cashier POS System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        
        // Create order summary panel
        orderSummaryPanel = createOrderSummaryPanel();

        // Create menu items panel
        JPanel menuPanel = createMenuPanel();

        //Create panel for total
        totalLabel = new JLabel("Total: $0.00");
        
        frame.add(menuPanel);
        frame.add(orderSummaryPanel);
        frame.add(totalLabel);
        
        frame.setLayout(new GridLayout(1, 2)); // Arrange panels side by side

        // Create a panel for checkout and cancel buttons
        JPanel checkoutPanel = new JPanel();
        JButton checkoutButton = new JButton("Checkout");
        JButton cancelButton = new JButton("Clear");

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCustomerInfoDialog();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOrderSummary();
            }
        });

        checkoutPanel.add(checkoutButton);
        checkoutPanel.add(cancelButton);
        frame.add(checkoutPanel);
        
        frame.setVisible(true);

        frame.setVisible(true);

        //closing the connection
        try {
            conn.close();
            JOptionPane.showMessageDialog(null,"Connection Closed.");
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
        }
    }

    private static JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 1));

        ArrayList<MenuItem> menuItems = getMenuItems();

        for (MenuItem menuItem : menuItems) {
            System.out.println(menuItem.getName());
            JButton itemButton = new JButton(menuItem.getName() + " - $" + menuItem.getPrice());
            itemButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Add the selected item to the order summary
                    orderSummary.add(menuItem);
                    updateOrderSummary();
                }
            });
            menuPanel.add(itemButton);
        }

        return menuPanel;
    }

    private static JPanel createOrderSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private static void updateOrderSummary() {
        orderSummaryPanel.removeAll(); // Clear the order summary panel

        // Calculate and display the total
        double total = orderSummary.stream().mapToDouble(MenuItem::getPrice).sum();
        DecimalFormat df = new DecimalFormat("#.00");
        totalLabel.setText("Total: $" + df.format(total));

        
        for (MenuItem menuItem : orderSummary) {
            
            JPanel itemPanel = new JPanel();
            JButton removeButton = new JButton("X");
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Remove the selected item from the order summary
                    orderSummary.remove(menuItem);
                    updateOrderSummary();
                }
            });
            JLabel itemLabel = new JLabel(menuItem.getName() + " - $" + menuItem.getPrice());
            itemPanel.add(removeButton);
            itemPanel.add(itemLabel);
            orderSummaryPanel.add(itemPanel);
            
        }

        if (orderSummary.isEmpty()) {
            orderSummaryPanel.add(new JLabel("Select an item to add to the order"));
        }
        
        
        orderSummaryPanel.revalidate(); // Refresh the order summary panel
        orderSummaryPanel.repaint();
        
    }

    private static void showCustomerInfoDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Customer Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Customer Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String customerName = nameField.getText();
            String customerEmail = emailField.getText();
            // System.out.println(customerName + " " + customerEmail);

            if (!customerName.isEmpty() && !customerEmail.isEmpty()) {
                Customer customer = new Customer(customerName, customerEmail);
                CashierCalls.submitOrder(orderSummary, customer);
                clearOrderSummary();
            } else {
                JOptionPane.showMessageDialog(null, "Please enter customer name and email.");
            }
        }
    }

    /* Want to implement this later for customizing menu items */
    // private static void showIngredientSelectionDialog(MenuItem menuItem) {
    //     JDialog dialog = new JDialog();
    //     dialog.setTitle("Select Ingredients");

    //     JPanel ingredientPanel = new JPanel();
    //     ingredientPanel.setLayout(new BoxLayout(ingredientPanel, BoxLayout.Y_AXIS));

    //     // Fetch ingredients for the selected menu item from the database
    //     List<Ingredient> ingredients = getIngredientsForMenuItem(menuItem);

    //     for (Ingredient ingredient : ingredients) {
    //         JCheckBox checkbox = new JCheckBox(ingredient.getName(), true); // Selected by default
    //         ingredientPanel.add(checkbox);
    //     }

    //     JButton addToOrderButton = new JButton("Add to Order");
    //     addToOrderButton.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             // Process selected ingredients and add the customized item to the order summary
    //             // You can implement this logic here
    //             dialog.dispose();
    //         }
    //     });

    //     JButton cancelButton = new JButton("Cancel Item");
    //     cancelButton.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             dialog.dispose();
    //         }
    //     });

    //     dialog.add(ingredientPanel, BorderLayout.CENTER);
    //     dialog.add(addToOrderButton, BorderLayout.SOUTH);
    //     dialog.add(cancelButton, BorderLayout.SOUTH);
    //     dialog.pack();
    //     dialog.setVisible(true);
    // }

    private static ArrayList<MenuItem> getMenuItems() {
        return CashierCalls.getMenuItems();
    }

    private static void clearOrderSummary() {
        orderSummary.clear();
        updateOrderSummary();
    }
}