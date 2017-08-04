import time
import redis
import re
import json
import sys
import traceback
import threading
from decimal import *
sys.path.append('bin/common')
from log import *
from collections import deque

NUM = 20
Q = [deque() for i in range(NUM)]

def dealone(cli, one, idx):
    NOTICE('accept pohead {0}'.format(one['receipt_id']))
    pdate = int(time.mktime(time.strptime(one['posting_date'],'%Y%m%d')))
    cli.set('posting_date:{0}'.format(one['receipt_id']), pdate)
    NOTICE('deal pohead {0}'.format(one['receipt_id']))


def thread_worker(func,idx,gConfig):
    r = redis.StrictRedis(host=gConfig['redis_host'], port=gConfig['redis_port'], db=gConfig['redis_db'])
    while True:
        one = {}
        try:
            one = Q[idx].popleft()
            func(r, one, idx)
        except IndexError:
            break
        except:
            traceback.print_exc(file=sys.stderr)
            sys.stderr.flush()

INIT_LOG('RECEIVEHEAD', False)

for line in sys.stdin.xreadlines():
    line = line[:-1]
    fields = line.split("\t")
    receiptId, postingDate = fields[0], fields[4]
    if receiptId == 'MBLNR':
        continue
    idx = int(receiptId) % NUM
    Q[idx].append({'receipt_id':receiptId, 'posting_date':postingDate})
    NOTICE('push pohead {0} in dqueue[{1}]'.format(receiptId, idx))

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
