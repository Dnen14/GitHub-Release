-- Insert five drinks into the menu_item table
INSERT INTO menu_item (id, _size, price, name)
VALUES (1, 'medium', 4.75, 'Classic Milk Tea'),
       (2, 'medium', 5.75, 'Taro Pearl Milk Tea'),
       (3, 'medium', 5.9, 'Honey Lemonade with Aloe Vera'),
       (4, 'large', 6, 'Mango Green Milk Tea'),
       (5, 'medium', 6.75, 'Oreo Ice Blended w Pearls');



-- Insert twenty ingredients/supplies into the ingredient table
INSERT INTO ingredient (id, quantity, restock_price, name)
VALUES (1, 10, 16.62, 'Tapioca Pearls'),
       (2, 40, 8.00, 'Brown Sugar'),
       (3, 557, 12.59, 'Straws'),
       (4, 1000, 10.88, 'Napkins'),
       (5, 14, 18.00, 'Honey'),
       (6, 17, 29.99, 'Lychee Jelly'),
       (7, 22, 9.99, 'Aloe Vera'),
       (8, 8, 7.99, 'Sweetened Condensed Milk'),
       (9, 7, 19.99, 'Black Tea Leaves'),
       (10, 3, 11.99, 'Vanilla Ice Cream'),
       (11, 11, 14.99, 'Lemons'),
       (12, 28, 16.99, 'Oreos'),
       (13, 6, 7.99, 'Half and Half'),
       (14, 7, 8.99, 'Whole Milk'),
       (15, 14, 34.99, 'Mango'),
       (16, 3, 11.99, 'Jasmine Green Tea'),
       (17, 2, 6.99, 'Whipped Cream'),
       (19, 4, 11.99, 'Pineapple'),
       (20, 87, 13.99, 'Cups');


-- Insert the ingredient-menu_item relationship into the ingredientmenuitemjointable (such as every drink needs a cup)
INSERT INTO ingredient_menu_item_join_table (id, ingredient_id, menu_item_id)
VALUES (1, 20, 1),
       (2, 20, 2),
       (3, 20, 3),
       (4, 20, 4),
       (5, 20, 5),
       (6, 1, 1), 
       (7, 1, 2),
       (8, 1, 3),
       (9, 1, 4),
       (10, 1, 5),
       (11, 3, 1), -- Every drink needs a straw
       (12, 3, 2),
       (13, 3, 3),
       (14, 3, 4),
       (15, 3, 5),
       (16, 4, 1), -- Every drink needs a napkin
       (17, 4, 2),
       (18, 4, 3),
       (19, 4, 4),
       (20, 4, 5), 
       (21, 8, 1), -- Drinks that need sweetened condensed milk
       (22, 8, 2),
       (23, 8, 4),
       (24, 2, 1), -- Rest of ingredients for Classic Milk Tea
       (25, 9, 1),
       (26, 2, 2), -- Rest of ingredients for Taro Pearl Milk Tea
       (27, 9, 2),
       (28, 5, 3), -- Rest of ingredients for Honey Lemonade with Aloe Vera
       (29, 11, 3),
       (30, 7, 3),
       (31, 6, 3),
       (32, 15, 4), -- Rest of ingredients for Mango Green Milk Tea
       (33, 2, 4),
       (34, 16, 4),
       (35, 14, 4),
       (36, 10, 5), -- Rest of ingredients for Oreo Ice Blended w Pearls
       (37, 12, 5),
       (38, 13, 5),
       (39, 17, 5),


       
