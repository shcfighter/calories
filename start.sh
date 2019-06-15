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

mvn clean install

#启动服务
nohup java -Xms64m -Xmx64m -jar /data/calories/calories/target/calories-1.0-SNAPSHOT-fat.jar -conf /data/calories_config.json -cluster >> /data/calories/logs/calories.log &
echo 'calories Micro Services start ...'
