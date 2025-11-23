@echo off
echo ================================================
echo   Iniciando Backend - Gestor de Compras
echo ================================================
echo.
echo Backend sera iniciado em: http://localhost:8081
echo Console H2: http://localhost:8081/h2-console
echo Swagger: http://localhost:8081/swagger-ui.html
echo.
echo Aguarde...
echo.

cd gestor-compras-backend
call mvnw.cmd spring-boot:run
