import java.util.*;
import java.sql.*;
import java.time.*;

public final class CashierCalls extends SQLCalls{
    
    /*
        submits an order to the SQL database and adds all of the proper join table items
        @author Brandon Thomas
        @param items - list of menu items in the order
        @param c - customer that placed the order
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

            //execute the query for the order table
            call.AddItem("order_table",st,nxew String[]{String.valueOf(order_id),String.valueOf(getTotal(items)),LocalDateTime.now().toString()});

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

    /*
        @author Brandon Thomas
        @return a long containing the next available id of an order
        @throws no errors
    */
    public static long getNextOrderId(){
        return getNextTableId("order_table");
    }

    /*
        @author Brandon Thomas
        @return long containing the next available id in the menu item order join table of the database
    */
    public static long getNextMenuOrderJoinId(){
        return getNextTableId("menu_item_order_join_table");
    }

    /*
        @author Brandon Thomas
        @return long containing the next available id in the customer table of the database
    */
    public static long getNextCustomerId(){
        return getNextTableId("customer");
    }

    /*
        @author Brandon Thomas
        @return long containing the next available id in the menu item order join table of the database
    */
    public static long getNextCustomerOrderJoinId(){
        return getNextTableId("customer_order_join_table");
    }

    /*
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
            ResultSet rs = st.executeQuery("SELECT MAX(id) as max_id FROM " + table);

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

    /*
        @author Brandon Thomas
        @returns all of the menu items in the database  
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

    /*
        @author Brandon Thomas
        @return ArrayList<Customer> a list containing all of the customers in the db
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

    /*
        @author Brandon Thomas
        @param ArrayList<MenuItem> items - a list of all of the items in an order
        @return double - total price of all of the items in the list
    */
    public static double getTotal(ArrayList<MenuItem> items){
        double total = 0.0;
        for(MenuItem item : items){
            total += item.getPrice();
        }
        return total;
    }
} 

