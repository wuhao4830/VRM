# -*- coding: UTF-8 -*-
import redis
import datetime
import time
import re
import MySQLdb
import json
import sys
import traceback
from decimal import *
sys.path.append('bin/common')
from log import *
import json
import threading
from collections import deque

taxDict={ 
        'J0':Decimal('1.00'), 
        'J1':Decimal('1.17'), 
        'J2':Decimal('1.13'), 
        'J3':Decimal('1.13'), 
        'J4':Decimal('1.07'), 
        'J5':Decimal('1.06'), 
        'J6':Decimal('1.04'), 
        'J7':Decimal('1.03'), 
        'J8':Decimal('1.13') 
        }

NUM = 20
Q = [deque() for i in range(NUM)]
L = [[] for i in range(NUM)]

def dec2Str(number):
    return str(number.quantize(Decimal('0.0000')))

def dealOne(tIdx, one, cli):
    global Q,L,NUM
    detailList = one
    header = {  \
            'receipt_id':detailList[0]['receipt_id'], \
            'it_amount':Decimal('0.000'), \
            'nt_amount':Decimal('0.000'), \
            'vid' : detailList[0]['vid'], \
            'shop_id': detailList[0]['shop_id'], \
            'company_id': detailList[0]['company_id'], \
            'order_id': detailList[0]['order_id'] \
             }
    NOTICE('accept one {0}'.format(header['receipt_id']))
    header['posting_date'] = cli.get('posting_date:{0}'.format(header['receipt_id']))
    order = cli.hgetall('order:{0}'.format(header['order_id']))
    if order is None or 'type' not in order:
        WARNING('no order_info for receipt[{0}], orderId: {1}'.format(header['receipt_id'], header['order_id']))
        return
    if int(order['type']) == -1:
        NOTICE("order type we do not care receipt:{0}".format(header['receipt_id']))
        return
    if int(order['type']) in [1,2] and int(detailList[0]['movement_type']) in [102,162]:
        order['type'] = 5

    taxInfo = {}
    for idx in range(0,9):
        taxInfo['J{0}'.format(idx)] = {'it_amount': '0.0000', 'nt_amount': '0.0000'}

    detailDict = {}
    for detail in detailList:
        orderDetail = json.loads(order[detail['order_lineno']])
        """
            数量 = 订货数量 * 包装分子 / 包装分母
            含税单价 = 含税金额 / 数量
            未税金额 = 含税单价 *  收货数量 / ( 1+税率) 
            税额 = 未税金额 * 税率
            含税金额 = 未税金额 + 税额
            
        """
        taxCode = 'J0' if orderDetail['tax'].strip() == '' else orderDetail['tax']
        direction = Decimal('1.0000') if (detail['movement_type'] in ['101','162']) else Decimal('-1.0000')
        detail['tax_code'] = taxCode
        detail['barcode'] = orderDetail['barcode']
        detail['name'] = orderDetail['name']
        if Decimal(orderDetail['qty']) == Decimal('0.0000'):
            detail['it_price'] = '0.0000'
            detail['nt_amount'] = '0.0000'
            detail['it_amount'] = '0.0000'
        elif Decimal(orderDetail['fenzi']) == Decimal('0.0000') or Decimal(orderDetail['fenmu']) == Decimal('0.0000'):
            detail['it_price'] =  dec2Str( Decimal(orderDetail['it_amount']) / Decimal(orderDetail['qty']) )
            detail['nt_amount'] = dec2Str( direction * Decimal(orderDetail['it_amount']) / Decimal(orderDetail['qty']) * Decimal(detail['qty']) / Decimal(taxDict[taxCode]) )
            detail['it_amount'] = dec2Str( Decimal(detail['nt_amount']) * Decimal(taxDict[taxCode]) )
        else:
            detail['it_price'] =  dec2Str( Decimal(orderDetail['it_amount']) / (Decimal(orderDetail['qty']) * Decimal(orderDetail['fenzi']) / Decimal(orderDetail['fenmu']) ) )
            detail['nt_amount'] = dec2Str( direction * Decimal(orderDetail['it_amount']) / (Decimal(orderDetail['qty']) * Decimal(orderDetail['fenzi']) / Decimal(orderDetail['fenmu']) ) * Decimal(detail['qty']) / Decimal(taxDict[taxCode]) )
            detail['it_amount'] = dec2Str( Decimal(detail['nt_amount']) * Decimal(taxDict[taxCode]) )
        taxInfo[taxCode]['nt_amount'] = dec2Str( Decimal(taxInfo[taxCode]['nt_amount']) + Decimal(detail['nt_amount']))
        taxInfo[taxCode]['it_amount'] = dec2Str( Decimal(taxInfo[taxCode]['it_amount']) + Decimal(detail['it_amount']))
        detailDict[detail['line_no']] = json.dumps(detail, ensure_ascii=False).encode('utf8')
    finalTaxInfo = {}
    for taxCode in taxInfo:
        ntAmount = Decimal(taxInfo[taxCode]['nt_amount'])
        itAmount = (ntAmount * taxDict[taxCode]).quantize(Decimal('0.0000'))
        if ntAmount != Decimal('0.0000'):
            finalTaxInfo[taxCode] = {'nt_amount':str(ntAmount), 'it_amount':str(itAmount)}
        header['it_amount'] = dec2Str(Decimal(header['it_amount']) + itAmount)
        header['nt_amount'] = dec2Str(Decimal(header['nt_amount']) + ntAmount)
    header['tax_info'] = json.dumps(finalTaxInfo)
    cli.hmset('receipt:{0}'.format(header['receipt_id']), detailDict)
    L[tIdx].append([ \
            header['posting_date'], header['receipt_id'], header['vid'], header['shop_id'], header['company_id'], \
            header['tax_info'], header['it_amount'], header['nt_amount'], header['order_id'], \
            int(time.mktime(datetime.datetime.strptime(order['date'], "%Y%m%d").timetuple())), order['type'], time.time(), time.time() \
            ])
    NOTICE('deal one {0}'.format(header['receipt_id']))

sql = """
INSERT INTO receipt (posting_date, receipt_id, vid, shop_id, company_id, tax_info, it_amount, nt_amount, order_id, order_date, type, created_at, updated_at) 
VALUES (%s, %s,%s,%s,%s,%s,%s, %s,%s,%s,%s,%s,%s) 
ON DUPLICATE KEY UPDATE `posting_date` = VALUES(posting_date), `vid` = VALUES(vid), `shop_id` = VALUES(shop_id), `company_id` = VALUES(company_id), `type` = VALUES(type),  `nt_amount` = VALUES(nt_amount), `it_amount` = VALUES(it_amount), `tax_info` = VALUES(tax_info), `order_date`  = VALUES(order_date), `updated_at` = VALUES(updated_at)
"""

def thread_worker(func, idx,gConfig):
    global conDict, sql,L, curDict
    cli = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db=gConfig['redis_db'])
    con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")	
    cursor = con.cursor()
    while True:
        one = {}
        try:
            one = Q[idx].popleft() 
            func(idx, one, cli)
            if len(L[idx]) == 1000:
                cursor.executemany(sql, L[idx])
                con.commit()
                NOTICE('thread[{0}] executemany {1}'.format(idx, len(L[idx])))
                L[idx] = []
        except IndexError:
            cursor.executemany(sql, L[idx])
            con.commit()
            NOTICE('thread[{0}] executemany on Final {1}'.format(idx, len(L[idx])))
            break
        except:
            ERROR('Except occurr when deal with {0}, detail:{1}'.format(str(one), traceback.format_exc()))

# init log
INIT_LOG('RECEIVEITEM', False)

lastId = ''
detailList = [] 
for line in sys.stdin:
    fields = line[:-1].split("\t")
    receiptId, lineNo, moveType, skuId, shopId, storageId, vId, ntAmount, qty, unit, orderId, orderLineNo, allReceived, companyId = \
            fields[0], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[9], fields[10], fields[11], fields[12], fields[13], fields[14], fields[15]

    if receiptId == 'MBLNR' or moveType not in ['101', '102', '161', '162'] or not re.match(r'^0', vId):
        continue
    if lastId != receiptId:
        if len(detailList) > 0:
            #idx = int(lastId) % NUM
            idx = int(vId) % NUM
            NOTICE('push receiveitem:{0} into Q[{1}]'.format(lastId, idx))
            Q[idx].append(detailList)
        detailList = []
        lastId = receiptId
    detailList.append({ \
            'receipt_id':receiptId, 'line_no': lineNo, 'movement_type':moveType, 'sku_id':skuId, \
            'shop_id':shopId, 'storage_id':storageId, 'vid':int(vId.lstrip('0')), 'nt_amount':ntAmount, \
            'unit':unit, 'order_id':orderId, 'order_lineno':orderLineNo, 'all_received':allReceived, \
            'company_id':companyId, 'qty':qty })

#for last one
idx = int(receiptId) % NUM
Q[idx].append(detailList)
NOTICE('push in Q one {0}'.format(lastId))
print 'all detail push in Q'

cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
task = []
# start worker    
for idx in range(NUM):
    th = threading.Thread(target=thread_worker,args=(dealOne, idx,gConfig))
    th.setDaemon(True)
    th.start()
    task.append(th)

# wait for task finished
for idx in range(NUM):
    task[idx].join()
