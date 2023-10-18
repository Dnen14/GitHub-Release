/**
 * The Customer class represents a customer with attributes such as ID, name, and email.
 *
 * This class is used to create and manage customer information, providing methods to set and retrieve
 * information about each customer. It also includes an `equals` method to compare two Customer objects
 * for equality based on their ID, name, and email.
 *
 * @author Brandon Thomas
 * @version 1.0
 * @since Oct 11, 2023
 */
public class Customer{
    private long _id;
    private String _name;
    private String _email;

    /**
     * Default constructor for the Customer class.
     * Initializes a Customer with default values.
     */
    public Customer(){
        this(-1);
    }

    /**
     * Constructor for the Customer class.
     * Initializes a Customer with the specified ID.
     *
     * @param id the unique identifier for the customer
     */
    public Customer(long id){
        this(id,"Defualt Name");
    }

    /**
     * Constructor for the Customer class.
     * Initializes a Customer with the specified ID and name.
     *
     * @param id the unique identifier for the customer
     * @param name the name of the customer
     */
    public Customer(long id, String name){
        this(id,name,"Default@email.com");
    }

    /**
     * Constructor for the Customer class.
     * Initializes a Customer with the specified ID, name, and email.
     *
     * @param name the name of the customer
     * @param email the email of the customer
     */
    public Customer(String name, String email){
        this(CashierCalls.getNextCustomerId(), name, email);
        
    }

    /**
     * Constructor for the Customer class.
     * Initializes a Customer with the specified ID, name, and email.
     *
     * @param id the unique identifier for the customer
     * @param name the name of the customer
     * @param email the email of the customer
     */
    public Customer(long id, String name, String email){
        _id = id;
        _name = name;
        _email = email;
    }

    /**
     * Sets the ID of the customer.
     *
     * @param id The new ID to set for the customer.
     */
    public void setId(long id){
        _id = id;
    }

    /**
     * Sets the name of the customer.
     *
     * @param name The new name to set for the customer.
     */
    public void setName(String name){
        _name = name;
    }

    /**
     * Sets the email of the customer.
     *
     * @param email The new email to set for the customer.
     */
    public void setEmail(String email){
        _email = email;
    }

    /**
     * Gets the ID of the customer.
     *
     * @return The ID of the customer.
     */
    public long getId(){
        return _id;
    }

    /**
     * Gets the name of the customer.
     *
     * @return The name of the customer.
     */
    public String getName(){
        return _name;
    }

    /**
     * Gets the email of the customer.
     *
     * @return The email of the customer.
     */
    public String getEmail(){
        return _email;
    }

    /**
     * Compares two Customer objects for equality.
     *
     * @param other The other Customer object to compare to.
     * @return True if the two Customer objects are equal, false otherwise.
     */
    public boolean equals(Customer other){
        boolean id_equals = _id == other.getId();
        boolean name_equals = _name.equals(other.getName());
        boolean email_equals = _email.equals(other.getEmail());

        return id_equals && name_equals && email_equals;
    }

}