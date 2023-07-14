Rem Windows project starter

@ECHO OFF
CD src
START 0_cleanproject.bat
timeout /t 2 /nobreak
START 1_buildproject.bat
timeout /t 2 /nobreak
START 2_startproject.bat
timeout /t 2 /nobreak
START 3_add4players.bat
EXIT