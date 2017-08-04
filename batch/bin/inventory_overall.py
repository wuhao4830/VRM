# -*- coding: UTF-8 -*-
import json
import time
import re
import os
import pypyodbc
import MySQLdb
import redis
import datetime
from decimal import *

def process(lshDB, wumartDB, redisCli, data):
    date = int(time.mktime(datetime.datetime.strptime(data,"%Y%m%d").timetuple()))
    now = time.mktime(time.localtime())

    gkc = {}
    sql = 'select vid, date, sum(it_amount) from inventory_gkc where date = {0} group by vid'.format(date)
    cur.execute(sql)
    for vid, date, ntAmount in cur:
        if date not in gkc:
            gkc[date] = {}
        gkc[date][vid] = ntAmount

    cur.execute('select distinct(vid) from vender_main')
    l =[]
    cnt = 0
    for vid, in cur:
        base = cli.hgetall('inventory:{0}:{1}'.format(vid, date))
        amount = Decimal('0.00')
        for k,v in base.iteritems():
            d = json.loads(v)
            amount += Decimal(d['it_amount'])
        l.append([vid, str(amount), gkc.get(date,{}).get(vid,'0'), date, now, now])
    cur.executemany("""insert into inventory_overall(vid,it_amount,gkc,date,created_at,updated_at) 
        values(%s,%s,%s,%s,%s,%s)
        on duplicate key update it_amount = VALUES(it_amount), gkc = VALUES(gkc), updated_at = VALUES(updated_at)""", l)
    con.commit()

if __name__ == "__main__":
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    cli = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db=gConfig['redis_db'])
    con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
    cur = con.cursor()

    #today = datetime.datetime.strptime("20160327", "%Y%m%d")
    today = datetime.date.today()
    date = ( today  - datetime.timedelta(days=1)).strftime("%Y%m%d")
    process(lshDB=cur, wumartDB = None, redisCli = cli, data = date)
    os.system(gConfig['GenInventoryDaily']+ ' %s "'%date)
    cur.close()
    con.close()
    """
    start = datetime.datetime.strptime("20160303", "%Y%m%d")
    end = datetime.datetime.strptime("20160308", "%Y%m%d")
    date_generated = [start + datetime.timedelta(days=x) for x in range(0, (end-start).days)]

    for date in date_generated:
        process(lshDB = cur, wumartDB = None, redisCli = cli, data = date.strftime("%Y%m%d"))
    """
