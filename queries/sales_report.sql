SELECT m.name
FROM menu_item_order_join_table mio
INNER JOIN order_table o ON mio.orderid = o.id
INNER JOIN menu_item m ON mio.menuitemid = m.id
WHERE o.date_placed BETWEEN '2022-10-04 22:00:09' AND '2022-10-04 23:00:00';