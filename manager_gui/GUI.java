import java.sql.*;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class GUI extends JFrame {
    public static void main(String[] args)
    {
        //Building the connection
        Connection conn = null;
        try {
        conn = DriverManager.getConnection(
            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
            "csce315_909_zakborman",
            "542618xrad");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null,"Opened database successfully");

        try {
            // back end
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }

        JFrame frame = new JFrame("DB GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        // sample data, replace with database table
        Object[][] data = {
            {"Tapioca Pearls", 16.62, 10},
            {"Brown Sugar", 8.00, 40},
            {"Straws", 12.59, 557}
        };

        String[] columnNames = {"Name", "Restock Price", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JTextField ingredientField = new JTextField(20);
        JTextField quantityField = new JTextField(5);
        JButton restockButton = new JButton("Submit Request");

        JPanel restockPanel = new JPanel();
        restockPanel.add(new JLabel("Ingredient: "));
        restockPanel.add(ingredientField);
        restockPanel.add(new JLabel("Quantity: "));
        restockPanel.add(quantityField);
        restockPanel.add(restockButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(restockPanel, BorderLayout.EAST);

        frame.add(mainPanel);
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
}
