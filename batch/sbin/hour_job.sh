#!/bin/sh

function find()
{
    fileList=`ls -rt data/$1*`
}

function log()
{
    timestamp=`date +"[%Y-%m-%d %H:%M:%S]"`
    echo -e "${timestamp} $1"
}

find 'POHEAD'
for file in ${fileList[@]}  
do  
    log "start dealing with ${file}" 
    iconv -futf16 -tutf8 ${file} |python bin/OrderHead.py
    if [[ $? -ne 0 ]]
    then
        log "deal with $file failed, so exit"
        exit -1
    fi
    mv $file data/history
done  

find 'POITEM'
for file in ${fileList[@]}  
do  
    log "start dealing with ${file}" 
    iconv -futf16 -tutf8 ${file} |python bin/OrderDetail.py
    if [[ $? -ne 0 ]]
    then
        log "deal with $file failed, so exit"
        exit -1
    fi
    mv $file data/history
done

find 'COSTCONFIRMATION'
for file in ${fileList[@]}  
do  
    log "start dealing with ${file}" 
    python bin/fare.py ${file}
    if [[ $? -ne 0 ]]
    then
        log "deal with $file failed, so exit"
        exit -1
    fi
    mv $file data/history
done  

find 'ZCMRJDS'
for file in ${fileList[@]}  
do  
    log "start dealing with ${file}" 
    python bin/sale_agreement.py ${file}
    if [[ $? -ne 0 ]]
    then
        log "deal with $file failed, so exit"
        exit -1
    fi
    mv $file data/history
done  

find 'FXMX'
for file in ${fileList[@]}  
do  
    log "start dealing with ${file}" 
    python bin/envidence_record_fk.py ${file}
    if [[ $? -ne 0 ]]
    then
        log "deal with $file failed, so exit"
        exit -1
    fi
    mv $file data/history
done  

find 'FPMX'
for file in ${fileList[@]}  
do  
    log "start dealing with ${file}" 
    python bin/envidence_record_fp.py ${file}
    if [[ $? -ne 0 ]]
    then
        log "deal with $file failed, so exit"
        exit -1
    fi
    mv $file data/history
done  

