SELECT DATE_TRUNC('day', Date_Placed) AS order_day, SUM(TotalPrice) AS total_sales
FROM Order_Table
GROUP BY order_day
ORDER BY total_sales DESC
LIMIT 10;