SELECT mi.name AS menu_item_name, mi.price
FROM Menu_Item mi
INNER JOIN Menu_Item_Order_Join_Table mjo ON mi.ID = mjo.MenuItemID
INNER JOIN Order_Table o ON mjo.Orderid = o.ID
WHERE o.ID = 0;