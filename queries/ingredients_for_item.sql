SELECT i.name AS ingredient_name
FROM Ingredient i
INNER JOIN Ingredient_Menu_Item_Join_Table im ON i.ID = im.Ingredient_ID
INNER JOIN Menu_Item mi ON im.Menu_Item_ID = mi.ID
WHERE mi.name = 'Honey Lemonade with Aloe Vera';