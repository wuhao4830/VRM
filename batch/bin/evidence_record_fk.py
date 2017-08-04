# -*- coding: UTF-8 -*-
import sys
import MySQLdb
import time
import datetime
import json
try:
    cfg = json.loads(open("conf/batch.conf").read())
    gConfig=cfg[cfg['env']]
    con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
    cur = con.cursor()
except Exception:
    print "no connect!"
    sys.exit()

file = open(str(sys.argv[1]))
lines = file.readlines()
FK = []
for e in lines[1:]:
    s = e.strip("\r\n")
    try:
        p = s.split(',')
        pay_id         = p[0]
        bill_id        = p[1]
        settle_id      = p[2]
        invoice_code   = p[3]
        invoice_number = p[4]
        evidence_id    = p[5]
        evidence_date  = p[6]
    except Exception:
        print "fromat error",e
        cur.close()
        con.close()
        sys.exit()

    created_at = time.time()
    updated_at = time.time()
    evidence_date = time.mktime(datetime.datetime.strptime(evidence_date, "%Y%m%d").timetuple())
    tmp = (pay_id, bill_id, settle_id, invoice_code, invoice_number, evidence_id, evidence_date, created_at, updated_at, pay_id, bill_id, settle_id, invoice_code, evidence_date, updated_at)
    FK.append(tmp)

sql =  """insert into pay_evidence(pay_id,bill_id,settle_id,invoice_code,invoice_number,evidence_id,evidence_date,created_at,updated_at)values(%s,%s,%s,%s,%s,%s,%s,%s,%s) ON DUPLICATE KEY UPDATE pay_id = %s, bill_id = %s, settle_id = %s, invoice_code = %s, evidence_date = %s, updated_at = %s"""
try:
    cur.executemany(sql, FK)
except Exception:
    print "insert failed :", sql
    cur.close()
    con.close()
    sys.exit()

con.commit()
cur.close()
con.close()
