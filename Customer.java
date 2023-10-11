/*
    @author Brandon Thomas
*/
public class Customer{
    private long _id;
    private String _name;
    private String _email;

    public Customer(){
        this(-1);
    }

    public Customer(long id){
        this(id,"Defualt Name");
    }

    public Customer(long id, String name){
        this(id,name,"Default@email.com");
    }

    public Customer(String name, String email){
        this(CashierCalls.getNextCustomerId(), name, email);
        
    }

    public Customer(long id, String name, String email){
        _id = id;
        _name = name;
        _email = email;
    }

    /*
        @author Brandon Thomas
        @param id - id for customer id to be set to
    */
    public void setId(long id){
        _id = id;
    }

    /*
        @author Brandon Thomas
        @param name - name for customer name to be set to
    */
    public void setName(String name){
        _name = name;
    }

    /*
        @author Brandon Thomas
        @param email - email for customer email to be set to
    */
    public void setEmail(String email){
        _email = email;
    }

    public long getId(){
        return _id;
    }

    public String getName(){
        return _name;
    }

    public String getEmail(){
        return _email;
    }

    public boolean equals(Customer other){
        boolean id_equals = _id == other.getId();
        boolean name_equals = _name.equals(other.getName());
        boolean email_equals = _email.equals(other.getEmail());

        return id_equals && name_equals && email_equals;
    }

}