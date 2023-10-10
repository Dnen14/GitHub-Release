enum ItemSize{
    Small,
    Medium,
    Large
}

public class MenuItem{
    private double _price;
    private String _name;
    private long int _id;
    private ItemSize _size;


    public MenuItem(){
        this(-1);
    }

    public MenuItem(long int id){
        this(id,"Default Name");
    }

    public MenuItem(long int id, String name){
        this(id,name,0.0);
    }

    public MenuItem(long int id, String name, double price){
        this(id,name,price,ItemSize.Medium);
    }

    public MenuItem(long int id, String name, double price, ItemSize size){
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

    public void setId(long int id){
        _id = id;
    }

    public double getPrice(double price){
        return _price;
    }

    public String getName(String name){
        return _name;
    }

    public ItemSize getSize(String size){
        return _size;
    }

    public long int getId(long int id){
        return _id;
    }
}