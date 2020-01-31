@echo off
set time_start_launch_elastic_search=%TIME%

rem win10: shell:startup

d:
cd D:\software\elasticsearch-7.5.1\bin
elasticsearch.bat

@echo Finished, start: %time_start_launch_elastic_search%, end: %TIME%
