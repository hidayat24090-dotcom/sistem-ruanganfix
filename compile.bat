@echo off
echo ========================================
echo   COMPILE - Sistem Ruangan
echo ========================================
echo.

REM Set paths
SET JAVAFX_PATH=C:\javafx-sdk-17\lib
SET PROJECT_PATH=C:\Users\Axioo Pongo\Desktop\sistem-ruangan

cd "%PROJECT_PATH%"

REM Create bin folder
if not exist "bin" mkdir bin

echo [INFO] Compiling Java files...
echo.

REM Compile with resources in classpath
javac --module-path "%JAVAFX_PATH%" ^
      --add-modules javafx.controls,javafx.fxml ^
      -cp "lib\*;src;resources" ^
      -d bin ^
      src\com\sistemruangan\util\DatabaseConnection.java ^
      src\com\sistemruangan\model\Ruangan.java ^
      src\com\sistemruangan\model\Peminjaman.java ^
      src\com\sistemruangan\controller\RuanganController.java ^
      src\com\sistemruangan\controller\PeminjamanController.java ^
      src\com\sistemruangan\view\LoginController.java ^
      src\com\sistemruangan\view\DashboardController.java ^
      src\com\sistemruangan\view\DaftarRuanganController.java ^
      src\com\sistemruangan\view\DataPeminjamanController.java ^
      src\com\sistemruangan\MainApp.java

if errorlevel 1 (
    echo.
    echo [ERROR] Compilation FAILED!
    pause
    exit /b 1
)

echo.
echo [INFO] Copying resources to bin...

REM Copy FXML files
if not exist "bin\fxml" mkdir bin\fxml
xcopy /y /q resources\fxml\*.fxml bin\fxml\ >nul 2>&1

REM Copy CSS files
if not exist "bin\css" mkdir bin\css
xcopy /y /q resources\css\*.css bin\css\ >nul 2>&1

echo [SUCCESS] Compilation completed!
echo.
pause