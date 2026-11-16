@echo off
REM ============================================================
REM  BACKEND — Spring Boot (porta 8080)
REM  Roda nesta propria janela. Feche a janela para parar.
REM ============================================================

title BACKEND - Spring Boot (8080)

if not exist "%~dp0back\mvnw.cmd" (
    echo [ERRO] Nao encontrei "%~dp0back\mvnw.cmd" — rode este .bat na raiz do projeto.
    pause
    exit /b 1
)

echo Iniciando o backend em http://localhost:8080 ...
echo Lembrete: o banco PostgreSQL "cadastro" precisa existir
echo (usuario postgres, senha 1234) antes de o backend subir.
echo.

cd /d "%~dp0back"
call mvnw.cmd spring-boot:run

echo.
echo [O backend foi encerrado ou falhou ao subir.]
pause
