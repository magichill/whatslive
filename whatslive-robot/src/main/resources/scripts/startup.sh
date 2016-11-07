#!/bin/bash

JAVA_OPTS="-server -Xms1g -Xmx1g -Xmn384m -XX:MaxPermSize=128m \
-Xss512k -XX:+UseConcMarkSweepGC \
-XX:+UseParNewGC -XX:CMSFullGCsBeforeCompaction=5 \
-XX:+UseCMSCompactAtFullCollection \
-XX:+PrintGC -Xloggc:/letv/logs/apps/whatslive/whatslive-robot/std.log"

PHOME=$(dirname `readlink -f "$0"`)
PHOME=$(dirname $PHOME)
PNAME=whatsliveRobot

pid=`ps -eo pid,args | grep $PNAME | grep java | grep -v grep | awk '{print $1}'`
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

java $JAVA_OPTS -cp $PHOME/conf:$PHOME/lib/* com.letv.whatslive.robot.boot.RobotBootstrap $PNAME $1 > /dev/null 2>&1 &
