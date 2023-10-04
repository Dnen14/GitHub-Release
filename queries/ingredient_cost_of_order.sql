SELECT SUM(i.restock_price) AS total_cost
FROM Ingredient i
INNER JOIN Ingredient_Menu_Item_Join_Table im ON i.ID = im.Ingredient_ID
INNER JOIN Menu_Item mi ON im.Menu_Item_ID = mi.ID
INNER JOIN Menu_Item_Order_Join_Table mjo ON mi.ID = mjo.MenuItemID
INNER JOIN Order_Table o ON mjo.Orderid = o.ID
WHERE o.ID = 1;