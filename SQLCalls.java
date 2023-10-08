import java.util.ArrayList;
import java.sql.*;

public class SQLCalls{

    //Should return the columns from a specified sql table
    public String[] getColumnNames(Statement stmt, String table){
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

    //Views the a table for the manager
    //Runs a SQL command and then formats the results
    public ArrayList<ArrayList<String>> ViewTable(Statement stmt, String[] columnNames, String table){

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

    //Adds an item to the item to a specified table
    public static void AddItem(String table, Statement stmt, Object[] values){
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

    public static void UpdateTable(Statement stmt, Object value, String table, String changedValueColumn, String sameValueColumn, String sameValue){
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

    public static void deleteItem(Statement stmt, String table, String column, Object value){
        try{
            String deleteCondition = "";
           if(value instanceof String){
                    deleteCondition = "'"+value+"'";
            }
            else{
                deleteCondition = value.toString();
            }

            stmt.executeUpdate("DELETE FROM " + table + " WHERE " + column + " = " + deleteCondition);

        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

}