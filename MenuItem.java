/**
 * The ItemSize enum represents different sizes for menu items.
 *
 * This enum provides a list of predefined sizes for menu items, including Small, Medium, and Large.
 * Each size has an associated name, which can be obtained using the `toString` method.
 *
 * @author Brandon Thomas
 * @version 1.0
 * @since Oct 11, 2023
 */
enum ItemSize{
    Small("small"),
    Medium("medium"),
    Large("large");

    private final String name;

    ItemSize(String s){
        name = s;
    }

    public String toString(){
        return this.name;
    }
}

/**
 * The MenuItem class represents an item on the menu and is useful for inventory management.
 * It stores information about the item's ID, name, price, and size.
 *
 * This class provides a convenient way to work with menu item data and perform operations
 * such as setting and retrieving the item's attributes.
 *
 * @author Brandon Thomas
 * @version 1.0
 * @since Oct 11, 2023
 */
public class MenuItem{
    private double _price;
    private String _name;
    private long _id;
    private ItemSize _size;

    /**
     * Default constructor for the MenuItem class.
     * Initializes a MenuItem object with default values.
     */
    public MenuItem(){
        this(-1);
    }

    /**
     * Constructor for the MenuItem class.
     * Initializes a MenuItem object with the specified ID.
     *
     * @param id the unique identifier for the menu item
     */
    public MenuItem(long id){
        this(id,"Default Name");
    }

    /**
     * Constructor for the MenuItem class.
     * Initializes a MenuItem object with the specified ID and name.
     *
     * @param id the unique identifier for the menu item
     * @param name the name of the menu item
     */
    public MenuItem(long id, String name){
        this(id,name,0.0);
    }

    /**
     * Constructor for the MenuItem class.
     * Initializes a MenuItem object with the specified ID, name, and price.
     *
     * @param id the unique identifier for the menu item
     * @param name the name of the menu item
     * @param price the price of the menu item
     */
    public MenuItem(long id, String name, double price){
        this(id,name,price,ItemSize.Medium);
    }

    /**
     * Constructor for the MenuItem class.
     * Initializes a MenuItem object with the specified ID, name, price, and size.
     *
     * @param id the unique identifier for the menu item
     * @param name the name of the menu item
     * @param price the price of the menu item
     * @param size the size of the menu item
     */
    public MenuItem(long id, String name, double price, ItemSize size){
        _price = price;
        _name = name;
        _id = id;
        _size = size;
    }

    /**
     * Sets the price of the menu item.
     *
     * @param price The new price to set for the menu item.
     */
    public void setPrice(double price){
        _price = price;
    }

    /**
     * Sets the name of the menu item.
     *
     * @param name The new name to set for the menu item.
     */
    public void setName(String name){
        _name = name;
    }

    /**
     * Sets the size of the menu item.
     *
     * @param size The new size to set for the menu item.
     */
    public void setSize(ItemSize size){
        _size = size;
    }

    /**
     * Sets the unique identifier (ID) of the menu item.
     *
     * @param id The new ID to set for the menu item.
     */
    public void setId(long id){
        _id = id;
    }

    /**
     * Gets the price of the menu item.
     *
     * @return The price of the menu item.
     */
    public double getPrice(){
        return _price;
    }

    /**
     * Gets the name of the menu item.
     *
     * @return The name of the menu item.
     */
    public String getName(){
        return _name;
    }

    /**
     * Gets the size of the menu item.
     *
     * @return The size of the menu item.
     */
    public ItemSize getSize(){
        return _size;
    }

    /**
     * Gets the unique identifier (ID) of the menu item.
     *
     * @return The ID of the menu item.
     */
    public long getId(){
        return _id;
    }
}
