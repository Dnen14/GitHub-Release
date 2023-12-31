import java.sql.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * The Manager_GUI class represents the graphical user interface for the Manager's view
 * in the Point of Sale (POS) System.
 *
 * This class provides functionalities for the Manager, including the display of the Manager POS System
 * and authentication using the `managerSecurity` method.
 *
 * @author Rahul Singh
 * @author Brandon Thomas
 * @author Zak Borman
 * @author Abhinav Nallam
 * @version 3.0
 * @since Oct 3, 2023
 */
public class Manager_GUI extends JFrame {
    private static String dbURL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db";
    private static String username = "csce315_909_rahul_2003";
    private static String password = "Rs03252003#";

    private static Connection conn = null;
    private static JFrame cashierFrame;
    private static JFrame managerFrame;

    public static SQLCalls database = new SQLCalls();
    public static Object[][] inventory = new Object[0][0];
    public static String[] columnNames = new String[0];
    public static String[] ingredients = new String[0];
    public static String[] menuItems = new String[0];
    public static ArrayList<String> orderIDs = new ArrayList<String>();
    public static ArrayList<String> excessReportList = new ArrayList<String>();

    /**
     * The main entry point of the program.
     * @author Rahul Singh
     * @author Brandon Thomas
     * @author Zak Borman
     * @author Abhinav Nallam
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        managerSecurity();
    }

    /**
     * Displays a login panel to authenticate the user before granting access to the Manager POS System.
     * @author Rahul Singh
     * @return True if the user successfully authenticated and gained access to the Manager POS System, false otherwise.
     */
    public static boolean managerSecurity() {
        while (true) {
            JPanel panel = new JPanel();
            JTextField nameField = new JTextField(10);
            JPasswordField pinField = new JPasswordField(10);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("PIN:"));
            panel.add(pinField);
    
            int result = JOptionPane.showConfirmDialog(null, panel, "Enter Credentials", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String inputName = nameField.getText();
                String inputPin = new String(pinField.getPassword());
    
                if (verifyCredentials(inputName, inputPin)) {
                    // Credentials are valid, create the Manager POS System frame
                    managerFrame = createManagerFrame();
                    managerFrame.setVisible(true);
                    return true; // Exit the loop
                } else {
                    // Credentials are invalid, display an error message
                    JOptionPane.showMessageDialog(null, "Invalid credentials. Try again.");
                }
            } else {
                // The user canceled or closed the dialog, exit the loop
                JOptionPane.showMessageDialog(null, "Access denied.");
                return false; // Exit the loop
            }
        }
    }

    /**
     * Verifies user credentials against a database.
     *
     * @author Rahul Singh
     * @param name The name provided by the user.
     * @param pin The PIN provided by the user.
     * @return True if the provided credentials match a record in the database, false otherwise.
     */
    public static boolean verifyCredentials(String name, String pin) {
        // Verify credentials against database
        try {
            Connection conn = DriverManager.getConnection(dbURL, username, password);
            String query = "SELECT * FROM credentials WHERE name = ? AND pin = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, pin);
    
            ResultSet resultSet = pstmt.executeQuery();
    
            if (resultSet.next()) {
                // Credentials are valid
                conn.close();
                return true;
            }
    
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return false;
    }

    /**
     * Refreshes the ingredient table when editing inventory.
     * 
     * @author Zak Borman
     * @param stmt A Statement object for executing SQL queries.
     * @param inventoryTableModel Model of the table containing current inventory.
     */
    public static void refreshInventory(Statement stmt, DefaultTableModel inventoryModel) {
        columnNames = database.getColumnNames(stmt, "ingredient");
        inventory = database.ViewTable(stmt, columnNames, "ingredient").stream().map(x -> x.toArray(new String[0])).toArray(String[][]::new);
        
        inventoryModel.setDataVector(inventory, columnNames);
    }

    /**
     * Refreshes the menu item list and ingredient list when editing menu.
     * 
     * @author Zak Borman
     * @param stmt A Statement object for executing SQL queries.
     * @param menuListModel Model of the list containing current menu items.
     * @param ingredientList Model of the list containing checkboxes for selected ingredients.
     */
    public static void refreshMenu(Statement stmt, DefaultListModel<String> menuListModel, DefaultListModel<JCheckBox> ingredientListModel) {
        menuItems = database.getSpecifiedTableValues(stmt, "menu_item", "name");
        ingredients = database.getSpecifiedTableValues(stmt, "ingredient", "name");
        
        // Update the model of the menuList
        menuListModel.clear();
        for (String item : menuItems) {
            menuListModel.addElement(item);
        }

        // Clear and update the ingredientList
        ingredientListModel.clear();
        for (String i : ingredients) {
            JCheckBox ingredient = new JCheckBox(i);
            ingredientListModel.addElement(ingredient);
        }
    }

    /**
     * Creates and returns a new JFrame for the Manager's view in the Point of Sale (POS) System.
     * This JFrame serves as the graphical user interface for the Manager, providing access to the Manager's functionalities.
     * 
     * @author Rahul Singh
     * @author Brandon Thomas
     * @author Zak Borman
     * @author Abhinav Nallam
     * @return A new instance of a JFrame to switch to the Cashier's view.
     * @see Cashier_GUI#createCashierFrame()
     */
    public static JFrame createManagerFrame() {    

        //Building the connection
        try {
            conn = DriverManager.getConnection(dbURL, username, password);
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        //System.out.println("Opened database successfully");
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            columnNames = database.getColumnNames(stmt, "ingredient");
            inventory = database.ViewTable(stmt, columnNames, "ingredient").stream().map(x -> x.toArray(new String[0])).toArray(String[][]::new);
            menuItems = database.getSpecifiedTableValues(stmt, "menu_item", "name");
            ingredients = database.getSpecifiedTableValues(stmt, "ingredient", "name");
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error accessing Database 1.");
        }
        

        //closing the connection
        try {
            conn.close();
            //System.out.println("Connection Closed.");
        } 
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
        }

        // GUI Window
        JFrame frame = new JFrame("Manager POS System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        JPanel inventoryPanel = new JPanel();
        DefaultTableModel inventoryModel = new DefaultTableModel(inventory, columnNames);
        JTable inventoryTable = new JTable(inventoryModel);
        JScrollPane inventoryPane = new JScrollPane(inventoryTable);
        inventoryPanel.add(inventoryPane);

        JTextField ingredientField = new JTextField(20);
        JTextField quantityField = new JTextField(5);
        JTextField restockPriceField = new JTextField(5);

        JPanel ingredientButtonPanel = new JPanel();
        ingredientButtonPanel.setLayout(new BoxLayout(ingredientButtonPanel, BoxLayout.Y_AXIS));
        JButton restockButton = new JButton("Restock");
        JButton addIngredientButton = new JButton("Add Ingredient");
        JButton updateIngredientButton = new JButton("Update Ingredient");
        JButton deleteIngredientButton = new JButton("Delete Ingredient");

        restockButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addIngredientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateIngredientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteIngredientButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        ingredientButtonPanel.add(restockButton);
        ingredientButtonPanel.add(Box.createVerticalStrut(10));
        ingredientButtonPanel.add(addIngredientButton);
        ingredientButtonPanel.add(Box.createVerticalStrut(10));
        ingredientButtonPanel.add(updateIngredientButton);
        ingredientButtonPanel.add(Box.createVerticalStrut(10));
        ingredientButtonPanel.add(deleteIngredientButton);

        inventoryPanel.add(new JLabel("Ingredient: "));
        inventoryPanel.add(ingredientField);
        inventoryPanel.add(new JLabel("Quantity: "));
        inventoryPanel.add(quantityField);
        inventoryPanel.add(new JLabel("Restock Price: "));
        inventoryPanel.add(restockPriceField);
        inventoryPanel.add(ingredientButtonPanel);

        restockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent k) {
                // restock request
                // Update if ingredient already exists, else add new entry
                int originalQuantity = -1;
                for(int i = 0; i < inventory.length; i++){
                    for(int j = 0; j < inventory[i].length;j++){
                        if(inventory[i][j].equals(ingredientField.getText())){
                            originalQuantity = Integer.valueOf(String.valueOf(inventory[i][j-2]));
                            break;
                        }
                    }
                    if(originalQuantity != -1){
                        break;
                    }
                }

                int quantity = Integer.valueOf(quantityField.getText()) + originalQuantity;

                Connection connfunc = null;
                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }

                //System.out.println("Opened database successfully");
                
                try{
                    Statement stmt = connfunc.createStatement();
                    database.UpdateTable(stmt, quantity, "ingredient", "quantity", "name", ingredientField.getText());

                    refreshInventory(stmt, inventoryModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 2.");
                }

                try {
                    connfunc.close();
                    //System.out.println("Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });
        addIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent k) {
                // restock request
                // Update if ingredient already exists, else add new entry

                Connection connfunc = null;
                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }
                
                try{
                    Statement stmt = connfunc.createStatement();
                    Object[] values = {inventory.length + 2, quantityField.getText(), restockPriceField.getText(), ingredientField.getText()};
                    database.AddItem("ingredient", stmt, values);

                    refreshInventory(stmt, inventoryModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 2.");
                }

                try {
                    connfunc.close();
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });

        updateIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent k) {
                // restock request
                // Update if ingredient already exists, else add new entry

                Connection connfunc = null;
                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }
                
                try{
                    Statement stmt = connfunc.createStatement();
                    database.UpdateTable(stmt, quantityField.getText(), "ingredient", "quantity", "name", ingredientField.getText());
                    database.UpdateTable(stmt, restockPriceField.getText(), "ingredient", "restock_price", "name", ingredientField.getText());
                    
                    refreshInventory(stmt, inventoryModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 2.");
                }

                try {
                    connfunc.close();
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });

        deleteIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent k) {
                // restock request
                // Update if ingredient already exists, else add new entry

                Connection connfunc = null;
                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }
                
                try{
                    Statement stmt = connfunc.createStatement();
                    database.deleteItem(stmt, "ingredient", "name", "'" + ingredientField.getText() + "'");
                    refreshInventory(stmt, inventoryModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 2.");
                }

                try {
                    connfunc.close();
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });
        
        // Menu modification panel (lower row)
        JPanel menuPanel = new JPanel();

        JPanel menuButtonPanel = new JPanel();
        menuButtonPanel.setLayout(new BoxLayout(menuButtonPanel, BoxLayout.Y_AXIS));

        JList<String> menuList = new JList<>();
        DefaultListModel<String> menuListModel = new DefaultListModel<>();
        for (String i : menuItems) {
            menuListModel.addElement(i);
        }
        menuList.setModel(menuListModel);
        JScrollPane menuPane = new JScrollPane(menuList);
        
        JTextField menuItemField = new JTextField(20);
        JTextField priceField = new JTextField(5);

        JButton addItemButton = new JButton("Add");
        JButton updateItemButton = new JButton("Update");
        JButton deleteItemButton = new JButton("Delete");

        addItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        JList<JCheckBox> ingredientList = new JList<>();
        DefaultListModel<JCheckBox> ingredientListModel = new DefaultListModel<>();
        for (String i : ingredients) {
            JCheckBox ingredient = new JCheckBox(i);
            ingredientListModel.addElement(ingredient);
        }
        ingredientList.setModel(ingredientListModel);
        ingredientList.setCellRenderer(new CheckBoxListCellRenderer());
        JScrollPane ingredientPane = new JScrollPane(ingredientList);

        menuList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent j) {
                if (!j.getValueIsAdjusting() && !menuList.isSelectionEmpty()) {
                    // TODO: Get data of item selected in list
                    // fill menuItemField, priceField and select
                    // checkboxes of ingredients in item
                    Connection connfunc = null;
                    String priceValue = "";
                    String menuID = "";
                    String[] selectedIngredientsID = new String[0];
                    String[] selectedIngredients = new String[0];
                    
                    try {
                        connfunc = DriverManager.getConnection(dbURL, username, password);
                    } 
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println(e.getClass().getName()+": "+e.getMessage());
                        System.exit(0);
                    }

                    //System.out.println("Opened database successfully");

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
                        JOptionPane.showMessageDialog(null,"Error accessing Database 3.");
                    }

                    try {
                        connfunc.close();
                        //System.out.println("Connection Closed.");
                    } 
                    catch (Exception e) {
                        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                    }

                    String SelectedValue = (String) menuList.getSelectedValue();
                    menuItemField.setText(SelectedValue); 
                    priceField.setText(priceValue);

                    for(int i = 0; i < ingredients.length; i++){
                        ingredientListModel.getElementAt(i).setSelected(false);
                    }

                    for(int i = 0; i < ingredients.length; i++){
                        for(int k = 0; k < selectedIngredients.length; k++){
                            if(ingredientListModel.getElementAt(i).getText().equals(selectedIngredients[k])){
                                ingredientListModel.getElementAt(i).setSelected(true);
                            }
                        }
                    }

                    ingredientList.setModel(ingredientListModel);
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
                // add item to menu_item
                Connection connfunc = null;
                ArrayList<String> selectedCheckboxes = new ArrayList<String>();
                Object[] values;
                Object[] selectedCheckboxesIDs = new Object[0];

                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }

                //System.out.println("Opened database successfully");

                try{
                    Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    for(int i = 0; i < ingredients.length; i++){
                        if(ingredientListModel.getElementAt(i).isSelected() == true){
                            selectedCheckboxes.add((String) ingredientListModel.getElementAt(i).getText());
                        }
                    }
                    selectedCheckboxesIDs = new Object[selectedCheckboxes.size()];
                    for(int i = 0; i < selectedCheckboxes.size(); i++){
                        selectedCheckboxesIDs[i] = database.getOneTableValue(stmt, "ingredient", "id", "name", selectedCheckboxes.get(i));
                    }

                    values = new Object[3];
                    values[0] = menuItems.length + 2;
                    values[1] = priceField.getText();
                    values[2] = menuItemField.getText();

                    database.AddItem("menu_item", stmt, values);

                    String menuItemID = database.getOneTableValue(stmt, "menu_item", "id", "name", menuItemField.getText());

                    for(int i = 0; i < selectedCheckboxesIDs.length; i++){
                        Object[] inputVals = {CashierCalls.getNextMenuItemIngredientJoinId(), selectedCheckboxesIDs[i], menuItemID};
                        database.AddItem("ingredient_menu_item_join_table", stmt, inputVals);
                    }

                    refreshMenu(stmt, menuListModel, ingredientListModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 4.");
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }

                try {
                    connfunc.close();
                    //System.out.println("Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });

        updateItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent j) {
                // TODO: update item of same name in menu_item
                Connection connfunc = null;
                String selectedCheckboxes = new String();
                Object selectedCheckboxesIDs = new Object();

                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }

                //System.out.println("Opened database successfully");

                try{
                    Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    database.UpdateTable(stmt,(Object) priceField.getText(), "menu_item", "price", "name", menuList.getSelectedValue());
                    String selectedMenuItemID = database.getOneTableValue(stmt, "menu_item", "id", "name", menuList.getSelectedValue());
                    for(int i = 0; i < ingredients.length; i++){
                        String selectedIngredientItemID = database.getOneTableValue(stmt, "ingredient", "id", "name", ingredients[i]);
                        String condition = "menu_item_id = " + selectedMenuItemID + " AND ingredient_id = " + selectedIngredientItemID;

                        if(ingredientListModel.getElementAt(i).isSelected() == true && "FALSE".equals(database.checkIfValueExists(stmt, "ingredient_menu_item_join_table", "join_id", "join_id",condition))){
                            selectedCheckboxes = (String) ingredientListModel.getElementAt(i).getText();
                            selectedCheckboxesIDs = database.getOneTableValue(stmt, "ingredient", "id", "name", selectedCheckboxes);
                            
                            Object[] inputVals = {CashierCalls.getNextMenuItemIngredientJoinId(), selectedCheckboxesIDs, selectedMenuItemID};
                            database.AddItem("ingredient_menu_item_join_table", stmt, inputVals);
                        }
                        else if(ingredientListModel.getElementAt(i).isSelected() == false && "TRUE".equals(database.checkIfValueExists(stmt, "ingredient_menu_item_join_table", "join_id", "join_id",condition))){
                            selectedCheckboxes = (String) ingredientListModel.getElementAt(i).getText();
                            selectedCheckboxesIDs = database.getOneTableValue(stmt, "ingredient", "id", "name", selectedCheckboxes);

                            try {
                                stmt.executeUpdate("DELETE FROM ingredient_menu_item_join_table WHERE ingredient_id = " + selectedCheckboxesIDs + " AND menu_item_id = " + selectedMenuItemID);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                System.err.println(e.getClass().getName()+": "+e.getMessage());
                                System.exit(0);
                            }
                        }
                    }

                    refreshMenu(stmt, menuListModel, ingredientListModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 5.");
                }

                try {
                    connfunc.close();
                    //System.out.println("Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });

        deleteItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent j) {
                // TODO: remove item from menu_item
                Connection connfunc = null;
                String[] selectedIDs = new String[0];

                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }

                //System.out.println("Opened database successfully");

                try{
                    Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                    String menuItemID = database.getOneTableValue(stmt, "menu_item", "id", "name", (String) menuList.getSelectedValue());
                    database.deleteItem(stmt, "ingredient_menu_item_join_table", "menu_item_id", menuItemID);
                    database.deleteItem(stmt, "menu_item", "id", menuItemID);
                    menuListModel.removeElement(menuList.getSelectedValue());
                    menuList.clearSelection();
                    refreshMenu(stmt, menuListModel, ingredientListModel);
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 6.");
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                }

                try {
                    connfunc.close();
                    //System.out.println("Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
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

        // Report Panel
        JPanel reportPanel = new JPanel();
        
        JPanel salesReportPanel = new JPanel();
        salesReportPanel.setLayout(new BoxLayout(salesReportPanel, BoxLayout.Y_AXIS));
        JTextField salesReportTime1 = new JTextField(5);
        JTextField salesReportTime2 = new JTextField(5);
        JButton salesReportButton = new JButton("Sales Report");

        JPanel excessReportPanel = new JPanel();
        excessReportPanel.setLayout(new BoxLayout(excessReportPanel, BoxLayout.Y_AXIS));
        JTextField excessReportTime = new JTextField(5);
        JButton excessReportButton = new JButton("Excess Report");

        JButton restockReportButton = new JButton("Restock Report");

        salesReportButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent j) {
                // Given a time window, display the sales by menu item from the order history
                Connection connfunc = null;

                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }

                //System.out.println("Opened database successfully");

                try{

                    Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                    // Get the start and end times from the text fields
                    String startTime = salesReportTime1.getText();
                    String endTime = salesReportTime2.getText();

                    // Query to retrieve sales data within the specified time window
                    ArrayList<String> salesData = database.getSalesByMenuItem(stmt, startTime, endTime);

                    // Create a map to store the total sales for each menu item
                    Map<String, Double> salesByMenuItem = new HashMap<>();

                    for (String menuItemName : salesData) {
                        double price = Double.valueOf(database.getOneTableValue(stmt, "menu_item", "price", "name", menuItemName));
                        salesByMenuItem.put(menuItemName, salesByMenuItem.getOrDefault(menuItemName, 0.0) + price);
                    }

                    // TODO: print all items in salesByMenuItem (for debugging):
                    // System.out.println("Items in salesByMenuItem:");
                    // for (Map.Entry<String, Double> entry : salesByMenuItem.entrySet()) {
                    //     System.out.println(entry.getKey() + ": " + entry.getValue());
                    // }


                    // Create a pop-up window to display the sales report
                    JFrame popupFrame = new JFrame("Sales Report in between " + startTime + " and " + endTime);
                    popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    JPanel popupPanel = new JPanel(new BorderLayout());

                    // Create a JTable to display the results
                    DefaultTableModel model = new DefaultTableModel(salesByMenuItem.size(), 3);
                    model.setColumnIdentifiers(new String[]{"Menu Item", "Total Sales Revenue", "Total Quantity"});

                    // Create a DecimalFormat to format the total sales to two decimal places
                    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

                    int row = 0;
                    for (Map.Entry<String, Double> entry : salesByMenuItem.entrySet()) {
                        String menuItemName = entry.getKey();
                        double totalSales = entry.getValue();

                        // Get the price for the current menu item
                        double price = Double.valueOf(database.getOneTableValue(stmt, "menu_item", "price", "name", menuItemName));

                        // Calculate the quantity sold by dividing total sales by price
                        int quantity = (int) (totalSales / price);

                        model.setValueAt(menuItemName, row, 0);
                        model.setValueAt(decimalFormat.format(totalSales), row, 1);
                        model.setValueAt(quantity, row, 2);

                        row++;
                    }

                    JTable table = new JTable(model);
                    JScrollPane scrollPane = new JScrollPane(table);
                    popupPanel.add(scrollPane, BorderLayout.CENTER);

                    JOptionPane.showConfirmDialog(null, popupPanel, "Sales Report", JOptionPane.DEFAULT_OPTION);
                }
                catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Error accessing Database 6.");
                }

                try {
                    connfunc.close();
                    //System.out.println("Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
            }
        });

        excessReportButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent q) {
                // Given a timestamp, display the list of inventory items that only sold less 
                // than 10% of their inventory between the timestamp and the current time, 
                // assuming no restocks have happened during the window

                Connection connfunc = null;
                HashMap<String, BigDecimal> currentIngredientCountMap = new HashMap<String, BigDecimal>();
                HashMap<String, BigDecimal> ingredientsUsedMap = new HashMap<String, BigDecimal>();

                try {
                    connfunc = DriverManager.getConnection(dbURL, username, password);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getClass().getName()+": "+e.getMessage());
                    System.exit(0);
                }

                //System.out.println("Opened database successfully");

                try{
                    Statement stmt = connfunc.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    String timestamp = excessReportTime.getText().toString();
                    orderIDs = database.ordersInRange(stmt, timestamp, Timestamp.from(Instant.now()).toString());

                    ArrayList<ArrayList<String>> menuIDs = new ArrayList<ArrayList<String>>();
                    for(int i = 0; i < orderIDs.size(); i++){
                        menuIDs.add(new ArrayList<String>(Arrays.asList(database.getMultipleTableValues(stmt, "menu_item_order_join_table", "menuitemid", "orderid", orderIDs.get(i)))));
                    }

                    for(int i = 0; i < ingredients.length; i++){
                        ingredientsUsedMap.put(ingredients[i], new BigDecimal("0.00"));
                    }

                    for(int i = 0; i < menuIDs.size(); i++){
                        for(int j = 0; j < menuIDs.get(i).size(); j++){
                            ArrayList<String> ingredientIDs = new ArrayList<String>();
                            //priceField.setText(menuIDs.get(i).get(j));
                            //priceField.setText(Integer.toString(menuIDs.get(i).size()));
                            ingredientList.setModel(ingredientListModel);
                            ingredientList.setCellRenderer(new CheckBoxListCellRenderer());
                            String[] strings = database.getMultipleTableValues(stmt, "ingredient_menu_item_join_table", "ingredient_id", "menu_item_id", menuIDs.get(i).get(j));
                            ingredientIDs = (new ArrayList<String>(Arrays.asList(strings)));
                            for(int k = 0; k < ingredientIDs.size(); k++){
                                String string = database.getQuantityOfIngredientsInMenuItem(stmt, ingredientIDs.get(k), menuIDs.get(i).get(j));
                                BigDecimal quantityOfIngredientUsed = new BigDecimal(string);
                                String ingredientName = database.getOneTableValue(stmt, "ingredient", "name", "id", ingredientIDs.get(k));
                                ingredientsUsedMap.replace(ingredientName, ingredientsUsedMap.get(ingredientName).add(quantityOfIngredientUsed));
                            }
                        }
                    }

                    for(int i = 0; i < ingredients.length; i++){
                        String string = database.getOneTableValue(stmt, "ingredient", "quantity", "name", ingredients[i]);
                        BigDecimal currentInventoryValue = new BigDecimal(string);
                        currentIngredientCountMap.put(ingredients[i], currentInventoryValue);
                        if(currentIngredientCountMap.get(ingredients[i]).doubleValue() > (ingredientsUsedMap.get(ingredients[i]).add(currentIngredientCountMap.get(ingredients[i]))).multiply(new BigDecimal("0.9")).doubleValue()){
                            excessReportList.add(ingredients[i]);
                        }
                    } 
                    
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 7.");
                }

                try {
                    connfunc.close();
                    //System.out.println("Connection Closed.");
                } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
                }
                
                JPanel panel = new JPanel();

                DefaultTableModel menuModel = new DefaultTableModel(excessReportList.size(), 4);
                menuModel.setColumnIdentifiers(new String[]{"Ingredient", "Amount Sold", "Inventory at Timestamp", "% Sold"});
                
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                for (int i = 0; i < excessReportList.size(); ++i) {
                    String ingredient = excessReportList.get(i);
                    int sold = ingredientsUsedMap.get(ingredient).intValue();
                    int oldInventory = currentIngredientCountMap.get(ingredient).intValue() + sold;
                    double percentSold = (double) sold / oldInventory * 100;
                    
                    menuModel.setValueAt(ingredient, i, 0);
                    menuModel.setValueAt(sold, i, 1);
                    menuModel.setValueAt(oldInventory, i, 2);
                    menuModel.setValueAt(decimalFormat.format(percentSold), i, 3);
                }

                JTable excessTable = new JTable(menuModel);
                JScrollPane excessPane = new JScrollPane(excessTable);
                panel.add(excessPane);

                JOptionPane.showConfirmDialog(null, panel, "Excess Report", JOptionPane.DEFAULT_OPTION);
            }
        });

        restockReportButton.addActionListener(new ActionListener () {
            /*
                displays all of the ingredients that are below the restock report
                @author Brandon Thomas
            */
            public void actionPerformed(ActionEvent event) {
                // Display the list of inventory items whose current inventory is less than 
                // the inventory item's minimum amount to have around before needing to restock
                ArrayList<Ingredient> ings = CashierCalls.getUnderStockedIngredients();
                Object[][] obj_arr = new Object[ings.size()][5];
                for(int i = 0; i < ings.size(); i++){
                    for(int j = 0; j < 5; j++){
                        switch (j){
                            case 0:
                                obj_arr[i][j] = ings.get(i).getId();
                                break;
                            case 1:
                                obj_arr[i][j] = ings.get(i).getName();
                                break;
                            case 2:
                                obj_arr[i][j] = ings.get(i).getQuantity();
                                break;
                            case 3:
                                obj_arr[i][j] = ings.get(i).getRestockPrice();
                                break;
                            case 4:
                                obj_arr[i][j] = ings.get(i).getThreshold();
                                break;
                        }
                    }
                }

                JPanel inventoryReportPanel = new JPanel();
                DefaultTableModel inventoryReportModel = new DefaultTableModel(obj_arr, new String[]{"id","Name","Quantity","Restock Price","Threshold"});
                JTable inventoryReportTable = new JTable(inventoryReportModel);
                JScrollPane inventoryReportPane = new JScrollPane(inventoryReportTable);
                inventoryReportPanel.add(inventoryReportPane);
                JOptionPane.showConfirmDialog(null, inventoryReportPanel, "Restock Report", JOptionPane.DEFAULT_OPTION);

            }
        });

        salesReportPanel.add(new JLabel("Start"));
        salesReportPanel.add(salesReportTime1);
        salesReportPanel.add(Box.createVerticalStrut(10));
        salesReportPanel.add(new JLabel("End"));
        salesReportPanel.add(salesReportTime2);
        salesReportPanel.add(Box.createVerticalStrut(10));
        salesReportPanel.add(salesReportButton);

        excessReportPanel.add(new JLabel("Start"));
        excessReportPanel.add(excessReportTime);
        excessReportPanel.add(Box.createVerticalStrut(10));
        excessReportPanel.add(excessReportButton);

        reportPanel.add(salesReportPanel);
        reportPanel.add(Box.createHorizontalStrut(10));
        reportPanel.add(excessReportPanel);
        reportPanel.add(Box.createHorizontalStrut(10));
        reportPanel.add(restockReportButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
       
        mainPanel.add(inventoryPanel);
        mainPanel.add(menuPanel);
        mainPanel.add(reportPanel);

        frame.add(mainPanel);

        // Add a button to switch to the DB GUI
        JButton switchToCashierGUIButton = new JButton("Switch to Cashier View");
        switchToCashierGUIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if (cashierFrame == null) {
                    
                // }
                cashierFrame = Cashier_GUI.createCashierFrame();
                frame.dispose(); // Close the current frame
                cashierFrame.setVisible(true); // Show the DB GUI frame
            }
        });

        frame.add(switchToCashierGUIButton, BorderLayout.NORTH);

        return frame;
    }
}

/**
 * The CheckBoxListCellRenderer class extends JCheckBox and implements the ListCellRenderer interface
 * to provide a custom rendering component for JLists with checkboxes.
 *
 * This class allows rendering JCheckBoxes within a JList and customizes their appearance and behavior.
 * This class is used in the Manager_GUI class to render the checkboxes for the ingredients in the menu item
 * 
 * @author Zak Borman
 * @version 1.0
 * @since Oct 3, 2023
 */
class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer<JCheckBox> {
    /**
     * Returns a component that displays the specified value as a checkbox within a JList cell.
     *
     * @author Zak
     * @param list The JList that is requesting the rendering.
     * @param value The value to be rendered as a checkbox.
     * @param index The cell index being rendered.
     * @param isSelected True if the cell is selected; otherwise, false.
     * @param cellHasFocus True if the cell has focus; otherwise, false.
     * @return The JCheckBox component representing the specified value for rendering.
     */
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
