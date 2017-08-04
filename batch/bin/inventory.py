# -*- coding: UTF-8 -*-
import json
import time
import re
import pypyodbc
import MySQLdb
import redis
import datetime
from decimal import *

def formatSql(sDate):
    t = datetime.datetime.strptime(sDate, "%Y%m%d").timetuple()
    year = t.tm_year
    month = "%02d" % t.tm_mon
    day = "%02d" % t.tm_mday

    FIELDS = "b.LIFNR, a.MATNR, c.MEINS, SUM(LABST{date}) as LABST{date}, SUM(SALK3{date}) as SALK3{date}, d.TAKLV".format(date=day)

    sql = """
        SELECT
            {0}
        FROM 
            F_MERCHSTOCK{1}{2} as a 
            left join M_V_SITEARTICLE as b on a.MANDT = b.MANDT and a.MATNR = b.matnr and a.WERKS = b.WERKS and a.MANDT = '300'
            left join M_EINA as c on a.MANDT = c.MANDT and left(SUBSTRING(a.MATNR, PATINDEX('%[^0]%', a.MATNR), LEN(a.MATNR)), 6) = RIGHT(c.MATNR, 6) and b.LIFNR = c.LIFNR
            left join M_V_ARTICLE as d on a.MANDT = d.MANDT and a.MATNR = d.MATNR
        WHERE
            a.MANDT = '300'
            and left(b.LIFNR, 2) != 'DC'
        GROUP BY
            b.LIFNR, a.MATNR, c.MEINS, d.TAKLV
          """.format(FIELDS, year, month)

    return sql

def process(lshDB, wumartDB, redisCli, data):
    t = int(time.mktime(datetime.datetime.strptime(data, "%Y%m%d").timetuple()))
    sql = formatSql(data)
    wumartDB.execute(sql)

    invInfo = {}
    lastVid = 0
    for row in wumartDB:
        if row[0].strip() == '' or row[5] is None:
            continue
        vid = int(row[0])
        sku_id = row[1]
        unit = row[2]
        qty = row[3]
        ntAmount = row[4]
        tax = row[5]

        if lastVid != vid:
            if lastVid != 0:
                redisCli.hmset('inventory:{0}:{1}'.format(lastVid,t), invInfo)
            invInfo = {}
            lastVid = vid
        itAmount = (Decimal(ntAmount) * (1+Decimal(tax))).quantize(Decimal('0.000'))
        invInfo[sku_id] = json.dumps({'nt_amount':str(ntAmount), 'it_amount':str(itAmount), 'qty':str(qty), 'unit':str(unit)}).encode('utf8')

    if lastVid != 0:
        redisCli.hmset('inventory:{0}:{1}'.format(lastVid,t), invInfo)

if __name__ == "__main__":
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    cli = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db=gConfig['redis_db'])
    wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
    wm_cur = wm_con.cursor()
    #today = datetime.datetime.strptime("20160327", "%Y%m%d")
    today = datetime.date.today()
    date = (today - datetime.timedelta(days=1)).strftime("%Y%m%d")
    process(None, wumartDB = wm_cur, redisCli = cli, data = date)
    
    """
    start = datetime.datetime.strptime("20160303", "%Y%m%d")
    end = datetime.datetime.strptime("20160307", "%Y%m%d")
    date_generated = [start + datetime.timedelta(days=x) for x in range(0, (end-start).days)]

    for date in date_generated:
        process(None, wumartDB = wm_cur, redisCli = cli, data = date.strftime("%Y%m%d"))
    """
