import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Instant;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Manager_GUI extends JFrame {
    private static String dbURL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db";
    private static String username = "csce315_909_rahul_2003";
    private static String password = "Rs03252003#";

    private static Connection conn = null;
    private static JFrame cashierFrame;
    private static JFrame managerFrame;

    public static Object[][] inventory;
    public static String[] ingredients = new String[0];
    public static String[] menuItems = new String[0];
    public static ArrayList<String> orderIDs = new ArrayList<String>();
    public static ArrayList<String> excessReportList = new ArrayList<String>();


    public static void main(String[] args) {
        // Create the Manager POS System frame
        managerFrame = createManagerFrame();
        managerFrame.setVisible(true);    
    }

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

        inventory = new Object[0][0];
        String[] columnNames = new String[0];
        SQLCalls database = new SQLCalls();

        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            columnNames = database.getColumnNames(stmt, "ingredient");
            inventory = database.ViewTable(stmt, columnNames, "ingredient").stream().map(x -> x.toArray(new String[0])).toArray(String[][]::new);
            menuItems = database.getSpecifiedTableValues(stmt, "menu_item", "name");
            ingredients = database.getSpecifiedTableValues(stmt, "ingredient", "name");
            conn.close();
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
                        menuModel.getElementAt(i).setSelected(false);
                    }

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
                        if(menuModel.getElementAt(i).isSelected() == true){
                            selectedCheckboxes.add((String) menuModel.getElementAt(i).getText());
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
                    Random num = new Random();

                    database.AddItem("menu_item", stmt, values);

                    for(int i = 0; i < selectedCheckboxesIDs.length; i++){
                        Object[] inputVals = {num.nextInt(10000)+1, selectedCheckboxesIDs[i], menuItems.length};
                        database.AddItem("ingredient_menu_item_join_table", stmt, inputVals);
                    }
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 4.");
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

                        if(menuModel.getElementAt(i).isSelected() == true && "FALSE".equals(database.checkIfValueExists(stmt, "ingredient_menu_item_join_table", "id", "id",condition))){
                            priceField.setText(condition);
                            selectedCheckboxes = (String) menuModel.getElementAt(i).getText();
                            selectedCheckboxesIDs = database.getOneTableValue(stmt, "ingredient", "id", "name", selectedCheckboxes);
                            Random num = new Random();
                            
                            Object[] inputVals = {num.nextInt(10000)+1, selectedCheckboxesIDs, menuItems.length};
                            database.AddItem("ingredient_menu_item_join_table", stmt, inputVals);
                        }
                    }
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

                    int count = 0;
                    for(int i = 0; i < ingredients.length; i++){
                         if(menuModel.getElementAt(i).isSelected() == true){
                            count++;
                        }
                    }
                
                    selectedIDs = new String[count];
                    for(int i = 0; i < ingredients.length; i++){
                         if(menuModel.getElementAt(i).isSelected() == true){
                            selectedIDs[i] = database.getOneTableValue(stmt, "ingredient", "id", "name", menuModel.getElementAt(i).getText());
                        }
                    }

                    for(int i = 0; i < selectedIDs.length; i++){
                        database.deleteItem(stmt, "ingredient_menu_item_join_table", "ingredient_id",(String) ((String) selectedIDs[i] + " AND menu_item_id = " + (String) database.getOneTableValue(stmt, "menu_item", "id", "name",(String) menuList.getSelectedValue())));
                    }
                }
                catch(Exception e){
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
                }
                catch(Exception e){
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

                    HashMap<String, Integer> ingredientsUsedMap = new HashMap<String, Integer>();
                    for(int i = 0; i < ingredients.length; i++){
                        ingredientsUsedMap.put(ingredients[i], 0);
                    }

                    for(int i = 0; i < menuIDs.size(); i++){
                        for(int j = 0; j < menuIDs.get(i).size(); j++){
                            ArrayList<String> ingredientIDs = new ArrayList<String>();
                            ingredientIDs = (new ArrayList<String>(Arrays.asList(database.getMultipleTableValues(stmt, "ingredient_menu_item_join_table", "ingredient_id", "menu_item_id", menuIDs.get(i).get(j)))));
                            for(int k = 0; k < ingredientIDs.size(); k++){
                                int quantityOfIngredientUsed = Integer.valueOf(database.getQuantityOfIngredientsInMenuItem(stmt, menuIDs.get(i).get(j), ingredientIDs.get(k)));
                                ingredientsUsedMap.put(ingredientIDs.get(k), ingredientsUsedMap.get(ingredientIDs.get(k)) + quantityOfIngredientUsed);
                            }
                        }
                    }

                    HashMap<String, Integer> currentIngredientCountMap = new HashMap<String, Integer>();
                    for(int i = 0; i < ingredients.length; i++){
                        currentIngredientCountMap.put(ingredients[i], Integer.valueOf(database.getOneTableValue(stmt, "ingredient", "quantity", "name", ingredients[i])));
                        if(currentIngredientCountMap.get(ingredients[i]) > (ingredientsUsedMap.get(ingredients[i]) + currentIngredientCountMap.get(ingredients[i])) * .9){
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

                JList<String> excessJList = new JList<>();
                DefaultListModel<String> menuModel = new DefaultListModel<>();
                for (String ingredient : excessReportList) {
                    menuModel.addElement(ingredient);
                }
                excessJList.setModel(menuModel);
                JScrollPane excessPane = new JScrollPane(excessJList);
                
                panel.add(excessPane);

                JOptionPane.showConfirmDialog(null, panel, "Customer Information", JOptionPane.OK_CANCEL_OPTION);
            }
        });

        restockReportButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent j) {
                // Display the list of inventory items whose current inventory is less than 
                // the inventory item's minimum amount to have around before needing to restock
                
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
                }
                catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Error accessing Database 8.");
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
                if (cashierFrame == null) {
                    cashierFrame = Cashier_GUI.createCashierFrame();
                }
                frame.dispose(); // Close the current frame
                cashierFrame.setVisible(true); // Show the DB GUI frame
            }
        });

        frame.add(switchToCashierGUIButton, BorderLayout.NORTH);

        return frame;
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
