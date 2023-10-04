SELECT c._Name AS customer_name
FROM Customer c
INNER JOIN Customer_Order_Join_Table co ON c.ID = co.CustomerID
INNER JOIN Order_Table o ON co.OrderId = o.ID
WHERE o.Date_Placed IN (
    SELECT Date_Placed
    FROM Order_Table
    INNER JOIN Customer_Order_Join_Table co2 ON Order_Table.ID = co2.OrderId
    WHERE co2.CustomerID = 2
);