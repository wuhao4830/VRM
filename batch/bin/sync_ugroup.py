#coding=utf-8
import MySQLdb
import pypyodbc
import sys
import time
import json
reload(sys)
sys.setdefaultencoding("utf-8")

con= MySQLdb.connect(host='192.168.21.57', port = 3317, user='root',passwd='', db ='lsh_base', charset="utf8")
cursor = con.cursor()

wm_con=pypyodbc.connect('DSN=wumart;uid=lscx;pwd=lscx;')
wm_cur = wm_con.cursor()

sync_ugroup_update = []
sync_ugroup_insert = []
sync_ugroup_delete = []
update_record      = []
change_list        = []
lsh_dict           = {}
wumart_dict        = {}

#get base info
try:
    for (itemId, ugroup, status) in wm_cur.execute("select matnr, asort, mmsta from M_V_UKZ_MMSTA where MANDT = '300'"):
        key = (itemId + ugroup).strip()
        wumart_dict[key] = [itemId.strip(), ugroup, status]
    print 'get wumart data success'
    
    cursor.execute("select sku_id from item_update_status where market_id = 1  and status <> 1")
    for (sku_id,) in cursor:
        change_list.append(sku_id.strip())
    print 'get change list success'
    
    cursor.execute("select sku_id, ugroup, sap_status from item_ugroup where status = 1 and market_id = '1'")
    for (sku_id, ugroup, sap_status) in cursor:
        key = (sku_id + ugroup).strip()
        lsh_dict[key] = [sku_id.strip(), ugroup, sap_status]
    print 'get lsh data success'
except:
    print 'init base data failed'
    cursor.close()
    wm_cur.close()
    con.close()
    wm_con.close()
    sys.exit()

#process data
try:
    for key in wumart_dict:
        if  wumart_dict[key][0] in change_list:
            print '{0} in progress, skipped'.format(key)
            continue        

        if key in lsh_dict:
            if wumart_dict[key][2].strip() != lsh_dict[key][2].strip():
                print 'UPDATE', lsh_dict[key], wumart_dict[key] 
                sap_ugroup = wumart_dict[key][1]
                sap_status = wumart_dict[key][2]
                updated_at = time.time()
                s = json.dumps({"ori_status":{"title":lsh_dict[key][1],"value":lsh_dict[key][2]},"new_status":{"title":sap_ugroup,"value":sap_status}},ensure_ascii=False)

                update_record.append([wumart_dict[key][0], 1, sap_ugroup, 'sync_ugroup', 2, 'sync_ugroup', s, time.time()])
                sync_ugroup_update.append([sap_ugroup, sap_status, updated_at, wumart_dict[key][0], wumart_dict[key][1]])
        else:
            print 'INSERT', wumart_dict[key]
            sku_id        = wumart_dict[key][0]
            market_id     = 1
            sap_ugroup    = wumart_dict[key][1]
            sap_status    = wumart_dict[key][2]
            status        = 1
            is_valid      = 1
            ugroup_sys    = ''
            shelf_num_sys = ''
            shelf_num     = ''
            shelf_pos     = 0
            bidding_id    = 0
            item_id       = 0
            created_at    = time.time()
            updated_at    = time.time()
                   
            tmp = (sku_id, market_id, sap_ugroup, sap_status, status, is_valid, ugroup_sys, shelf_num_sys, shelf_num, shelf_pos, bidding_id, item_id, created_at, updated_at)
            sync_ugroup_insert.append(tmp)
            
            s = json.dumps({"ori_status":{"title":sap_ugroup,"value":sap_status},"new_status":{"title":sap_ugroup,"value":sap_status}},ensure_ascii=False)
            update_record.append([sku_id, 1, sap_ugroup, 'sync_ugroup', 1, 'sync_ugroup', s, time.time()])
    for key in lsh_dict:
        if key not in wumart_dict and lsh_dict[key][0] not in change_list[]:
            print 'DELETE', lsh_dict[key]
            sync_ugroup_delete.append([lsh_dict[key][0], lsh_dict[key][1]])
            s = json.dumps({"ori_status":{"title":lsh_dict[key][1],"value":lsh_dict[key][2]},"new_status":{"title":lsh_dict[key][1],"value":lsh_dict[key][2]}},ensure_ascii=False)
            update_record.append([lsh_dict[key][0], 1, lsh_dict[key][1], 'sync_ugroup', 1, 'sync_ugroup', s, time.time()])

    delete_sql = """update item_ugroup set status = 0 where sku_id = %s and ugroup = %s and market_id = 1"""
    insert_sql = """insert into item_ugroup (sku_id, market_id, ugroup, sap_status, status, is_valid, ugroup_sys, shelf_num_sys, shelf_num, shelf_pos,bidding_id,item_id, created_at, updated_at)values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"""
    update_sql = """update item_ugroup set ugroup = %s, sap_status = %s, updated_at = %s where sku_id = %s and ugroup = %s and market_id = 1 and status = 1"""
    record_sql = """insert into item_update_record(sku_id, market_id, ugroup, username, type, action, data, created_at) values(%s,%s,%s,%s,%s,%s,%s,%s)"""
    
    cursor.executemany(update_sql, sync_ugroup_update)
    print 'process ugroup update end'
    cursor.executemany(insert_sql, sync_ugroup_insert)
    print 'process ugroup insert end'
    cursor.executemany(record_sql, update_record)
    print 'process record end'
    cursor.executemany(delete_sql, sync_ugroup_delete)
    print 'process delete end'

except Exception :
    print 'process sync ugroup failed'
    con.rollback()
    cursor.close()
    wm_cur.close()
    wm_con.close()
    con.close()
    sys.exit()


print 'process sync ugroup success'
con.commit()
cursor.close()
wm_cur.close()
con.close()
wm_con.close()




