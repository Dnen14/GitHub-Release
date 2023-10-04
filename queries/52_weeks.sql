SELECT DATE_TRUNC('week', Date_Placed) AS week_start, COUNT(*) AS order_count
FROM Order_Table
GROUP BY week_start
ORDER BY week_start;