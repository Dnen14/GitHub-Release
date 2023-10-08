import java.sql.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Manager_GUI extends JFrame {
    public static Object[][] inventory;
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

        inventory = new Object[0][0];
        String[] columnNames = new String[0];
        SQLCalls database = new SQLCalls();
        String[] menuItems = new String[0];
        String[] ingredients = new String[0];
        try {
            // TODO: back end, specifics below
            Statement stmt = conn.createStatement();

            columnNames = database.getColumnNames(stmt, "ingredient");

            inventory = database.ViewTable(stmt, columnNames, "ingredient").stream().map(x -> x.toArray(new String[0])).toArray(String[][]::new);

            menuItems = database.getColumnNames(stmt, "menu_item");

            ingredients = database.getColumnNames(stmt, "ingredient");

            conn.close();
            JOptionPane.showMessageDialog(null,"Connection Closed.");
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }
        JFrame frame = new JFrame("DB GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        JPanel inventoryPanel = new JPanel();
        DefaultTableModel inventoryModel = new DefaultTableModel(inventory, columnNames);
        JTable inventoryTable = new JTable(inventoryModel);
        JScrollPane inventoryPane = new JScrollPane(inventoryTable);
        inventoryPanel.add(inventoryPane);

        JTextField ingredientField = new JTextField(20);
        JTextField quantityField = new JTextField(5);
        JButton restockButton = new JButton("Restock");

        inventoryPanel.add(new JLabel("Ingredient: "));
        inventoryPanel.add(ingredientField);
        inventoryPanel.add(new JLabel("Quantity: "));
        inventoryPanel.add(quantityField);
        inventoryPanel.add(restockButton);

        restockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent k) {
                // TODO: implement restock request
                // Update if ingredient already exists, else add new entry
                int originalQuantity = -1;
                for(int i = 0; i < inventory.length; i++){
                    for(int j = 0; j < inventory[i].length;i++){
                        if(inventory[i][j] == ingredientField.getText()){
                            originalQuantity = (int) inventory[i][j-2];
                            break;
                        }
                    }
                    if(originalQuantity != -1){
                        break;
                    }
                }
                
                int quantity = Integer.parseInt(ingredientField.getText()) + originalQuantity;
                Connection connfunc = null;
                try {
                connfunc = DriverManager.getConnection(
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
                try{
                    Statement stmt = connfunc.createStatement();
                    database.UpdateTable(stmt, quantityField.getText(), "ingredient", "quantity", "name", ingredientField.getText());
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database.");
                }
                try {
                    connfunc.close();
                    JOptionPane.showMessageDialog(null,"Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
                }
        });
        
        JPanel menuPanel = new JPanel();

        // TODO: replace with name column of menu_item table

        JList<String> menuList = new JList<>(menuItems);
        JScrollPane menuPane = new JScrollPane(menuList);
        menuPanel.add(menuPane);
        
        JTextField menuItemField = new JTextField(20);
        JTextField priceField = new JTextField(5);
        JButton itemButton = new JButton("Update Menu");

        // TODO: replace with name column of ingredient table
        

        JList<JCheckBox> ingredientList = new JList<>();
        DefaultListModel<JCheckBox> menuModel = new DefaultListModel<>();
        for (String i : ingredients) {
            JCheckBox ingredient = new JCheckBox(i);
            menuModel.addElement(ingredient);
        }
        ingredientList.setModel(menuModel);
        ingredientList.setCellRenderer(new CheckBoxListCellRenderer());
        JScrollPane ingredientPane = new JScrollPane(ingredientList);

        menuList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    // TODO: Get data of item selected in list
                    // fill menuItemField and priceField
                    // select checkboxes of ingredients already used    
                }
            }
        });

        ingredientList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                @SuppressWarnings("unchecked")
                JList<JCheckBox> list = (JList<JCheckBox>) event.getSource();
                int index = list.locationToIndex(event.getPoint());

                JCheckBox checkBox = list.getModel().getElementAt(index);
                checkBox.setSelected(!checkBox.isSelected());

                list.repaint();
            }
        });

        itemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: implement menu changes
                // Update if item already exists, else add new entry
            }
        });

        menuPanel.add(new JLabel("Item Name: "));
        menuPanel.add(menuItemField);
        menuPanel.add(ingredientPane);
        menuPanel.add(new JLabel("Price: "));
        menuPanel.add(priceField);
        menuPanel.add(itemButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
       
        mainPanel.add(inventoryPanel);
        mainPanel.add(menuPanel);

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

class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<JCheckBox> {
    public Component getListCellRendererComponent(JList<? extends JCheckBox> list, 
    JCheckBox value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setSelected(value.isSelected());
        setText(value.getText());
        setFont(list.getFont());
        setFocusPainted(false);
        return this;
    }
}
