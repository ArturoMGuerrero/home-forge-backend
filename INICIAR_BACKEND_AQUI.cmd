@echo off
echo ================================================
echo Iniciando HomeForge Backend con SQL Server
echo ================================================
echo.
echo Base de datos: EGDATACAP01\SQLEXPRESS - test
echo Puerto: 8080
echo.
echo Presiona Ctrl+C para detener
echo ================================================
echo.

call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver

pause
