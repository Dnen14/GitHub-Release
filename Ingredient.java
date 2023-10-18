/**
 * The Ingredient class represents an item used in a menu item and is useful for inventory management.
 * It stores information about the item's ID, name, quantity, restock price, and threshold.
 *
 * This class provides a convenient way to work with ingredient data and perform operations
 * such as setting and retrieving the item's attributes.
 *
 * @author Brandon Thomas
 * @version 1.0
 * @since Oct 16, 2023
 */
public class Ingredient{
    String _name;
    long _id;
    double _quantity;
    double _restock_price;
    double _threshold;

    /**
     * Default constructor for the Ingredient class.
     * Initializes an Ingredient object with default values.
     */
    public Ingredient(){
        this(-1);
    }

    /**
     * Constructor for the Ingredient class.
     * Initializes an Ingredient object with the specified ID.
     *
     * @param id the unique identifier for the ingredient
     */
    public Ingredient(long id){
        this(id,"default Name");
    }

    /**
     * Constructor for the Ingredient class.
     * Initializes an Ingredient object with the specified ID and name.
     *
     * @param id the unique identifier for the ingredient
     * @param name the name of the ingredient
     */
    public Ingredient(long id, String name){
        this(id,name,0.0);
    }

    /**
     * Constructor for the Ingredient class.
     * Initializes an Ingredient object with the specified ID, name, and price.
     *
     * @param id the unique identifier for the ingredient
     * @param name the name of the ingredient
     * @param price the price of the ingredient
     */
    public Ingredient(long id, String name, double price){
        this(id,name,price,0.0);
    }

    /**
     * Constructor for the Ingredient class.
     * Initializes an Ingredient object with the specified ID, name, price, and quantity.
     *
     * @param id the unique identifier for the ingredient
     * @param name the name of the ingredient
     * @param price the price of the ingredient
     * @param quantity the quantity of the ingredient
     */
    public Ingredient(long id, String name, double price, double quantity){
        this(id,name,price,quantity,0.0);
    }

    /**
     * Constructor for the Ingredient class.
     * Initializes an Ingredient object with the specified ID, name, price, quantity, and restock price.
     *
     * @param id the unique identifier for the ingredient
     * @param name the name of the ingredient
     * @param price the price of the ingredient
     * @param quantity the quantity of the ingredient
     * @param restock the restock price of the ingredient
     */
    public Ingredient(long id, String name, double price, double quantity, double restock){
        this(id,name,price,quantity,restock,50);
    }

    /**
     * Constructor for the Ingredient class.
     * Initializes an Ingredient object with the specified ID, name, price, quantity, restock price, and threshold.
     *
     * @param id the unique identifier for the ingredient
     * @param name the name of the ingredient
     * @param price the price of the ingredient
     * @param quantity the quantity of the ingredient
     * @param restock the restock price of the ingredient
     * @param threshold the threshold of the ingredient
     */
    public Ingredient(long id, String name, double price, double quantity, double restock, double threshold){
        _id = id;
        _name = name;
        _quantity = quantity;
        _restock_price = restock;
        _threshold = threshold;
    }

    /**
     * Gets the unique identifier (ID) of the ingredient.
     *
     * @return The ID of the ingredient.
     */
    public long getId(){
        return _id;
    }

    /**
     * Gets the unique identifier (ID) of the ingredient.
     *
     * @return The ID of the ingredient.
     */
    public void setId(long id){
        _id = id;
    }

    /**
     * Gets the name of the ingredient.
     *
     * @return The name of the ingredient.
     */
    public String getName(){
        return _name;
    }

    /**
     * Sets the name of the ingredient.
     *
     * @param name The name of the ingredient.
     */
    public void setName(String name){
        _name = name;
    }

    /**
     * Gets the current quantity of the ingredient.
     *
     * @return The quantity of the ingredient.
     */
    public double getQuantity(){
        return _quantity;
    }

    /**
     * Sets the current quantity of the ingredient.
     *
     * @param quantity The quantity of the ingredient.
     */
    public void setQuantity(double quantity){
        _quantity = quantity;
    }

    /**
     * Gets the restock price of the ingredient.
     *
     * @return The restock price of the ingredient.
     */
    public double getRestockPrice(){
        return _restock_price;
    }

    /**
     * Sets the restock price of the ingredient.
     *
     * @param price The restock price of the ingredient.
     */
    public void setRestockPrice(double price){
        _restock_price = price;
    }

    /**
     * Gets the threshold of the ingredient.
     *
     * @return The threshold of the ingredient.
     */
    public double getThreshold(){
        return _threshold;
    }

    /**
     * Sets the threshold of the ingredient.
     *
     * @param threshold The threshold of the ingredient.
     */
    public void setThreshold(double threshold){
        _threshold = threshold;
    }

}