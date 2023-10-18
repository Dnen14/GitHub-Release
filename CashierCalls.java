import java.util.*;
import java.sql.*;
import java.time.*;

/**
 *   class that contains all of the necesarry function calls for the front end to populate the page
 *   @author Brandon Thomas
 *   @version 1.0
 *   @since 10/17/2023 
*/
public final class CashierCalls extends SQLCalls{
    
    /**
     *   submits an order to the SQL database and adds all of the proper join table items
     *   @author Brandon Thomas
     *   @param items - list of menu items in the order
     *   @param c - customer that placed the order
    */
    public static void submitOrder(ArrayList<MenuItem> items, Customer c){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
                "csce315_909_bat2492",
                "BT2415");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        try{
            // create a statement
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            SQLCalls call = new SQLCalls();
            long order_id = getNextOrderId();

            String ids = "";
            for(int i = 0 ; i < items.size(); i++){
                ids += items.get(i).getId();
                if(i != items.size()-1){
                    ids += ",";
                }
            }

            HashMap<Integer,Double> map = new HashMap<Integer,Double>();
            // get all of the ingredients needed for every menu item
            ResultSet rs = st.executeQuery("SELECT * FROM menu_item, ingredient_menu_item_join_table WHERE id=menu_item_id " +
                                           "AND id in (" + ids + ") ORDER BY id;");
            while(rs.next()){
                int ing_id = rs.getInt("ingredient_id");
                double quant = rs.getDouble("quantity");
                if(map.containsKey(ing_id)){
                    map.put(ing_id,map.get(ing_id) + quant);
                }
                else{
                    map.put(ing_id,quant);
                }
            }
            // check to make sure there are enough ingredients
            boolean enough_ing = true;
            rs = st.executeQuery("SELECT * FROM ingredient");
            while(rs.next()){
                int ing_id = rs.getInt("id");
                double ing_quant = rs.getDouble("quantity");
                if(!map.containsKey(ing_id)){
                    continue;
                }
                map.put(ing_id,ing_quant - map.get(ing_id));
                if(map.get(ing_id) < 0.0){
                    enough_ing = false;
                }
            }
            if(!enough_ing){
                throw new Exception("not enough ingredients to fufil order");
            }
            // if there are - subtract the ingredients from their respective stock
            for(Map.Entry<Integer,Double> ing: map.entrySet()){
                st.executeUpdate("UPDATE ingredient SET quantity =" + ing.getValue() + " WHERE id = " + ing.getKey());
            }

            //execute the query for the order table
            call.AddItem("order_table",st,new String[]{String.valueOf(order_id),String.valueOf(getTotal(items)),LocalDateTime.now().toString()});

            //fill the menu item order join table
            for(MenuItem item: items){
                call.AddItem("menu_item_order_join_table",st,new String[]{String.valueOf(getNextMenuOrderJoinId()),String.valueOf(item.getId()),String.valueOf(order_id)});
            }

            ArrayList<Customer> all_customers = getCustomers();
            boolean new_customer = true;
            //check to see if the customer is a new customer
            for(Customer cust: all_customers){
                if(cust.getName().equals(c.getName()) && 
                   cust.getEmail().equals(c.getEmail())){

                    c.setId(cust.getId());
                    new_customer = false;
                    break;
                }
            }

            // if the customer is new add them to the customer table
            if(new_customer){
                call.AddItem("customer",st,new String[]{String.valueOf(c.getId()),c.getName(),c.getEmail()});
            }

            // add the order customer relation to the join table
            call.AddItem("customer_order_join_table",st,new String[]{String.valueOf(getNextCustomerOrderJoinId()),String.valueOf(order_id),String.valueOf(c.getId())});   

            
        }
        catch (Exception e){
            System.out.println("DB Querry Failed");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of understocked ingredients from the inventory.
     *
     * This method scans the list of ingredients in the inventory and identifies those
     * that have quantities below their specified restocking threshold. It returns an
     * ArrayList of understocked ingredients.
     * 
     * @author Brandon Thomas
     * @return An ArrayList of Ingredient objects representing the understocked ingredients.
     */
    public static ArrayList<Ingredient> getUnderStockedIngredients(){
        ArrayList<Ingredient> understocked = new ArrayList<Ingredient>();
        ArrayList<Ingredient> ingredients = getIngredients();
        for(Ingredient ing: ingredients){
            if(ing.getQuantity() < ing.getThreshold()){
                understocked.add(ing);
            }
        }
        return understocked;
    }

    /**
     * gets the next available id in the order table of the database
     * 
     *  @author Brandon Thomas
     *  @return a long containing the next available id of an order
     */
    public static long getNextOrderId(){
        return getNextTableId("order_table");
    }

    /**
     * gets the next available id in the menu item table of the database
     * 
     *  @author Brandon Thomas
     *  @return long containing the next available id in the menu item order join table of the database
     */
    public static long getNextMenuOrderJoinId(){
        return getNextTableId("menu_item_order_join_table");
    }

    /**
     * gets the next available id in the customer table of the database
     * 
     *  @author Brandon Thomas
     *  @return long containing the next available id in the customer table of the database
     */
    public static long getNextCustomerId(){
        return getNextTableId("customer");
    }

    /**
     * gets the next available id in the customer order join table of the database
     * 
     *  @author Brandon Thomas
     *  @return long containing the next available id in the menu item order join table of the database
     */
    public static long getNextCustomerOrderJoinId(){
        return getNextTableId("customer_order_join_table");
    }

    /**
     * gets the next available id in the menu item ingredient join table of the database
     * 
     *  @author Brandon Thomas
     *  @return long containing the next available id
     */
    public static long getNextMenuItemIngredientJoinId(){
        return getNextTableId("ingredient_menu_item_join_table");
    }

    /**
        @author Brandon Thomas
        @param table - the table to get the next available id from
        @return a long containing the next available id from the table
    */
    public static long getNextTableId(String table){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
                "csce315_909_bat2492",
                "BT2415");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        long id = -1;
        try{
            // create statement
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // make the query
            String id_name = "id";
            if(table == "ingredient_menu_item_join_table"){
                id_name = "join_id";
            }
            ResultSet rs = st.executeQuery("SELECT MAX("+id_name+") as max_id FROM " + table);

            // get value from query
            if(rs.next()){
                id = Long.valueOf(rs.getString("max_id")).longValue();
            }
            
        }
        catch (Exception e){
            System.out.println("DB Querry Failed");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return id + 1;
    }

    /**
     * 
     * gets all of the menu items in the database
     * 
     * @author Brandon Thomas
     * @return all of the menu items in the database as an ArrayList<MenuItem>
    */
    public static ArrayList<MenuItem> getMenuItems(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
                "csce315_909_bat2492",
                "BT2415");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        
        ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();
        try{
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            SQLCalls s = new SQLCalls();

            items = s.ViewTable(st,new String[]{"id","price","name"},"menu_item");

        }
        catch (Exception e){
            System.out.println("DB Querry Failed");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        ArrayList<MenuItem> ret = new ArrayList<MenuItem>();
        try{
            for(ArrayList<String> string_menu_item: items){
                // System.out.println("Printing Menu Item: ");
                // System.out.println("\tid: " + string_menu_item.get(0));
                // System.out.println("\tsize: " + string_menu_item.get(1));
                // System.out.println("\tprice: " + string_menu_item.get(2));
                // System.out.println("\tname: " + string_menu_item.get(3));
                MenuItem item = new MenuItem(Long.valueOf(string_menu_item.get(0)).longValue(),//id
                                             string_menu_item.get(2),//name
                                             Double.valueOf(string_menu_item.get(1)).doubleValue());//price
                ret.add(item);
            }
        }
        catch(Exception e){
            System.out.println("Could not parse out a Menu Item");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        
        
        return ret;
    }

    /**
     * gets all of the ingredients in the database
     * 
     * @author Brandon Thomas
     * @return ArrayList<Ingredient> a list containing all of the ingredients in the database
     */
    public static ArrayList<Ingredient> getIngredients(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
                "csce315_909_bat2492",
                "BT2415");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        ArrayList<ArrayList<String>> str_ingredients = new ArrayList<ArrayList<String>>();
        try{
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            SQLCalls s = new SQLCalls();

            str_ingredients = s.ViewTable(st,new String[]{"id","_name","restock_price","quantity","threshold"},"ingredient");
        }
        catch (Exception e){
            System.out.println("DB Querry Failed");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        try{
            for(ArrayList<String> str_ingredient: str_ingredients){
                /*
                for(int i = 0; i < 5; i++){
                    System.out.println(str_ingredient.get(i));
                }*/
                Ingredient ing = new Ingredient(Long.valueOf(str_ingredient.get(0)).longValue(),//id
                                                str_ingredient.get(3),//name
                                                Double.valueOf(str_ingredient.get(2)).doubleValue(),//price
                                                Double.valueOf(str_ingredient.get(1)).doubleValue(),//quantity
                                                Double.valueOf(str_ingredient.get(4)).doubleValue());//threshold

                ingredients.add(ing);
            }
        } catch (Exception e){
            System.out.println("Could not parse out an Ingredient");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return ingredients;
    }

    /**
     * gets all of the customers in the database
     * 
     * @author Brandon Thomas
     * @return ArrayList<Customer> a list containing all of the customers in the database      
     */
    public static ArrayList<Customer> getCustomers(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_09m_db",
                "csce315_909_bat2492",
                "BT2415");
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        ArrayList<ArrayList<String>> str_customers = new ArrayList<ArrayList<String>>();
        try{
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            SQLCalls s = new SQLCalls();

            str_customers = s.ViewTable(st,new String[]{"id","_name","email"},"customer");
        }
        catch (Exception e){
            System.out.println("DB Querry Failed");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        ArrayList<Customer> customers = new ArrayList<Customer>();
        try{
            for(ArrayList<String> str_customer: str_customers){
                // System.out.println("printing Customer: ");
                // System.out.println("\tid: " + str_customer.get(0));
                // System.out.println("\tname: " + str_customer.get(1));
                // System.out.println("\temail: " + str_customer.get(2));
                Customer cus = new Customer(Long.valueOf(str_customer.get(0)).longValue(),//Id
                                            str_customer.get(1),//Name
                                            str_customer.get(2));//email
                customers.add(cus);
            }
        }
        catch(Exception e){
            System.out.println("Could not parse out a Customer");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return customers;
    }

    /**
     * gets the total price of all of the items in an order
     * 
     *  @author Brandon Thomas
     *  @param  items - a list of all of the items in an order
     *  @return double - total price of all of the items in the list
     */
    public static double getTotal(ArrayList<MenuItem> items){
        double total = 0.0;
        for(MenuItem item : items){
            total += item.getPrice();
        }
        return total;
    }
} 

