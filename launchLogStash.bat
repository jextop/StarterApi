@echo off
set time_start_launch_log_stash=%TIME%

rem win10: shell:startup

d:
cd D:\software\logstash-5.5.2\bin
logstash -f logstash.conf

@echo Finished, start: %time_start_launch_log_stash%, end: %TIME%
