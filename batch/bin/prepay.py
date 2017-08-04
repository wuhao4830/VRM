# -*- coding: UTF-8 -*-
import random
import json
import time
import datetime
import re
import pypyodbc
import MySQLdb
from decimal import *
    
def genId():
    t = datetime.datetime.now()
    sec = t.second
    usec = t.microsecond & 0xFFFF
    rand = random.randint(0,2147483647)
    result = rand + (sec << 15) + (usec << 47)
    return result

def formatSql(date):
    sql = """
            select 
                Mandt '大区号',bukrs '公司代码',lifnr '供商代码',gjahr '年度',belnr '凭证号',
                buzei '凭证行项目号',budat'凭证日期',dmbtr'金额',sgtxt'备注'
            from F_V_ZPREPAY
            """.format(date)
    return sql

def process(lshDB, wumartDB, redisCli, data):
    sql = 'select prepay_id, company_id, year, document_id, line_no from prepay_info'
    idDict = {}
    lshDB.execute(sql)
    for prepayId, companyId, year, docId, lineNo in lshDB:
        key = '{0}:{1}:{2};{3}'.format(companyId, year, docId, lineNo) 
        idDict[key] = prepayId

    sql = formatSql(data)
    wumartDB.execute(sql)

    l = []
    now = int(time.time())
    for mandt, bukrs, lifnr, gjahr, belnr, buzei, budat, dmbtr, sgtxt in wm_cur:
        if mandt != '300':
            continue
        key = '{0}:{1}:{2};{3}'.format(bukrs, gjahr, belnr, buzei) 
        if key in idDict:
            continue
        prepayId = genId()
        date = int(time.mktime(time.strptime(budat, '%Y%m%d')))
        l.append([prepayId, int(lifnr), bukrs, gjahr, belnr, buzei, date, dmbtr, sgtxt, now, now])
 
    lsh_cur.executemany(
        """insert into prepay_info(prepay_id, vid, company_id, year, document_id, line_no, date, it_amount, comment, created_at, updated_at)
            values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
            on duplicate key update `date`=VALUES(date), `it_amount`=VALUES(it_amount), `comment`=VALUES(comment), `updated_at` = VALUES(updated_at)""", l)
    lsh_con.commit()

if __name__ == "__main__":
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
    wm_cur = wm_con.cursor()
    lsh_con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
    lsh_cur = lsh_con.cursor()

    process(lshDB = lsh_cur, wumartDB = wm_cur, redisCli = None, data = None)
