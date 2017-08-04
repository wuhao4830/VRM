# -*- coding: UTF-8 -*-
import json
import time
import datetime
import os
from decimal import *
import MySQLdb
import requests
def checkZeroVal(itAmountNum,gkcNum,gConfig):
    if itAmountNum!=0 and gkcNum!=0:
        return 0
    sendData=u'库存，高库存为零'
    sendMail(sendData,gConfig)
    return -3
def checkHistoryVal(itAmountNum,gkcNum,lastItAmountNum,lastGkcNum,gConfig):
    notNullitAmount = itAmountNum
    lastNotNullitAmount = lastItAmountNum
    notNullgkc = gkcNum
    lastNotNullgkc = lastGkcNum
    if ((1.1 < float(lastNotNullitAmount)/notNullitAmount)  or (0.9 > float(lastNotNullitAmount)/notNullitAmount)):
        sendData=u'库存波动异常'
        sendMail(sendData,gConfig)
        return -1
    if ((1.1 < float(lastNotNullgkc)/notNullgkc) or (0.9 > float(lastNotNullgkc)/notNullgkc)):
        sendData=u'高库存波动异常'
        sendMail(sendData,gConfig)
        return -2
    return 0
def genData(data,cur):
    date = int(time.mktime(datetime.datetime.strptime(data,"%Y%m%d").timetuple()))
    itAmountNum=0
    gkcNum=0
    cur.execute('select count(*) from inventory_overall where it_amount<>0 and date=%s '%date)
    itAmountNums = cur.fetchone()
    itAmountNum=itAmountNums[0]
    cur.execute('select count(*) from inventory_overall where gkc<>0 and date=%s '%date)
    gkcNums = cur.fetchone()
    gkcNum=gkcNums[0]
    return itAmountNum,gkcNum
def checkVidData(data,cur,gConfig):
    date = int(time.mktime(datetime.datetime.strptime(data,"%Y%m%d").timetuple()))
    cur.execute('select vid from inventory_overall where it_amount=0 and gkc<>0 and date=%s '%date)
    vidList=cur.fetchall()
    if len(vidList)==0:
        return None
    else:
        sendData=u'存在高库存不为零，库存为0的情况,vidList:%s'%(json.dumps(vidList,default=lambda obj: obj.__dict__,ensure_ascii=False))
        sendMail(sendData,gConfig)
        return vidList
def process(gConfig):
    con= MySQLdb.connect(host = gConfig['sql_host'], port = gConfig['sql_port'] , user = gConfig['sql_user'], passwd = gConfig['sql_passwd'], db = gConfig['sql_db'], charset="utf8")
    cur = con.cursor()
    today = datetime.date.today()
    date = ( today  - datetime.timedelta(days=1)).strftime("%Y%m%d")
    theLastDate=( today  - datetime.timedelta(days=2)).strftime("%Y%m%d")
    itAmountNum,gkcNum=genData(date,cur)
    lastItAmountNum,lastGkcNum=genData(theLastDate,cur)
    zeroTrue=checkZeroVal(itAmountNum,gkcNum,gConfig)
    historyTure=checkHistoryVal(itAmountNum,gkcNum,lastItAmountNum,lastGkcNum,gConfig)
    vidList=checkVidData(date,cur,gConfig)
    vidTrue=0
    if vidList!=None:
        vidTrue=1
    cur.close()
    con.close()
    if zeroTrue==0 and historyTure==0 and vidTrue==0:
        return 0
    else:
        return -1
def sendMail(sendData,gConfig):
    link = gConfig['mail_link']
    postParam={}
    postParam['tos']='wuhao@lsh123.com,mali@lsh123.com'
    postParam['subject']=u'库存数据倒入异常'
    postParam['content']=sendData
    result=requests.post(link, data=postParam)
if __name__ == "__main__":
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    try:
        isTrue= process(gConfig)
        if isTrue==0:
            print gConfig['rebuild_sh']
            os.system(gConfig['rebuild_sh'])
    except Exception as e:
        print 'error:',e
