Rem Windows project starter

@echo off
chcp 65001 >nul
CD src
START 0_cleanproject.bat
timeout /t 2 /nobreak
START 1_buildproject.bat
timeout /t 2 /nobreak
START 2_startproject.bat
EXIT
