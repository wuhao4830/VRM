#!/bin/sh

source /home/vrm/.bash_profile

yesterday=`date --date="yesterday" +"%Y%m%d"`

function log()
{
    timestamp=`date +"[%Y-%m-%d %H:%M:%S]"`
    echo -e "${timestamp} $1"
}

log "Daily Job Begin"

log "Start sale"
python bin/sale.py
if [[ $? -ne 0 ]]
then
    log "sale failed, so exit"
fi

log "Start inventory_gkc"
python bin/inventory_gkc.py
if [[ $? -ne 0 ]]
then
    log "inventory_gkc failed, so exit"
fi

log "Start inventory"
python bin/inventory.py
if [[ $? -ne 0 ]]
then
    log "inventory failed, so exit"
fi

log "Start inventory_overall"
python bin/inventory_overall.py
if [[ $? -ne 0 ]]
then
    log "inventory_overall failed, so exit"
fi

log "Start vender"
python bin/vender.py
if [[ $? -ne 0 ]]
then
    log "vender failed, so exit"
fi

log "Start prepay"
python bin/prepay.py
if [[ $? -ne 0 ]]
then
    log "prepay failed, so exit"
fi

log "Start Receipt Head"
iconv -futf16 -tutf8 data/RECEIVEHEAD_${yeserday}* |python bin/ReceiptHead.py
if [[ $? -ne 0 ]]
then
    log "receipt head failed, so exit"
    exit -1
fi
mv data/RECEIVEHEAD_${yesterday}* data/history

log "Start Receipt Detail"
iconv -futf16 -tutf8 data/RECEIVEITEM_${yesterday}* |python bin/Receipt.py
if [[ $? -ne 0 ]]
then
    log "receipt detail failed, so exit"
    exit -1
fi
mv data/RECEIVEITEM_${yesterday}* data/history

log "Start check"
python bin/checkInventory.py 
if [[ $? -ne 0 ]]
then
    log "check failed, so exit"
fi

log "Daily Job End"
