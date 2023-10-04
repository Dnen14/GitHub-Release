SELECT c._Name AS customer_name
FROM Customer c
INNER JOIN Customer_Order_Join_Table co ON c.ID = co.CustomerID
INNER JOIN Order_Table o ON co.OrderId = o.ID
INNER JOIN Menu_Item_Order_Join_Table mjo ON o.ID = mjo.Orderid
INNER JOIN Menu_Item mi ON mjo.MenuItemID = mi.ID
WHERE mi._name = 'Classic Milk Tea';