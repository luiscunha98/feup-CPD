Rem ------ Run Server ------

START java Server 8000

timeout /t 2 /nobreak

Rem ------ Run Clients -----

START java Client localhost 8000
START java Client localhost 8000
START java Client localhost 8000
START java Client localhost 8000

EXIT