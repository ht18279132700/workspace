#!/bin/bash

export JAVA_HOME=/usr/java/jdk1.6.0_32
cd /data/web/task/groovy
/usr/local/groovy-2.1.0/bin/groovy count_wd_daily.groovy >> count_wd_daily.log 2>&1
