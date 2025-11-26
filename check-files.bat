@echo off
echo ========================================
echo   CHECK FILES - Sistem Ruangan
echo ========================================
echo.

SET PROJECT_PATH=C:\Users\Axioo Pongo\Desktop\sistem-ruangan

cd "%PROJECT_PATH%"

echo [1] Checking RESOURCES folder...
echo.

if exist "resources\fxml" (
    echo ✅ resources\fxml exists
    dir /b resources\fxml\*.fxml
) else (
    echo ❌ resources\fxml NOT FOUND!
)

echo.

if exist "resources\css" (
    echo ✅ resources\css exists
    dir /b resources\css\*.css
) else (
    echo ❌ resources\css NOT FOUND!
)

echo.
echo ========================================
echo.

echo [2] Checking BIN folder...
echo.

if exist "bin\fxml" (
    echo ✅ bin\fxml exists
    dir /b bin\fxml\*.fxml
) else (
    echo ❌ bin\fxml NOT FOUND!
    echo    You need to run compile-debug.bat
)

echo.

if exist "bin\css" (
    echo ✅ bin\css exists
    dir /b bin\css\*.css
) else (
    echo ❌ bin\css NOT FOUND!
    echo    You need to run compile-debug.bat
)

echo.
echo ========================================
echo.

echo [3] Checking LIB folder...
echo.

if exist "lib\mysql-connector-j-9.1.0.jar" (
    echo ✅ MySQL Connector found
) else (
    echo ❌ MySQL Connector NOT FOUND!
    echo    Download from: https://dev.mysql.com/downloads/connector/j/
)

echo.
echo ========================================
echo.

echo [4] Checking COMPILED classes...
echo.

if exist "bin\com\sistemruangan\MainApp.class" (
    echo ✅ MainApp.class compiled
) else (
    echo ❌ MainApp.class NOT compiled!
    echo    Run compile-debug.bat
)

if exist "bin\com\sistemruangan\util\DatabaseConnection.class" (
    echo ✅ DatabaseConnection.class compiled
) else (
    echo ❌ DatabaseConnection.class NOT compiled!
    echo    Run compile-debug.bat
)

echo.
echo ========================================
echo   CHECK COMPLETE
echo ========================================
echo.
pause