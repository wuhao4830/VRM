# -*- coding: UTF-8 -*-
import re
import time
import pypyodbc
import MySQLdb
import json

wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
wm_cur = wm_con.cursor()

cfg = json.loads(open("conf/batch.conf").read())
gConfig=cfg[cfg['env']]
lsh_con= MySQLdb.connect(host=gConfig['sql_host'], port = gConfig['sql_port'], user=gConfig['sql_user'], passwd=gConfig['sql_passwd'], db =gConfig['sql_db'], charset="utf8")
lsh_cur = lsh_con.cursor()

sql = "select LIFNR,NAME1,STRAS,LINKMAN,TELF1, CATSS, KONZS,SETTLYDAYS, VGROUP, CONTROLSTOCK, EMAIL,BANKL,BANKN, BANKA, KOINH, LISER, SWIFT, YFBZ, BNKLZ,FAX_NUMBER,STCD1, ZTERM, EKGRP, PROVZ FROM m_v_vender where mandt = 300"
wm_cur.execute(sql)

mainList = []
payList = []
for vid,name,address,person,phone,status,companyId,settleDays,vGrp,ctrlStock,pGrp,bankCode,bankAccount,bankName, \
        bankAccountName,LISER,cityCode,YFBZ,bnklz,fax,tax_no, zterm, topCat, PROVZ in wm_cur:
           if re.match(r'^DC', vid):
               vid = vid[2:]
           if status.strip() == '':
               continue
           vid = int(vid)
           vType = 1 if LISER.strip() == '' else 2
           prepayFlag = 0 if YFBZ.strip() == '' else 1
           control = 0x06 if ctrlStock == '是' else 0x00
           if vGrp.strip() == '':
               vGrp = 0
           else:
               vGrp = int(vGrp[1:])
           if bankName is None:
               bankName = ''
           if bankAccountName is None:
               bankAccountName = ''
           if bankAccount is None:
               bankAccount = ''
           if bankCode is None:
               bankCode = ''
           if cityCode is None:
               cityCode = ''
           if bnklz is None:
               bnklz = ''
           tax_no = tax_no.decode('utf8')
           bank_zone_code = PROVZ 
           now  = int(time.time())
           mainList.append([vid, name, address, phone, int(status.strip()), pGrp, fax, person, vType, topCat, now, now])
           payList.append([vid, vGrp, companyId, tax_no, bankName, bankAccount, bankAccountName, bnklz, bankCode, cityCode, settleDays, prepayFlag,  zterm, now, now, bank_zone_code])
            

mainSql = """
insert into vender_main(vid,name,address,phone,status,purchase_grp,fax,contact_person,type, top_cat, created_at, updated_at)
values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
on duplicate key update `top_cat` = VALUES(top_cat), `updated_at` = VALUES(updated_at), `vid`= VALUES(vid), `name` = VALUES(name), `address` = VALUES(address), `phone` = VALUES(phone), `status` = VALUES(status), `purchase_grp` = VALUES(purchase_grp), `fax` = VALUES(fax), `contact_person` = VALUES(contact_person), `type` = VALUES(type)
"""

paySql = """
insert into vender_pay(vid,vender_grp, company_id,tax_no, bank_name, bank_account, bank_account_name, bank_unit_code, bank_code, bank_city_code, settle_days, prepay_flag,  payment_period, created_at, updated_at, bank_zone_code) 
values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
on duplicate key update `updated_at` =VALUES(updated_at), `vid` = VALUES(vid), `vender_grp` = VALUES(vender_grp), `company_id` = VALUES(company_id), 
`tax_no` = VALUES(tax_no), `bank_name`=VALUES(bank_name), `bank_account` = VALUES(bank_account), `bank_account_name`=VALUES(bank_account_name), `bank_city_code`=VALUES(bank_city_code), `bank_code`=VALUES(bank_code), `bank_unit_code`=VALUES(bank_unit_code), `settle_days`=VALUES(settle_days), `prepay_flag`=VALUES(prepay_flag), `payment_period` = VALUES(payment_period),`bank_zone_code`=VALUES(bank_zone_code)
"""

lsh_cur.executemany(mainSql, mainList)
lsh_cur.executemany(paySql, payList)
lsh_con.commit()

