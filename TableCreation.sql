CREATE Table Ingredient(
    ID int NOT NULL,
    quantity int,
    _name text,
    restock_price float,
    PRIMARY KEY(ID),
);

CREATE Table Ingredient_Menu_Item_Join_Table(
    ID int NOT NULL,
    Ingredient_ID int,
    Menu_Item_ID int,
    PRIMARY KEY(ID),
    FOREIGN KEY(Ingredient_ID) REFERENCES Ingredient(ID),
    FOREIGN KEY(Menu_Item_ID) REFERENCES Menu_Item(ID)
);

CREATE Table Menu_Item(
    ID int NOT NULL,
    _size int,
    price float,
    _name text,
    PRIMARY KEY(ID),
);


CREATE Table Menu_Item_Order_Join_Table(
    ID int NOT NULL,
    MenuItemID int,
    Orderid int,
    PRIMARY KEY(ID),
    FOREIGN KEY(MenuItemID) REFERENCES Menu_Item(ID),
    FOREIGN KEY(Orderid) REFERENCES Order_Table(ID)
);

CREATE Table Order_Table(
    ID int NOT NULL,
    MenuItems text,
    TotalPrice float,
    Customer int,
    DatePlaced int,
    PRIMARY KEY(ID),
    FOREIGN KEY(Customer) REFERENCES CustomerOrderJoinTable(ID)
);


CREATE Table Customer(
    ID int NOT NULL,
    _Name text,
    email text,
    Orders int,
    PRIMARY KEY(ID),
    FOREIGN KEY(Orders) REFERENCES CustomerOrderJoinTable(ID)
);

CREATE Table Customer_Order_Join_Table(
    ID int,
    OrderId int,
    CustomerID int,
    PRIMARY KEY(ID),
    FOREIGN KEY(OrderId) REFERENCES OrderTable(ID),
    FOREIGN KEY(CustomerID) REFERENCES Customer(ID)
);

-- SELECT * FROM Ingredient;
-- SELECT * FROM IngredientMenuItemJoinTable;
-- SELECT * FROM Menu_Item;
-- SELECT * FROM MenuItemOrderJoinTable;
-- SELECT * FROM Order;
-- SELECT * FROM Customer;
-- SELECT * FROM CustomerOrderJoinTable;