import java.sql.*;
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
        frame.setSize(800, 600);
        
        // Create order summary panel
        orderSummaryPanel = createOrderSummaryPanel();

        // Create menu items panel
        JPanel menuPanel = createMenuPanel();
        
        frame.add(menuPanel);
        frame.add(orderSummaryPanel);
        
        frame.setLayout(new GridLayout(1, 2)); // Arrange panels side by side
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

        for (MenuItem menuItem : orderSummary) {
            JLabel itemLabel = new JLabel(menuItem.getName() + " - $" + menuItem.getPrice());
            orderSummaryPanel.add(itemLabel);
        }

        orderSummaryPanel.revalidate(); // Refresh the order summary panel
        orderSummaryPanel.repaint();
    }

    private static ArrayList<MenuItem> getMenuItems() {
        return CashierCalls.getMenuItems();
    }
}