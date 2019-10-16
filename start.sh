#!/bin/sh

# 杀calories微服务进程
ps -aux | grep calories- | grep -v grep | awk '{print $2}' | while read pid
do
	echo "calories- is running, to kill bootstrap pid=$pid"
	kill -9 $pid
	echo "kill result: $?"
done

# 删除log
echo "remove logs"
rm -rf /data/calories/logs/calories*.log
rm -rf /data/calories/logs/calories_dump.hprof*

mvn clean install -Dmaven.test.skip=true

#启动服务
nohup java -Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=/data/calories/logs/calories_dump.hprof -XX:+PrintGC -XX:+PrintGCDetails -Xloggc:/data/calories/logs/calories_gc.log -jar /data/calories/calories/target/calories-1.0-SNAPSHOT-fat.jar -conf /data/calories_config.json -cluster >> /data/calories/logs/calories.log &
echo 'calories Micro Services start ...'
