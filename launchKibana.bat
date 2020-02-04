@echo off
set time_start_launch_kibana=%TIME%

rem win10: shell:startup

d:
cd D:\software\kibana-7.5.2-windows-x86_64
bin\kibana

@echo Finished, start: %time_start_launch_kibana%, end: %TIME%
