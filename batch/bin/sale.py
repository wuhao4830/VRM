# -*- coding: UTF-8 -*-
import json
import time
import re
import pypyodbc
import MySQLdb
import redis
import datetime
from decimal import *
import json

def formatSql(date):
    sqlTpl = """
        SELECT
            b.LIFNR, a.MATNR, a.WERKS, a.SALEAMT1
        FROM 
            F_STOREMERCH_MARGIN as a 
            inner join M_V_SITEARTICLE as b on a.MANDT = b.MANDT and a.matnr = b.matnr and a.werks = b.werks and a.mandt = '300'
            inner join M_V_VENDER as c on b.lifnr = c.lifnr and c.liser = 'X'
        WHERE
            a.SPTAG = '{0}'
        ORDER BY
            b.lifnr, a.matnr, a.werks, a.saleamt1
        """
    return sqlTpl.format(date)

def process(lshDB, wumartDB, redisCli, data):
    sql = formatSql(data)
    wumartDB.execute(sql)
    date = int(time.mktime(datetime.datetime.strptime(data,'%Y%m%d').timetuple()))

    saleInfo = []
    lastVid = 0
    for vid, sku_id, shopId, itAmount in wm_cur:
        vid = int(vid)
        if lastVid != vid:
            if lastVid != 0:
                redisCli.set('saleinfo:{0}:{1}'.format(lastVid, date), json.dumps(saleInfo, ensure_ascii = False).encode('utf8'))
            saleInfo = []
            lastVid = vid
        saleInfo.append({'shop_id':shopId, 'sku_id':sku_id, 'it_amount':str(itAmount)})

    if lastVid != 0:
        redisCli.set('saleinfo:{0}:{1}'.format(lastVid,date), saleInfo)

if __name__ == "__main__":
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    cli = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db=gConfig['redis_db'])
    wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
    wm_cur = wm_con.cursor()
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
