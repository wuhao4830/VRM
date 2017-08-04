# -*- coding: UTF-8 -*-
import MySQLdb
from xml.dom.minidom import parse
from decimal import *
import xml.dom.minidom
import time
import sys
import datetime
import random
import json

def genId():
    t = datetime.datetime.now()
    sec = t.second
    usec = t.microsecond & 0xFFFF
    rand = random.randint(0,2147483647)
    result = rand + (sec << 15) + (usec << 47)
    return result

def getValue(item, tagName):
    value = ''
    elements = item.getElementsByTagName(tagName) 
    if elements and elements[0].childNodes:
        value = item.getElementsByTagName(tagName)[0].childNodes[0].data
    return value
def getStoreNo(item):
    tagName='ZUONR'
    value = 0
    elements = item.getElementsByTagName(tagName)
    if elements and elements[0].childNodes:
        tmpVal = item.getElementsByTagName(tagName)[0].childNodes[0].data
        if tmpVal!=None and tmpVal[0]=='C':
            value=int(tmpVal[4:8])
    return value
def process(db, redisCli, data):
    selectSql='select fare_id,company_id,agreement_id,date,line_no,profit_center,is_calc from fare'
    idDict = {}
    cursor.execute(selectSql)
    for fare_id,company_id,agreement_id,date,line_no,profit_center,is_calc in cursor:
        key='%s:%s:%s:%s:%s'%(company_id,agreement_id,date,line_no,profit_center)
        idDict[key] = '%s:%s'%(fare_id,is_calc)
    L = []
    L_ONE=[]

    DOMTree = xml.dom.minidom.parse(data)
    collection = DOMTree.documentElement
    itemList = collection.getElementsByTagName("Items")
    
    for item in itemList:
        now = int(time.time())
        createDate = int(time.mktime(time.strptime(getValue(item, 'BUDAT'), '%Y%m%d')))
        AUGDT = getValue(item, 'AUGDT')
        clearDate = 0 if AUGDT.lstrip('0') == '' else int(time.mktime(time.strptime(AUGDT, '%Y%m%d')))
        int(time.mktime(time.strptime(getValue(item, 'BUDAT'), '%Y%m%d')))
        BUKRS = getValue(item, 'BUKRS')
        BELNR = getValue(item, 'BELNR')
        isClear = 0 if getValue(item, 'AUGBL') == '' else 1
        ZUONR=getValue(item, 'ZUONR')
        FYXYH=getValue(item, 'FYXYH')
        key='%s:%s:%s:%s:%s'%(BUKRS,FYXYH,createDate,BELNR,ZUONR)
        fare_id=''
        is_calc=0
        if idDict.get(key)==None:
            fare_id=genId()
        else:
            tmpList=idDict[key].split(':')
            fare_id=tmpList[0]
            is_calc=int(tmpList[1])
        if is_calc!=0:
            L_ONE.append([\
                    isClear, \
                    now , \
                    fare_id
                    ])
        else:
            L.append([ \
                    getValue(item, 'LIFNR').lstrip('0'), \
                    createDate, \
                    createDate, \
                    BUKRS, \
                    BELNR, \
                    FYXYH, \
                    getValue(item, 'KSCHL'), \
                    getValue(item, 'FYJE'), \
                    getValue(item, 'WSJE'), \
                    getValue(item, 'QRRQ'), \
                    ZUONR, \
                    fare_id, \
                    getStoreNo(item), \
                    clearDate, \
                    isClear, \
                    now, \
                    now, \
                    getValue(item, 'FYXM'), \
                    0 if Decimal(getValue(item, 'FYJE')) >= Decimal('0.00') else 1
                    ])

    sql = """
    insert into fare(vid, date,cal_date, company_id, line_no, agreement_id, code, it_amount, nt_amount, comment,profit_center,fare_id,shop_id,clear_date, is_clear, created_at, updated_at, name, is_freeze) 
    values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
    on duplicate key update vid=VALUES(vid), code=VALUES(code), it_amount=VALUES(it_amount), nt_amount=VALUES(nt_amount), comment=VALUES(comment), clear_date=VALUES(clear_date), is_clear=VALUES(is_clear), updated_at=VALUES(updated_at), name=VALUES(name),is_freeze=VALUES(is_freeze) """
    cursor.executemany(sql, L)
    updateSql= """
        update fare set is_clear = %s,updated_at=%s where fare_id=%s
        """
    cursor.executemany(updateSql, L_ONE)
    con.commit()

if __name__ == '__main__':
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")	
    cursor = con.cursor()
    try:
        process(db = cursor, redisCli = None, data = sys.argv[1])
    except Exception,e:
        print e
        con.rollback()
    cursor.close()
    con.close()

