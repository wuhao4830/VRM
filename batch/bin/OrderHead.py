import redis
import traceback
import os
import time
import sys
import threading
sys.path.append('bin/common')
sys.path.append('common')
from log import *
from collections import deque
import json


NUM = 20
Q = [deque() for i in range(NUM)]

typeDict = {'ZNBA':1,'ZNBD':2, 'ZNMB':3,'ZNSP':6, 'ZNZK':4, 'ZNLY':7}

def dealone(r, one, idx):
    global typeDict
    ebeln, bukrs, bstyp,bsart, statu, aedat, lifnr = one
    NOTICE('accept order {0}'.format(ebeln))
    orderType = -1 if bsart not in typeDict else typeDict[bsart]
    r.hmset('order:'+ ebeln, {'date': aedat, 'type': orderType})
    NOTICE('deal order {0}'.format(ebeln))

def thread_worker(func,idx,gConfig):
    global Q
    r = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db=gConfig['redis_db'])
    while True:
        one = {}
        try:
            one = Q[idx].popleft()
            func(r, one, idx)
        except IndexError:
            NOTICE('thread[{0}] has finished'.format(idx))
            break
        except:
            ERROR('Except occur with {0}, detail:{1}'.format(str(one), traceback.format_exc()))

INIT_LOG('POHEAD', False)

for line in sys.stdin.xreadlines():
    ebeln, bukrs, bstyp,bsart, statu, aedat, lifnr = line[:-1].split("\t")
    if ebeln == 'EBELN':
        continue
    idx = int(ebeln) % NUM
    Q[idx].append([ebeln,bukrs,bstyp, bsart, statu, aedat, lifnr])
    NOTICE('push order {0} in dqueue[{1}]'.format(line, idx))

task = []
cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
for i in range(NUM):
    th = threading.Thread(target=thread_worker,args=(dealone,i,gConfig))
    th.setDaemon(True)
    th.start()
    task.append(th)

for idx in range(NUM):
    task[idx].join()
