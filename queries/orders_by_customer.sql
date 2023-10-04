SELECT o.ID AS order_id, o.DatePlaced, o.TotalPrice
FROM Order_Table o
INNER JOIN Customer_Order_Join_Table co ON o.ID = co.OrderId
INNER JOIN Customer c ON co.CustomerID = c.ID
WHERE c._Name = 'Customer_Name';