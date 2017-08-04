#!/bin/bash

APP="updateBarcodeJobs"
PHOME=$(dirname `readlink -f "$0"`)
PHOME=$(dirname $PHOME)

#JMX_PORT=`expr 8500`
JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xmn384m -XX:MaxPermSize=128m \
-Xss256k -XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC -XX:CMSFullGCsBeforeCompaction=5 \
-XX:+UseCMSCompactAtFullCollection \
-XX:+PrintGC -Xloggc:/data/logs/${APP}/gc_$1.log \
-Dlogs.dir=/home/work/wuhao/spring.batch.test-1.0-SNAPSHOT-dev/log \
-Duser.timezone=GMT+08"

pid=`ps -eo pid,args | grep ${APP} | grep java | grep -v grep | awk '{print $1}'`
echo "-----------------${APP} pid is ${pid}-----------------"

if [ -n "$pid" ]
then
    kill -3 ${pid}
    kill ${pid} && sleep 3
    if [  -n "`ps -eo pid | grep $pid`" ]
    then
        kill -9 ${pid}
    fi
    echo "-----------------kill pid: ${pid}-----------------"
fi

if [  -n "`ps -eo pid | grep $pid`" ]
then
    echo "-----------------kill failure!-----------------"
fi

echo "start ...."
java $JAVA_OPTS -cp ${PHOME}/conf:${PHOME}/lib/* org.springframework.batch.core.launch.support.CommandLineJobRunner classpath:/jobs/base_data.xml ${APP} > /dev/null 2>&1 &
