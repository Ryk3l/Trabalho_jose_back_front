@echo off
REM ============================================================
REM  FRONTEND — Angular (porta 4200)
REM  Roda nesta propria janela. Feche a janela para parar.
REM ============================================================

title FRONTEND - Angular (4200)

if not exist "%~dp0front\package.json" (
    echo [ERRO] Nao encontrei "%~dp0front\package.json" — rode este .bat na raiz do projeto.
    pause
    exit /b 1
)

REM Primeira vez nesta maquina: instala as dependencias antes de subir.
if not exist "%~dp0front\node_modules" (
    echo Primeira vez nesta maquina: instalando dependencias do frontend...
    call npm install --prefix "%~dp0front"
    if errorlevel 1 (
        echo [ERRO] Falha no npm install — verifique se o Node.js esta instalado.
        pause
        exit /b 1
    )
)

echo Iniciando o frontend em http://localhost:4200 ...
echo Aguarde aparecer "Watch mode enabled" antes de abrir o navegador.
echo.

cd /d "%~dp0front"
REM --host 0.0.0.0: sem isso o Vite pode subir so no IPv6 (::1) e o navegador nao acessa.
call npm start -- --host 0.0.0.0

echo.
echo [O frontend foi encerrado ou falhou ao subir.]
pause
