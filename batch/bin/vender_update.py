# -*- coding: UTF-8 -*-
import time
import datetime
import re
import MySQLdb
import json

cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
lsh_con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
lsh_cur = lsh_con.cursor()

l = []
now = int(time.time())
for vid in [317916,317917,317918,317919,317981,318230,318234,318235,318709,102561,325204,317665,317666,317667,317704,317705,318229,322605,324863,324920,324929,311403,311404,311406,325129,204814,313810,313920,313801]:

    l.append([2,vid])

sql = """update vender_pay set payment_control = %s where vid = %s"""

lsh_cur.executemany(sql, l)
lsh_con.commit()
