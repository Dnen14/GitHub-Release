CREATE Table Ingredient(
    ID int NOT NULL,
    quantity int,
    Menu_item int,
    name text,
    restock_price float,
    PRIMARY KEY(ID),
    FOREIGN KEY(Menu_item) REFERENCES IngredientMenuItemJoinTable(ID)
);

CREATE Table IngredientMenuItemJoinTable(
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
    name text,
    Order_ID int,
    Ingredient int,
    PRIMARY KEY(ID),
    FOREIGN KEY(Order_ID) REFERENCES MenuItemOrderJoinTable(ID),
    FOREIGN KEY(Ingredient) REFERENCES IngredientMenuItemJoinTable(ID)
);


CREATE Table MenuItemOrderJoinTable(
    ID int NOT NULL,
    MenuItemID int,
    Orderid int,
    PRIMARY KEY(ID),
    FOREIGN KEY(MenuItemID) REFERENCES Menu_Item(ID),
    FOREIGN KEY(Orderid) REFERENCES Order(ID)
);

CREATE Table OrderTable(
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

CREATE Table CustomerOrderJoinTable(
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