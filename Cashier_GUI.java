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
    private static JFrame cashierFrame;
    private static JFrame managerFrame;

    public static void main(String[] args) {
        // Create the Cashier POS System frame
        cashierFrame = createCashierFrame();
        cashierFrame.setVisible(true);
    }

    public static JFrame createCashierFrame() {

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
        // JOptionPane.showMessageDialog(null,"Opened database successfully");
        

        JFrame frame = new JFrame("Cashier POS System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        
        // Create order summary panel
        orderSummaryPanel = createOrderSummaryPanel();

        // Create menu items panel
        JPanel menuPanel = createMenuPanel();

        //Create panel for total
        totalLabel = new JLabel("Total: $0.00");

        //Arrange the panels
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(orderSummaryPanel);
        leftPanel.add(totalLabel, BorderLayout.SOUTH);

        frame.add(menuPanel, BorderLayout.EAST);
        frame.add(leftPanel, BorderLayout.CENTER);

        // Create a panel for checkout and cancel buttons
        JPanel checkoutPanel = createCheckoutPanel();
        frame.add(checkoutPanel, BorderLayout.SOUTH);

        // Add a button to switch to the DB GUI
        JButton switchToManagerGUIButton = new JButton("Switch to Manager View");
        switchToManagerGUIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (managerFrame == null) {
                    managerFrame = Manager_GUI.createManagerFrame();
                }
                frame.dispose(); // Close the current frame
                managerFrame.setVisible(true); // Show the DB GUI frame
            }
        });

        frame.add(switchToManagerGUIButton, BorderLayout.NORTH);

        //closing the connection
        try {
            conn.close();
            // JOptionPane.showMessageDialog(null,"Connection Closed.");
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
        }
        
        return frame;   
    }

    private static JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 3));

        ArrayList<MenuItem> menuItems = getMenuItems();

        for (MenuItem menuItem : menuItems) {
            // System.out.println(menuItem.getName());
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
        panel.add(new JLabel("Select an item to add to the order"));
        return panel;
    }

    private static JPanel createCheckoutPanel() {
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

        return checkoutPanel;
    }

    private static void updateOrderSummary() { 
        /*
        orderSummaryPanel.removeAll(); // Clear the order summary panel

        // Calculate and display the total
        double total = orderSummary.stream().mapToDouble(MenuItem::getPrice).sum();
        DecimalFormat df = new DecimalFormat("#.00");
        totalLabel.setText("Total: $" + df.format(total));

        // Create a JPanel to hold the items
         JPanel itemsPanel = new JPanel();
         itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        
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
            // itemPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            itemsPanel.add(itemPanel);
            
        }

        // Create a JScrollPane and add the itemsPanel to it
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        orderSummaryPanel.add(scrollPane);
        orderSummaryPanel.setBackground(new Color(200, 200, 200));

        if (orderSummary.isEmpty()) {
            JLabel label = new JLabel("<html><div style='text-align: center;'>Select an item to add to the order</div></html>");
            orderSummaryPanel.add(label);
        }
        
        
        orderSummaryPanel.revalidate(); // Refresh the order summary panel
        orderSummaryPanel.repaint();
        */
        
        orderSummaryPanel.removeAll(); // Clear the order summary panel

        // Create a JPanel to hold the items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        DefaultListModel<MenuItem> orderListModel = new DefaultListModel<>();
        JList<MenuItem> itemsList = new JList<>(orderListModel);
        itemsList.setCellRenderer(new MenuItemRenderer());

        for (MenuItem menuItem : orderSummary) {
            orderListModel.addElement(menuItem);
        }

        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsList.addListSelectionListener(e -> {
            int selectedIndex = itemsList.getSelectedIndex();
            if (selectedIndex >= 0) {
                orderListModel.remove(selectedIndex);
                orderSummary.remove(selectedIndex);
                updateOrderSummary();
            }
        });

        // Create a JScrollPane and add the itemsList to it
        JScrollPane scrollPane = new JScrollPane(itemsList);
        itemsPanel.add(scrollPane);

        orderSummaryPanel.add(itemsPanel);
        orderSummaryPanel.setBackground(new Color(200, 200, 200));

        if (orderSummary.isEmpty()) {
            JLabel label = new JLabel("<html><div style='text-align: center;'>Select an item to add to the order</div></html>");
            orderSummaryPanel.add(label);
        } else {
            JLabel label = new JLabel("<html><div style='text-align: center;'>Click on an item in the order to remove it</div></html>");
            orderSummaryPanel.add(label);
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

class MenuItemRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof MenuItem) {
            MenuItem menuItem = (MenuItem) value;
            setText(menuItem.getName() + " - $" + menuItem.getPrice());
        }
        return this;
    }
}