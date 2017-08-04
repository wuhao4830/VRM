# -*- coding: UTF-8 -*-
import time
import datetime
import re
import pypyodbc
import MySQLdb
import json

wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
wm_cur = wm_con.cursor()

cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
lsh_con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
lsh_cur = lsh_con.cursor()

now = int(time.mktime(datetime.date.today().timetuple()))
sql = "select WERKS,NAME1,BUKRS,PSTLZ,STRAS,TELF1,ZCLOSE FROM M_V_SITE WHERE MANDT = '300'"
wm_cur.execute(sql)

l = []
cnt = 0
for shopId, name, companyId, postcode, address, tel, zclose in wm_cur:
    status = 2 if zclose == 'X' else 1
    l.append([shopId, name, companyId, postcode, address, tel, status, now, now])
    cnt += 1
    if cnt % 1000 == 0:
        lsh_cur.executemany(
        """insert into shop(shop_id, name, company_id, postcode, address, phone, status, created_at, updated_at)
           values(%s,%s,%s,%s,%s,%s,%s,%s,%s)""", l)
        l = []
        lsh_con.commit()

lsh_cur.executemany("insert into shop(shop_id, name, company_id, postcode, address, phone, status, created_at, updated_at) values(%s,%s,%s,%s,%s,%s,%s,%s,%s)", l)
lsh_con.commit()
