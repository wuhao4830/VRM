# -*- coding: UTF-8 -*-
import time
import datetime
import re
import MySQLdb
import sys
import json
sys.path.append('bin/common')
from log import *
from decimal import *

sql = """
insert into monthly_joint_sale(vid, date, shop_id, lowest_sale_amount, lowest_profit_amount, rate, real_sale_amount, real_rate, due_nt_amount, due_it_amount, tax_info, created_at, updated_at)
values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
on duplicate key update `updated_at` = VALUES(updated_at)
"""
cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
lsh_con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
lsh_cur = lsh_con.cursor()

l = []
for line in sys.stdin:
    fields = line[:-1].split('\t')
    if len(fields) != 42:
        FATAL('MONTHLY SALE NOT COMPLETE!')
        continue
    if fields[0] == 'MANDT':
        continue
    vid, year, month, shopId, lowestSaleAmt, lowestProfitAmt,  realSaleAmt, dueNtAmt, dueItAmt = \
            int(fields[3]), int(fields[1]), int(fields[2]), fields[4], fields[6], fields[5], fields[19], fields[23], fields[20]
    if Decimal(lowestSaleAmt) == Decimal('0.0000'):
        rate = str(Decimal('0.0000'))
    else:
        rate = str( (Decimal(lowestProfitAmt) / Decimal(lowestSaleAmt)).quantize(Decimal('0.0000')) )
    if Decimal(realSaleAmt) == Decimal('0.0000'):
        realRate = str(Decimal('0.0000'))
    else:
        realRate = str( ( (Decimal(realSaleAmt) - Decimal(dueItAmt)) / Decimal(realSaleAmt)).quantize(Decimal('0.0000')))

    if month == 12:
        month_end_date = datetime.date(year+1,1,1) - datetime.timedelta(days=1)
    else:
        month_end_date = datetime.date(year,month+1,1) - datetime.timedelta(days=1)
    date = int(time.mktime(month_end_date.timetuple()))
        
    taxInfo = {}
    for i in range(32, 42):
        taxInfo['J{0}'.format(i-32)] = {'it_amount':str(Decimal(fields[i]))}

    l.append([vid, date, shopId, lowestSaleAmt, lowestProfitAmt, rate, realSaleAmt, realRate, dueNtAmt, dueItAmt, json.dumps(taxInfo), int(time.time()), int(time.time())])
print len(l)

lsh_cur.executemany(sql, l)
lsh_con.commit()
