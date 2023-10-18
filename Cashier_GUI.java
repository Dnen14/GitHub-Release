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

/**
 * The Cashier_GUI class represents the graphical user interface for a cashier in a point-of-sale system.
 *
 * This class allows cashiers to create and manage customer orders, select menu items, and proceed with checkout.
 * It also provides the functionality to switch to the manager view and submit orders to the database.
 *
 * @author Rahul Singh
 * @version 3.0
 * @since Oct 11, 2023
 */
public class Cashier_GUI extends JFrame {
    private static Connection conn = null;
    private static ArrayList<MenuItem> orderSummary = new ArrayList<>();
    private static JPanel orderSummaryPanel;
    private static JLabel totalLabel;
    private static JFrame cashierFrame;
    private static JFrame managerFrame;

    /**
     * Main method to launch the Cashier POS System GUI.
     *
     * Creates the Cashier POS System frame and makes it visible.
     * @author Rahul Singh
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Create the Cashier POS System frame
        cashierFrame = createCashierFrame();
        cashierFrame.setVisible(true);
    }

    /**
     * Creates the Cashier POS System frame.
     *
     * This method sets up the Cashier GUI frame, including order summary, menu items, total label,
     * and checkout buttons. It also handles the connection to the database and the switch to the Manager view.
     * @author Rahul Singh
     * @return A new instance of a JFrame to switch to the Manager's view.
     * @see Manager_GUI#createManagerFrame()
     */
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
                // Display the login panel and proceed if the credentials are valid
                if (Manager_GUI.managerSecurity()) {
                    frame.dispose(); // Close the current frame
                    //managerFrame.setVisible(true); // Show the Manager GUI frame
                }
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

    /**
     * Creates a panel displaying menu items as buttons for selection.
     *
     * This method constructs a JPanel that displays a grid of menu items as buttons for the user to select.
     * Each button represents a menu item with its name and price, and clicking a button adds the selected item
     * to the order summary.
     * 
     * @author Rahul Singh
     * @return A JPanel containing menu items displayed as buttons.
     */
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

    /**
     * Creates a panel to display the order summary and item selection instructions.
     *
     * This method constructs a JPanel that is used to display the current order summary and provides instructions
     * for selecting items to add to the order. It uses a vertical layout to stack components.
     *
     * @author Rahul Singh
     * @return A JPanel for displaying the order summary and item selection instructions.
     */
    private static JPanel createOrderSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Select an item to add to the order"));
        return panel;
    }

    /**
     * Creates a panel for checkout and cancellation options.
     *
     * This method constructs a JPanel that contains buttons for checkout and order clearance.
     * It provides event listeners for these buttons to trigger the display of a customer information dialog
     * or the clearing of the order summary.
     *
     * @author Rahul Singh
     * @return A JPanel containing checkout and cancellation buttons.
     */
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

    /**
     * Updates the order summary panel with the current list of selected items.
     *
     * This method refreshes the order summary panel by displaying the list of selected items, their total price,
     * and provides the ability to remove items from the order. It uses a scrollable list for item display.
     * 
     * @author Rahul Singh
     */
    private static void updateOrderSummary() { 
        orderSummaryPanel.removeAll(); // Clear the order summary panel

        // Create a JPanel to hold the items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        DefaultListModel<MenuItem> orderListModel = new DefaultListModel<>();
        JList<MenuItem> itemsList = new JList<>(orderListModel);
        itemsList.setCellRenderer(new MenuItemRenderer());

        double total = 0.0; // Initialize the total to 0

        for (MenuItem menuItem : orderSummary) {
            orderListModel.addElement(menuItem);
            total += menuItem.getPrice(); // Add the price of each item to the total
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

        totalLabel.setText("Total: $" + String.format("%.2f", total)); // Update the totalLabel
        orderSummaryPanel.revalidate(); // Refresh the order summary panel
        orderSummaryPanel.repaint();

    }

    /**
     * Displays a dialog for entering customer information before checkout.
     *
     * This method shows a dialog that allows the user to enter the customer's name and email address
     * before proceeding with the checkout process. If valid information is provided, it creates a new
     * Customer instance and submits the order. If information is missing, an error message is displayed.
     * 
     * @author Rahul Singh
     * 
     */
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
 
    /**
     * Retrieves the list of menu items from the database.
     *
     * This method fetches a list of menu items from a database or data source and returns them for display
     * in the Cashier GUI.
     * @author Rahul Singh
     * @return An ArrayList of menu items.
     */
    private static ArrayList<MenuItem> getMenuItems() {
        return CashierCalls.getMenuItems();
    }

    /**
     * Clears the order summary and updates the total label.
     *
     * This method clears the order summary and updates the total label to reflect the change.
     * It is used to clear the order summary after an order is submitted.
     * 
     * @author Rahul Singh
     */
    private static void clearOrderSummary() {
        orderSummary.clear();
        updateOrderSummary();
    }
}

/**
 * The MenuItemRenderer class represents a custom renderer for the menu items list.
 *
 * This class allows the menu items list to display the name and price of each menu item.
 * It is used in the Cashier_GUI class to display the menu items in the menu panel.
 * 
 * @author Rahul Singh
 * @version 1.0
 * @see Cashier_GUI#createMenuPanel()
 */
class MenuItemRenderer extends DefaultListCellRenderer {
    /**
     * Customizes the rendering of menu items in a list component.
     *
     * This method overrides the behavior of the DefaultListCellRenderer's rendering process
     * to display menu items with their names and prices.
     * 
     * @author Rahul Singh
     * @param list        The JList component that displays the items.
     * @param value       The value (menu item) to be rendered.
     * @param index       The index of the item in the list.
     * @param isSelected  True if the item is selected, false otherwise.
     * @param cellHasFocus True if the cell has focus, false otherwise.
     * @return The customized rendering component for the menu item.
     */
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