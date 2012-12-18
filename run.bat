@echo off

if exist %~dp0\httpc.jar (
    java -jar "%~dp0\httpc.jar"
) else (
    java -jar "%~dp0\out\httpc.jar"
)
