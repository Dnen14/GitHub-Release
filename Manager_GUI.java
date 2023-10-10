import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Manager_GUI extends JFrame {
    public static Object[][] inventory;
    public static String[] ingredients = new String[0];
    public static String[] menuItems = new String[0];
    public static void main(String[] args)
    {
        //Building the connection
        Connection conn = null;
        try {
        conn = DriverManager.getConnection(
            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
            "csce315_909_NETID",
            "PASSWORD");
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
        try {
            // TODO: back end, specifics below
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            columnNames = database.getColumnNames(stmt, "ingredient");

            inventory = database.ViewTable(stmt, columnNames, "ingredient").stream().map(x -> x.toArray(new String[0])).toArray(String[][]::new);

            menuItems = database.getSpecifiedTableValues(stmt, "menu_item", "name");

            ingredients = database.getSpecifiedTableValues(stmt, "ingredient", "name");

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

        JPanel menuButtonPanel = new JPanel();
        menuButtonPanel.setLayout(new BoxLayout(menuButtonPanel, BoxLayout.Y_AXIS));

        // TODO: replace with name column of menu_item table

        JList<String> menuList = new JList<>(menuItems);
        JScrollPane menuPane = new JScrollPane(menuList);
        
        JTextField menuItemField = new JTextField(20);
        JTextField priceField = new JTextField(5);

        JButton addItemButton = new JButton("Add");
        JButton updateItemButton = new JButton("Update");
        JButton deleteItemButton = new JButton("Delete");

        addItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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
            public void valueChanged(ListSelectionEvent j) {
                if (!j.getValueIsAdjusting()) {
                    // TODO: Get data of item selected in list
                    // fill menuItemField, priceField and select
                    // checkboxes of ingredients in item
                    Connection connfunc = null;
                    String priceValue = "";
                    String menuID = "";
                    String[] selectedIngredientsID = new String[0];
                    String[] selectedIngredients = new String[0];
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
                        Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        priceValue = database.getOneTableValue(stmt, "menu_item", "price", "name", (String) menuList.getSelectedValue());
                        menuID = database.getOneTableValue(stmt, "menu_item", "id", "name", (String) menuList.getSelectedValue());
                        selectedIngredientsID = database.getMultipleTableValues(stmt, "ingredient_menu_item_join_table", "ingredient_id", "menu_item_id", menuID);
                        selectedIngredients = new String[selectedIngredientsID.length];
                        for(int i = 0; i < selectedIngredientsID.length; i++){
                            selectedIngredients[i] = database.getOneTableValue(stmt, "ingredient", "name", "id", selectedIngredientsID[i]);
                        }
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

                    String SelectedValue = (String) menuList.getSelectedValue();
                    menuItemField.setText(SelectedValue); 
                    priceField.setText(priceValue);

                    int count = 0;
                    for(int i = 0; i < ingredients.length; i++){
                        for(int k = 0; k < selectedIngredients.length; k++){
                            if(menuModel.getElementAt(i).getText().equals(selectedIngredients[k])){
                                menuModel.getElementAt(i).setSelected(true);
                            }
                        }
                    }

                    ingredientList.setModel(menuModel);
                    ingredientList.setCellRenderer(new CheckBoxListCellRenderer());
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

        addItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent j) {
                // TODO: add item to menu_item
                String itemID = "";
                Connection connfunc = null;
                ArrayList<String> selectedCheckboxes = new ArrayList<String>();
                Object[] values;
                Object[] selectedCheckboxesIDs = new Object[0];
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
                    Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    String SelectedValue = (String) menuList.getSelectedValue();
                    for(int i = 0; i < ingredients.length; i++){
                        if(menuModel.getElementAt(i).isSelected() == true){
                            selectedCheckboxes.add((String) menuModel.getElementAt(i).getText());
                        }
                    }
                    selectedCheckboxesIDs = new Object[selectedCheckboxes.size()];
                    for(int i = 0; i < selectedCheckboxes.size(); i++){
                        selectedCheckboxesIDs[i] = database.getOneTableValue(stmt, "ingredient", "id", "name", selectedCheckboxes.get(i));
                    }

                    values = new Object[4];
                    values[0] = menuItems.length + 1;
                    values[1] = "medium";
                    values[2] = priceField.getText();
                    values[3] = menuItemField.getText();
                    Random num = new Random();

                    database.AddItem("menu_item", stmt, values);

                    for(int i = 0; i < selectedCheckboxesIDs.length; i++){
                        Object[] inputVals = {num.nextInt(10000)+1, selectedCheckboxesIDs[i], menuItems.length};
                        database.AddItem("ingredient_menu_item_join_table", stmt, inputVals);
                    }
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

        updateItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: update item of same name in menu_item
            }
        });

        deleteItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: remove item from menu_item
                
            }
        });

        menuPanel.add(menuPane);
        menuButtonPanel.add(addItemButton);
        menuButtonPanel.add(Box.createVerticalStrut(10)); 
        menuButtonPanel.add(updateItemButton);
        menuButtonPanel.add(Box.createVerticalStrut(10));
        menuButtonPanel.add(deleteItemButton);

        menuPanel.add(new JLabel("Item Name: "));
        menuPanel.add(menuItemField);
        menuPanel.add(ingredientPane);
        menuPanel.add(new JLabel("Price: "));
        menuPanel.add(priceField);
        menuPanel.add(menuButtonPanel);

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
