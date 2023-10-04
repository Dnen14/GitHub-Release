import time
import datetime as dt
import numpy as np
import random as rand

class order:
    id = dt.datetime.now()
    menu_item_table_id = 0
    total = -1
    customer_id = 0
    date_placed = dt.datetime.now()

    def __init__(self,i,t_id,to,c_id,d_pl):
        self.id = i
        self.menu_item_table_id = t_id
        self.total = to
        self.customer_id = c_id
        self.date_placed = d_pl


class menuItem:
    id = -1
    size = 0
    price = 5.0
    order_id = -1
    ingredient_table_id = 0

    def menuItem(i,sz,p, o_id):
        id = i
        size = sz
        price = p
        order_id = o_id

order_file = open("orders.csv","wt")
order_file.write("order_id,order_total,order_date_placed\n")
join_table_file = open("join_table.csv","wt")
join_table_file.write("Order_ID,Menu_item\n")
orders = []
menu_item_order_join = []
menu_item_prices = [7.2, 5.18, 4.96, 5.2, 6.66, 5.63, 5.15, 6.23, 5.44, 5.44, 6.49, 5.46, 5.17, 5.64, 6.12, 6.12, 6.55,
                    6.22, 6.26, 6.35, 4.64, 4.61, 4.34, 5.00, 5.42]
times = np.linspace(11,22.5,24)
weights = {"monday"   : [1,1,2,2,4,4,5,5,9,9,5,5,3,3,3,3,5,5,9,9,7,7,4,4],
           "tuesday"  : [1,1,2,2,3,3,5,5,7,7,4,4,4,4,5,5,10,10,14,14,11,11,4,4],
           "wednesday": [3,3,5,5,6,6,4,4,5,5,3,3,3,3,6,6,2,2,1,1,4,4,10,10],
           "thursday" : [6,6,5,5,3,3,2,2,3,3,2,2,3,3,6,6,5,5,7,7,3,3,4,4],
           "friday"   : [7,7,6,6,10,10,9,9,5,5,7,7,8,8,7,7,11,11,7,7,5,5,6,6],
           "saturday" : [3,3,8,8,7,7,10,10,8,8,4,4,3,3,3,3,7,7,3,3,0,0,1,1],
           "sunday"   : [1,1,3,3,5,5,5,5,5,5,4,4,5,5,6,6,5,5,3,3,2,2,1,1]}

days_in_month = {1:31,2:28,3:31,4:30,5:31,6:30,7:31,8:31,9:30,10:31,11:30,12:31}

total = 1000000.0
date_year = dt.datetime.now().year - 1
date_month = dt.datetime.now().month
date_day = dt.datetime.now().day

print(date_year,type(date_year))
print(date_month,type(date_month))
print(date_day,type(date_day))
order_id = 0


for week in range(52):
    for day in weights:
        num_orders = rand.randint(200,300)
        num_items = rand.randint(10,15)
        if (week == 44 or week == 15) and day == "friday":
            num_orders = rand.randint(600,1000)
            num_items = rand.randint(20,30)
        for order_num in range(num_orders):

            order_time = rand.choices(times,weights=weights[day],k=1)
            order_hour = np.int_(order_time[0])
            order_minute = np.int_((order_time[0] % 1) * 60)
            order_second = dt.datetime.now().second
            order_microsecond = dt.datetime.now().microsecond

            #print(type(order_time),order_time)
            #print(type(order_hour),order_hour)
            #print(type(order_minute),order_minute)
            #print(type(order_second),order_second)
            #print(type(order_microsecond),order_microsecond)

            date = dt.datetime(date_year,date_month,date_day,order_hour,order_minute,order_second,order_microsecond)
            current_order = order(order_id,0,0.0,-1,date)
            order_id += 1

            num_items = rand.randint(2,num_items)
            for i in range(num_items):
                menu_item = rand.randint(0,len(menu_item_prices)-1)
                price = menu_item_prices[menu_item]
                total -= price
                current_order.total += price
                item_index = [x for x in range(len(menu_item_order_join)) if menu_item_order_join[x] == (order_id,menu_item)]
                if item_index != []:
                    menu_item_order_join[item_index][1] += 1
                else:
                    menu_item_order_join += [(order_id,menu_item*1000)]
            
            orders += [current_order]

        date_day += 1
        if date_day > days_in_month[date_month]:
            date_day = 1
            if date_month == 12:
                date_month = 1
                date_year += 1
            else:
                date_month += 1

for o in orders:
    order_file.write(f"{o.id},{o.total},{o.date_placed}\n")

for i in range(len(menu_item_order_join)):
    join_table_file.write(f"{menu_item_order_join[i][0]},{menu_item_order_join[i][1]}\n")

print(total)