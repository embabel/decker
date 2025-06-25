@echo off
setlocal

call .\scripts\support\check_env.bat

if errorlevel 1 (
    echo Environment check failed. Exiting...
    exit /b 1
)



cmd /c mvn -Dmaven.test.skip=true spring-boot:run

endlocal