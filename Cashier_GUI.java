import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import MenuItem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Cashier_GUI extends JFrame {
    public static void main(String[] args)
    {
        //Building the connection
        Connection conn = null;
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
        
        // Create menu items panel
        JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new GridLayout(0, 1));
    
            ArrayList<MenuItem> MenuItems = getMenuItems();
    
            for (MenuItem menuItem : MenuItems) {
                JButton itemButton = new JButton(menuItem.getName() + " - $" + menuItem.getPrice());
                itemButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Add the selected item to the order summary
                    }
                });
                menuPanel.add(itemButton);
            }
        // Add menu items buttons or list here
        
        // Create order summary panel
        JPanel orderSummaryPanel = new JPanel();
        // Add order summary components here
        
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

    private static ArrayList<MenuItem> getMenuItems() {
        return CashierCalls.getMenuItems();
    }
}