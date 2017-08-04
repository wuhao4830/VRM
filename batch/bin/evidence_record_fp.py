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
FP = []
for e in lines[1:]:
    
    s = e.strip("\r\n")
    try:
        p = s.split(',')
        settle_id      = p[0]   
        invoice_number = p[1]
        invoice_code   = p[2]
        tax_type       = p[3]

        evidence_id    = p[4]
        evidence_date  = p[5]
    except Exception:
        print "format error",e
        cur.close()
        con.close()
        sys.exit()
   
    evidence_date = time.mktime(datetime.datetime.strptime(str(evidence_date), "%Y%m%d").timetuple())
    created_at = time.time()
    updated_at = time.time()
    tmp = (settle_id, invoice_number, invoice_code, tax_type, evidence_id, evidence_date, created_at, updated_at, settle_id, evidence_id, evidence_date, updated_at)
    FP.append(tmp)

sql = """insert into invoice_evidence(settle_id,invoice_number,invoice_code,tax_type,evidence_id,evidence_date,created_at,updated_at)values(%s,%s,%s,%s,%s,%s,%s,%s) ON DUPLICATE KEY UPDATE settle_id = %s, evidence_id = %s, evidence_date = %s, updated_at = %s"""
    
try:
    cur.executemany(sql, FP)
except Exception:
    print "sql failed",sql
    cur.close()
    con.close()
    sys.exit()
   
con.commit()
cur.close()
con.close()
