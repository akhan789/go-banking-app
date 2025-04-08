@echo off
echo Starting all services...
REM Starting Audit Logging Service...
REM call audit-logging-service/start-audit-logging-service.bat
start cmd /c "cd audit-logging-service && call start-audit-logging-service.bat"

REM Starting Account Management Service...
start cmd /c "cd account-management-service && call start-account-management-service.bat"

REM Starting Balance Operations Service...
start cmd /c "cd balance-operations-service && call start-balance-operations-service.bat"

REM Starting Interest Management Service...
start cmd /c "cd interest-management-service && call start-interest-management-service.bat"

REM Starting Service Gateway...
start cmd /c "cd service-gateway && call start-service-gateway.bat"

echo All services have been started.
echo Launching GUI.
start cmd /c "cd swing-ui && call start-swing-ui.bat"
pause