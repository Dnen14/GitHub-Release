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

public class MenuItem{
    private double _price;
    private String _name;
    private long _id;
    private ItemSize _size;


    public MenuItem(){
        this(-1);
    }

    public MenuItem(long id){
        this(id,"Default Name");
    }

    public MenuItem(long id, String name){
        this(id,name,0.0);
    }

    public MenuItem(long id, String name, double price){
        this(id,name,price,ItemSize.Medium);
    }

    public MenuItem(long id, String name, double price, ItemSize size){
        _price = price;
        _name = name;
        _id = id;
        _size = size;
    }

    public void setPrice(double price){
        _price = price;
    }

    public void setName(String name){
        _name = name;
    }

    public void setSize(ItemSize size){
        _size = size;
    }

    public void setId(long id){
        _id = id;
    }

    public double getPrice(){
        return _price;
    }

    public String getName(){
        return _name;
    }

    public ItemSize getSize(){
        return _size;
    }

    public long getId(){
        return _id;
    }
}