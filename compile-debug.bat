@echo off
echo ========================================
echo   COMPILE DEBUG - Sistem Ruangan
echo ========================================
echo.

REM Set paths
SET JAVAFX_PATH=C:\javafx-sdk-17\lib
SET PROJECT_PATH=C:\Users\Axioo Pongo\Desktop\sistem-ruangan

cd "%PROJECT_PATH%"

REM Create bin folder
if not exist "bin" mkdir bin

echo [1/4] Cleaning old files...
if exist "bin\com" rmdir /s /q bin\com
mkdir bin\com

echo [2/4] Compiling Java files with verbose...
echo.

javac --module-path "%JAVAFX_PATH%" ^
      --add-modules javafx.controls,javafx.fxml ^
      -cp "lib\*;src;resources" ^
      -d bin ^
      -verbose ^
      src\com\sistemruangan\util\DatabaseConnection.java ^
      src\com\sistemruangan\util\SessionManager.java ^
      src\com\sistemruangan\model\Ruangan.java ^
      src\com\sistemruangan\model\Peminjaman.java ^
      src\com\sistemruangan\model\User.java ^
      src\com\sistemruangan\controller\RuanganController.java ^
      src\com\sistemruangan\controller\PeminjamanController.java ^
      src\com\sistemruangan\controller\UserController.java ^
      src\com\sistemruangan\view\LoginController.java ^
      src\com\sistemruangan\view\DashboardController.java ^
      src\com\sistemruangan\view\DaftarRuanganController.java ^
      src\com\sistemruangan\view\DataPeminjamanController.java ^
      src\com\sistemruangan\view\UserLoginController.java ^
      src\com\sistemruangan\view\UserRegisterController.java ^
      src\com\sistemruangan\view\UserDashboardController.java ^
      src\com\sistemruangan\view\UserRuanganListController.java ^
      src\com\sistemruangan\view\UserPeminjamanFormController.java ^
      src\com\sistemruangan\view\UserRiwayatController.java ^
      src\com\sistemruangan\view\UserProfileController.java ^
      src\com\sistemruangan\view\JadwalBulananController.java ^
      src\com\sistemruangan\MainApp.java

if errorlevel 1 (
    echo.
    echo ========================================
    echo [ERROR] Compilation FAILED!
    echo ========================================
    echo.
    echo Lihat error di atas untuk mengetahui masalahnya.
    pause
    exit /b 1
)

echo.
echo [3/4] Copying resources to bin...

REM Copy FXML files
echo    - Copying FXML files...
if not exist "bin\fxml" mkdir bin\fxml
xcopy /y /q resources\fxml\*.fxml bin\fxml\ >nul 2>&1

if exist "bin\fxml\UserLogin.fxml" (
    echo      ✅ FXML files copied
) else (
    echo      ❌ FXML files NOT copied - CHECK resources\fxml\ folder!
)

REM Copy CSS files
echo    - Copying CSS files...
if not exist "bin\css" mkdir bin\css
xcopy /y /q resources\css\*.css bin\css\ >nul 2>&1

if exist "bin\css\style.css" (
    echo      ✅ CSS files copied
) else (
    echo      ❌ CSS files NOT copied - CHECK resources\css\ folder!
)

echo.
echo [4/4] Verifying compiled files...

if exist "bin\com\sistemruangan\MainApp.class" (
    echo    ✅ MainApp.class found
) else (
    echo    ❌ MainApp.class NOT found!
)

if exist "bin\com\sistemruangan\util\DatabaseConnection.class" (
    echo    ✅ DatabaseConnection.class found
) else (
    echo    ❌ DatabaseConnection.class NOT found!
)

if exist "bin\fxml\UserLogin.fxml" (
    echo    ✅ UserLogin.fxml found in bin
) else (
    echo    ❌ UserLogin.fxml NOT found in bin!
)

if exist "bin\css\style.css" (
    echo    ✅ style.css found in bin
) else (
    echo    ❌ style.css NOT found in bin!
)

echo.
echo ========================================
echo [SUCCESS] Compilation completed!
echo ========================================
echo.
echo Next: Run test-db.bat to test database
echo Then: Run run.bat to start application
echo.
pause