# -*- coding: UTF-8 -*-
import time
import datetime
import json
import re
import MySQLdb

cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
lsh_con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
lsh_cur = lsh_con.cursor()

sql = "select vid, payment_period from vender_pay"
lsh_cur.execute(sql)
l = []

now = int(time.time())
for vid, payment_period in lsh_cur:
    if vid < 1000:
        continue

    type = 0
    if payment_period in ['4000', '5000', '6000', '8000', '9000']:
        type = 2
    elif payment_period == '7000':
        type = 4
    elif payment_period in ['Z000', 'Z005', 'Z010', 'Z015', 'Z020', 'Z025', 'Z030', 'Z035', 'Z040', 'Z045', 'Z050', 'Z055', 'Z060', 'Z065', 'Z070', 'Z075', 'Z080', 'Z085', 'Z090', 'Z095','Z105', 'Z110', 'Z120']:
        type = 1
    else:
        pass

    if vid in [317916,317917,317918, 317919,317981, 318230, 318234, 318235, 318709]:
        type = 3
    
    if vid in [308603,603629,328093,309415,325236,205755,107168,318461,324258,324003,207062,321735,318439,203140,322910,322614]:
        type = 6

    l.append([vid, 1, 1, type, now, now])

sql = """insert into vender_check(vid, check_confirm, check_highstock, auto_create_at, created_at, updated_at)
values(%s,%s,%s,%s,%s,%s)
on duplicate key update `updated_at` = VALUES(updated_at)
"""

lsh_cur.executemany(sql, l)
lsh_con.commit()
