@echo off
setlocal

echo Closing Gradle daemons...
cd /d "%~dp0"
if exist gradlew.bat (
    call gradlew.bat --stop
)

set "KOTLIN_DSL_CACHE=%USERPROFILE%\.gradle\caches\8.13\kotlin-dsl"
set "PROJECT_GRADLE_CACHE=%~dp0.gradle"

echo.
echo Clearing corrupted Gradle Kotlin DSL cache...
if exist "%KOTLIN_DSL_CACHE%" (
    rmdir /s /q "%KOTLIN_DSL_CACHE%"
    echo Removed: %KOTLIN_DSL_CACHE%
) else (
    echo Not found: %KOTLIN_DSL_CACHE%
)

echo.
echo Clearing this project's local Gradle cache...
if exist "%PROJECT_GRADLE_CACHE%" (
    rmdir /s /q "%PROJECT_GRADLE_CACHE%"
    echo Removed: %PROJECT_GRADLE_CACHE%
) else (
    echo Not found: %PROJECT_GRADLE_CACHE%
)

echo.
echo Done. Open Android Studio again and click Sync Project with Gradle Files.
pause
