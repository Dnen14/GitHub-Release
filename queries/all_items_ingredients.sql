SELECT mi._name AS menu_item_name, i._name AS ingredient_name
FROM Menu_Item mi
INNER JOIN Ingredient_Menu_Item_Join_Table im ON mi.ID = im.Menu_Item_ID
INNER JOIN Ingredient i ON im.Ingredient_ID = i.ID;