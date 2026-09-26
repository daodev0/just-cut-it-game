@echo off
title Cargando juego...
echo Iniciando el juego, por favor espera...
call gradlew.bat desktop:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Ocurrio un error al iniciar. Asegurate de tener Java instalado.
    pause
)
