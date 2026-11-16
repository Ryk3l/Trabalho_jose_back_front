@echo off
REM ============================================================
REM  POPULAR BANCO — cadastra 10 marcas de exemplo com CNPJ
REM  Rode DEPOIS de subir o backend (iniciar-back.bat).
REM  Se rodar de novo, os CNPJs repetidos aparecem como
REM  "Ja existe uma marca cadastrada" — nao e erro.
REM ============================================================

title Popular banco - marcas de exemplo
setlocal

echo Verificando se o backend esta no ar...
curl -s -o nul http://localhost:8080/api/marcas
if errorlevel 1 (
    echo [ERRO] Backend nao respondeu em http://localhost:8080
    echo Suba o backend primeiro com o iniciar-back.bat e rode este script depois.
    pause
    exit /b 1
)

echo Backend no ar. Cadastrando marcas de exemplo:
echo.

call :cadastrar "Distribuidora Central Ltda" "98.765.432/0001-98"
call :cadastrar "Comercial Aurora Sul Ltda" "11.222.333/0001-81"
call :cadastrar "Transporte Rapido Nordeste" "45.723.174/0001-10"
call :cadastrar "Supermercados Boa Compra" "19.131.184/0001-97"
call :cadastrar "Construtora Pilar Forte" "27.865.757/0001-02"
call :cadastrar "Farmacia Vida Plena" "05.315.167/0001-65"
call :cadastrar "Auto Pecas Torno Feliz" "33.000.167/0001-01"
call :cadastrar "Papelaria Estrela Doce" "60.746.948/0001-12"
call :cadastrar "Tech Solutions Prime" "10.000.000/0001-45"
call :cadastrar "Grafica Impressa Jauru" "34.028.316/0001-03"
call :cadastrar "Graaafica Impressa Jauru" "33.028.316/0001-03"

echo.
echo Listagem atual (GET /api/marcas):
curl -s http://localhost:8080/api/marcas
echo.
echo.
echo Pronto. Registros com "Ja existe" ja tinham sido cadastrados antes.
pause
exit /b 0

:cadastrar
echo Cadastrando: %~1 (%~2) ...
> "%TEMP%\marca_popular.json" echo {"nome": "%~1", "cnpj": "%~2"}
curl -s -X POST -H "Content-Type: application/json" --data "@%TEMP%\marca_popular.json" http://localhost:8080/api/marcas
echo.
echo -------------------------------------------------
exit /b 0
