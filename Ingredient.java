/*
    @author Brandon Thomas
    a class to store data of an Ingredient entry with basic constructors and set/get functions
*/
public class Ingredient{
    String _name;
    long _id;
    double _quantity;
    double _restock_price;
    double _threshold;

    public Ingredient(){
        this(-1);
    }

    public Ingredient(long id){
        this(id,"default Name");
    }

    public Ingredient(long id, String name){
        this(id,name,0.0);
    }

    public Ingredient(long id, String name, double price){
        this(id,name,price,0.0);
    }

    public Ingredient(long id, String name, double price, double quantity){
        this(id,name,price,quantity,0.0);
    }

    public Ingredient(long id, String name, double price, double quantity, double restock){
        this(id,name,price,quantity,restock,50);
    }

    public Ingredient(long id, String name, double price, double quantity, double restock, double threshold){
        _id = id;
        _name = name;
        _quantity = quantity;
        _restock_price = restock;
        _threshold = threshold;
    }

    public long getId(){
        return _id;
    }

    public void setId(long id){
        _id = id;
    }

    public String getName(){
        return _name;
    }

    public void setName(String name){
        _name = name;
    }

    public double getQuantity(){
        return _quantity;
    }

    public void setQuantity(double quantity){
        _quantity = quantity;
    }

    public double getRestockPrice(){
        return _restock_price;
    }

    public void setRestockPrice(double price){
        _restock_price = price;
    }

    public double getThreshold(){
        return _threshold;
    }

    public void setThreshold(double threshold){
        _threshold = threshold;
    }

}