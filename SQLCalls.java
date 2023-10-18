import java.util.ArrayList;
import java.sql.*;
import java.text.SimpleDateFormat;


/**
 * The SQLCalls class provides methods for interacting with the POS system's database.
 *
 * This class encapsulates database operations and SQL queries, allowing the application
 * to communicate with the underlying database. It includes methods for executing queries,
 * retrieving data, and managing database connections.
 *
 * Note: This class assumes a specific database schema and connection details.
 * 
 * @author Rahul Singh
 * @author Brandon Thomas
 * @author Zak Borman
 * @author Abhinav Nallam
 * @version 3.0
 * @since Oct 3, 2023
 */
public class SQLCalls{

    /**
     * Retrieves the column names of a specified SQL table.
     *
     * This method executes a SQL query to retrieve the column names of a specified database table.
     * It returns an array of strings containing the column names.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param table The name of the SQL table for which column names are to be retrieved.
     * @return An array of strings representing the column names of the specified table.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public String[] getColumnNames(Statement stmt, String table){
        //Should return the columns from a specified sql table
        try{
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] columns = new String[columnCount];

            for(int i = 1; i <= columnCount; i++){
                columns[i-1] = rsmd.getColumnName(i);
            }
            return columns;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * Retrieves values from a specified column of a SQL table.
     *
     * This method executes a SQL query to retrieve values from a specific column of a given
     * database table. It returns an array of strings containing the values.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param table The name of the SQL table from which to retrieve values.
     * @param columnName The name of the column from which to retrieve values.
     * @return An array of strings containing the values from the specified column.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public String[] getSpecifiedTableValues(Statement stmt, String table, String columnName){
        //Gets values from a column based on a specific table
        try{
            ResultSet rs = stmt.executeQuery("SELECT " + columnName + " FROM " + table);
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            String[] columns = new String[rowCount];
            int i = 0;

            while (rs.next()){
                columns[i] = rs.getString(columnName);
                i++;
            }

            return columns;
            
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * Retrieves a single value from a specific column of a SQL table based on a condition.
     *
     * This method executes a SQL query to retrieve a value from a specific column of a given
     * database table based on a specified condition. It returns the selected value as a string.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param table The name of the SQL table from which to retrieve the value.
     * @param columnName The name of the column from which to retrieve the value.
     * @param conditionColumn The name of the column to use for the condition.
     * @param conditionValue The value to match in the condition.
     * @return The selected value from the specified column that matches the condition.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
     public String getOneTableValue(Statement stmt, String table, String columnName, String conditionColumn, String conditionValue){
        // Returns one value from a table's specific column that matches the given values
        try{
            conditionValue = "'" + conditionValue + "'";
            ResultSet rs = stmt.executeQuery("SELECT " + columnName + " FROM " + table + " WHERE " + conditionColumn + " = " + conditionValue);
            String column = new String();

            while (rs.next()){
                column = rs.getString(columnName);
            }

            return column;
            
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * Retrieves multiple values from a specific column of a SQL table based on a condition.
     *
     * This method executes a SQL query to retrieve multiple values from a specific column of a given
     * database table based on a specified condition. It returns the selected values as an array of strings.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param table The name of the SQL table from which to retrieve the values.
     * @param columnName The name of the column from which to retrieve the values.
     * @param conditionColumn The name of the column to use for the condition.
     * @param conditionValue The value to match in the condition.
     * @return An array of strings containing the selected values from the specified column that match the condition.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public String[] getMultipleTableValues(Statement stmt, String table, String columnName, String conditionColumn, String conditionValue){
        // Returns any value from a table's specific column that matches the given values
        try{
            conditionValue = "'" + conditionValue + "'";
            ResultSet rs = stmt.executeQuery("SELECT " + columnName + " FROM " + table + " WHERE " + conditionColumn + " = " + conditionValue);
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            String[] columns = new String[rowCount];
            int i = 0;

            while (rs.next()){
                columns[i] = rs.getString(columnName);
                i++;
            }

            return columns;
            
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * Retrieves a list of order IDs within a specified date range.
     *
     * This method executes an SQL query to retrieve a list of order IDs from a database table
     * where the order date falls within the specified date range. The result is returned as an ArrayList of strings.
     * @author Zak Borman
     * @param stmt A Statement object for executing SQL queries.
     * @param time1 The start date of the date range (formatted as a string, e.g., 'YYYY-MM-DD').
     * @param time2 The end date of the date range (formatted as a string, e.g., 'YYYY-MM-DD').
     * @return An ArrayList of strings containing order IDs within the specified date range.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public ArrayList<String> ordersInRange(Statement stmt, String time1, String time2) {
        ArrayList<String> data = new ArrayList<String>();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM order_table WHERE order_table.date_placed BETWEEN '" + time1 + "' AND '" + time2 + "'");
            while (rs.next()) {
                data.add(rs.getString("id"));
            }
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * View the contents of a database table and format the results.
     *
     * This method executes an SQL query to retrieve all rows and columns from a specified database table.
     * The result is formatted and returned as an ArrayList of ArrayLists of strings, where each inner ArrayList represents a row of data.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param columnNames An array of column names for the table, used for formatting the results.
     * @param table The name of the database table to view.
     * @return An ArrayList of ArrayLists of strings containing the formatted table data.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public ArrayList<ArrayList<String>> ViewTable(Statement stmt, String[] columnNames, String table){
        //Views the table for the manager
        //Runs a SQL command and then formats the results
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        try{
            ResultSet rs = stmt.executeQuery("Select * from " + table);
            while(rs.next()){
                
                ArrayList<String> column = new ArrayList<String>();

                for(int i = 1; i <= columnNames.length; i++){
                    column.add(rs.getString(i));
                }

                data.add(column);
            }
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }

        return null;
    }

    /**
     * Add an item to a specified database table.
     *
     * This method inserts a new row into a specified database table with the provided values.
     * @author Abhinav Nallam
     * @param table The name of the database table where the item will be added.
     * @param stmt A Statement object for executing SQL queries.
     * @param values An array of objects representing the values to be added to the table.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public static void AddItem(String table, Statement stmt, Object[] values){
        //Adds an item to the item to a specified table
        try{
            String valuesAsQueryString = "";

            for(int i = 0; i < values.length; i++){
                String addedValue = "";

                if(values[i] instanceof String){
                    addedValue = "'"+values[i]+"'";
                }
                else{
                    addedValue = values[i].toString();
                }

                if(i + 1 == values.length){
                    valuesAsQueryString += addedValue;
                }
                else{
                    valuesAsQueryString += "" + addedValue + ", ";
                }
            }

           stmt.executeUpdate("INSERT INTO " + table + " VALUES (" + valuesAsQueryString + ")");
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Update a database table with a new value provided for a specific column based on a condition.
     *
     * This method updates a database table by setting a new value for a specified column based on a condition
     * where another column has a particular value.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param value The new value to set in the specified column.
     * @param table The name of the database table to be updated.
     * @param changedValueColumn The name of the column to be updated with the new value.
     * @param sameValueColumn The name of the column that needs to match a specific value.
     * @param sameValue The value that the "sameValueColumn" should match for the condition.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public static void UpdateTable(Statement stmt, Object value, String table, String changedValueColumn, String sameValueColumn, String sameValue){
        //Updates the table with a new value that is provided
        try{
                String changedAddedValue = "";
                String sameAddedValue = "";

                if(value instanceof String){
                    changedAddedValue = "'"+value+"'";
                }
                else{
                    changedAddedValue = value.toString();
                }
                if(sameValue instanceof String){
                    sameAddedValue = "'"+sameValue+"'";
                }
                else{
                    sameAddedValue = sameValue.toString();
                }

                stmt.executeUpdate("UPDATE " + table + " SET " + changedValueColumn + " = " + changedAddedValue + " WHERE " + sameValueColumn + " = " + sameAddedValue);
            }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Delete a row of data from a database table based on a specified condition.
     *
     * This method deletes a row of data from a database table where a specified column matches a given value.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param table The name of the database table from which to delete the row.
     * @param column The name of the column that should match the specified value for the deletion condition.
     * @param value The value that the specified column should match to delete the row.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public static void deleteItem(Statement stmt, String table, String column, String value){
        //Deletes a row of data from a table
        try{

            stmt.executeUpdate("DELETE FROM " + table + " WHERE " + column + " = " + value);

        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

   /**
     * Check if a specified value exists in a database table based on a given condition.
     *
     * This method checks if a value exists in a database table by evaluating a condition on a specified column.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param table The name of the database table to perform the existence check.
     * @param returnedColumnName The name of the column whose value is returned as "TRUE" if the condition is met, or "FALSE" if it is not.
     * @param conditionColumnName The name of the column on which the condition is evaluated.
     * @param condition The condition to be evaluated for existence (e.g., "column_name = value").
     * @return "TRUE" if the specified condition exists in the table, "FALSE" otherwise.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public String checkIfValueExists(Statement stmt, String table, String returnedColumnName, String conditionColumnName, String condition){
         //Checks if a table value exists
        try{
            String ret = "";
            ResultSet rs = stmt.executeQuery("SELECT " + returnedColumnName + " FROM " + table + " WHERE EXISTS " + "(SELECT  " + conditionColumnName + " FROM " + table + " WHERE " + condition + ")");
            
            if(rs.next()){
                ret = "TRUE";
            }
            else
            {
                ret = "FALSE";
            }

            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * Get the quantity of an ingredient in a menu item based on specific conditions.
     *
     * This method retrieves the quantity of an ingredient in a menu item from a database table by evaluating two conditions:
     * ingredient_id and menu_item_id.
     * @author Abhinav Nallam
     * @param stmt A Statement object for executing SQL queries.
     * @param ingredient_id The unique identifier of the ingredient.
     * @param menu_item_id The unique identifier of the menu item.
     * @return The quantity of the specified ingredient in the specified menu item.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public String getQuantityOfIngredientsInMenuItem(Statement stmt, String ingredient_id, String menu_item_id){
        //Get two table values from the a query based on the two conditions
        try{
            String ret = "";

            ResultSet rs = stmt.executeQuery("SELECT quantity FROM ingredient_menu_item_join_table WHERE ingredient_id = " + ingredient_id + " AND menu_item_id = " + menu_item_id);
            while(rs.next()){
                ret = rs.getString("quantity");
            }

            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return null;
    }

    /**
     * Retrieve a list of menu items sold within a specified time range.
     *
     * This method retrieves the names of menu items that were sold in orders placed within the specified time range.
     *
     * @author Rahul Singh
     * @param stmt A Statement object for executing SQL queries.
     * @param startTime The start time of the time range (in a format compatible with the database).
     * @param endTime The end time of the time range (in a format compatible with the database).
     * @return A list of menu item names sold during the specified time range.
     * @throws SQLException if an error occurs while executing the SQL query.
     */
    public ArrayList<String> getSalesByMenuItem(Statement stmt, String startTime, String endTime) {
        try {

            ArrayList<String> salesData = new ArrayList<String>();

            String query = "SELECT m.name " +
                "FROM menu_item_order_join_table mio " +
                "INNER JOIN order_table o ON mio.orderid = o.id " +
                "INNER JOIN menu_item m ON mio.menuitemid = m.id " +
                "WHERE o.date_placed BETWEEN '" + startTime + "' AND '" + endTime + "'";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String menuItemName = rs.getString("name");
                salesData.add(menuItemName);
            }

            return salesData;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return null;
    }

}