#!/bin/bash

export JAVA_HOME=/usr/java/jdk1.6.0_32
cd /data/web/task/groovy/sitemap/wap/tt
/usr/local/groovy-2.1.0/bin/groovy sitemap.groovy >> sitemap.log 2>&1
