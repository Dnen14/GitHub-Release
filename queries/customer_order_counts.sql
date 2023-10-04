SELECT c._Name AS customer_name, COUNT(*) AS order_count
FROM Customer c
INNER JOIN Customer_Order_Join_Table co ON c.ID = co.CustomerID
GROUP BY c._Name;