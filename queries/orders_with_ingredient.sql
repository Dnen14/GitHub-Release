SELECT o.ID AS order_id, o.Date_Placed, o.TotalPrice
FROM Order_Table o
INNER JOIN Menu_Item_Order_Join_Table mjo ON o.ID = mjo.Orderid
INNER JOIN Menu_Item mi ON mjo.MenuItemID = mi.ID
INNER JOIN Ingredient_Menu_Item_Join_Table im ON mi.ID = im.Menu_Item_ID
INNER JOIN Ingredient i ON im.Ingredient_ID = i.ID
WHERE i.name = 'Brown Sugar';