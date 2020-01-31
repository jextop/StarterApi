@echo off
set time_start_launch_active_mq=%TIME%

rem win10: shell:startup

d:
cd D:\software\apache-activemq-5.15.11
bin\activemq start

@echo Finished, start: %time_start_launch_active_mq%, end: %TIME%
