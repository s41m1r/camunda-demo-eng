#!/usr/bin/env bash

sh /home/saimir/camunda-bpm-ee-tomcat-7.6.4-ee/server/apache-tomcat-8.0.24/bin/shutdown.sh
sleep 3
rm -rf /home/saimir/camunda-bpm-ee-tomcat-7.6.4-ee/server/apache-tomcat-8.0.24/webapps/camunda-demo*
#rm -rf /home/saimir/camunda-bpm-ee-tomcat-7.6.4-ee/server/apache-tomcat-8.0.24/webapps/camunda-demo.war
rm -rf /home/saimir/camunda-bpm-ee-tomcat-7.6.4-ee/server/apache-tomcat-8.0.24/work/Catalina/localhost

