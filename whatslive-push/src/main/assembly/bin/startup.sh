#!/bin/bash

JMX_PORT=`expr $1 + 500`

JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xmn384m -XX:MaxPermSize=256m \
-Xss256k -XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC -XX:CMSFullGCsBeforeCompaction=5 \
-XX:+UseCMSCompactAtFullCollection \
"

#-Djava.rmi.server.hostname= \
#-Dcom.sun.management.jmxremote \
#-Dcom.sun.management.jmxremote.port=$JMX_PORT \
#-Dcom.sun.management.jmxremote.authenticate=false \
#-Dcom.sun.management.jmxremote.ssl=false"

PHOME=$(dirname `readlink -f "$0"`)
PHOME=$(dirname $PHOME)

pid=`ps -eo pid,args | grep whatslive-push | grep $1 | grep java | grep -v grep | awk '{print $1}'`

if [ -n "$pid" ]
then
    kill -3 ${pid}
    kill ${pid} && sleep 3
    if [  -n "`ps -eo pid | grep $pid`" ]
    then
        kill -9 ${pid}
    fi
    echo "kill pid: ${pid}"
fi

java -Djava.net.preferIPv4Stack=true -Dio.netty.leakDetectionLevel=advanced  $JAVA_OPTS -cp $PHOME/conf:$PHOME/lib/* com.letv.whatslive.push.Main $1 > /dev/null 2>&1 &
