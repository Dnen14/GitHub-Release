SELECT DATE_TRUNC('hour', Date_Placed) AS hour_start, COUNT(*) AS order_count, SUM(TotalPrice) AS total_sales
FROM Order_Table
GROUP BY hour_start
ORDER BY hour_start;