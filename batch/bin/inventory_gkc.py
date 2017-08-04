# -*- coding: UTF-8 -*-
import time
import datetime
import re
import pypyodbc
import MySQLdb
from decimal import *
import json

def formatSql(date):
    sql = """
            SELECT
                a.MATNR,LIFNR,a.PUUNIT,ZQMKC_JE,ZQMKC_SL,ZLBKUM,ZZTS,text03,TEXT01,b.taklv
            FROM
                mrpt_s.dbo.S_REP_ZFIGKCZZ as a
                left join M_V_ARTICLE as b on a.mandt = b.mandt and a.matnr = b.matnr
            WHERE
                a.MANDT = '300'
                and a.TEXT02 <> 'D'
                and ZDATE = '{0}'
            """.format(date)
    return sql

def process(lshDB, wumartDB, redisCli, data):
    sql = formatSql(data)
    wm_cur.execute(sql)

    l = []
    cnt = 0
    zdate = int(time.mktime((datetime.datetime.strptime(data, "%Y%m%d") - datetime.timedelta(days=1)).timetuple()))
    now = int(time.time())

    for sku_id,vid,uk,ntAmount,Qty,avgSale,toTarget,toReal,total,tax in wm_cur:
        vid = int(vid)
        itAmount = (Decimal(ntAmount) * (1+Decimal(tax))).quantize(Decimal('0.000'))
        l.append([zdate, sku_id, uk, ntAmount, Qty, avgSale, toTarget, toReal, total, vid, now, now, str(itAmount)])

    lsh_cur.executemany(
        """insert into inventory_gkc(date,sku_id,top_cat,nt_amount,qty,avg_sale,to_target,to_real,qty_overall,vid,updated_at,created_at,it_amount)
            values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
            on duplicate key update `top_cat`=VALUES(top_cat), `nt_amount`=VALUES(nt_amount), `qty`=VALUES(qty), `avg_sale`=VALUES(avg_sale), `to_target`=VALUES(to_target), `to_real`=VALUES(to_real), `qty_overall`=VALUES(qty_overall), `it_amount`=VALUES(it_amount), `updated_at` = VALUES(updated_at)""", l)
    lsh_con.commit()

if __name__ == "__main__":
    wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
    wm_cur = wm_con.cursor()
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    lsh_con =MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
    lsh_cur = lsh_con.cursor()

    today = datetime.datetime.now()
    #today = datetime.datetime.strptime("20160327", "%Y%m%d")
    date = today.strftime("%Y%m%d")
    process(lshDB = lsh_cur, wumartDB = wm_cur, redisCli = None, data = date)
    
    """
    start = datetime.datetime.strptime("20160303", "%Y%m%d")
    end = datetime.datetime.strptime("20160307", "%Y%m%d")
    date_generated = [start + datetime.timedelta(days=x) for x in range(0, (end-start).days)]

    for date in date_generated:
        process(lshDB = lsh_cur, wumartDB = wm_cur, redisCli = None, data = date.strftime("%Y%m%d"))
    """
