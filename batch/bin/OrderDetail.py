import redis
import traceback
import os
import time
import sys
import threading
sys.path.append('bin/common')
from log import *
import json
from collections import deque

NUM = 20
Q = [deque() for i in range(NUM)]

def dealone(idx, one, r):
    orderId, lineno, name, sku_id, qty, fenzi, fenmu, ntAmount, itAmount, price, tax, barcode =  one
    NOTICE('accept order {0} in dqueue[{1}]'.format(orderId, idx)) 
    r.hset('order:'+orderId, lineno, json.dumps({'name':name.decode('utf8'), 'tax':tax, 'qty':qty, 'price':price, 'it_amount': itAmount, 'nt_amount':ntAmount, 'fenzi':fenzi, 'fenmu':fenmu, 'barcode':barcode}, ensure_ascii = False).encode('utf8'))
    NOTICE('deal order {0} in dqueue[{1}]'.format(orderId, idx)) 


def thread_worker(func, idx,gConfig):
    global Q
    r = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db    =gConfig['redis_db'])
    while True:
        one = {}
        try:
            one = Q[idx].popleft()
            func(idx,one, r)
        except IndexError:
            NOTICE('thread[{0}] finished'.format(idx))
            break
        except:
            ERROR('Except occurr when deal with {0}, detail:{1}'.format(str(one), traceback.format_exc()))

INIT_LOG('POITEM', False)

for line in sys.stdin:
    fields = line[:-1].split("\t")
    orderId, lineno, name, sku_id, qty, fenzi, fenmu, price, ntAmount, itAmount, tax, barcode = \
            fields[0], fields[1], fields[5], fields[6], fields[13], fields[18], fields[19], fields[20], fields[22], fields[23], fields[24], fields[39] 
    if orderId.find('EBELN') != -1:
        continue
    idx = int(orderId) % NUM
    Q[idx].append([orderId, lineno, name, sku_id, qty, fenzi, fenmu, ntAmount, itAmount, price, tax, barcode])
    NOTICE('push order {0} in dqueue[{1}]'.format(line, idx))

task = []
cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
for idx in range(NUM):
    th = threading.Thread(target=thread_worker,args=(dealone, idx,gConfig))
    th.setDaemon(True)
    th.start()
    task.append(th)

for idx in range(NUM):
    task[idx].join()
