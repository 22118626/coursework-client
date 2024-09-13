cd /d %~dp0

REM rmdir .\build /s /q

.\gradlew.bat jar --no-daemon

cd /d %~dp0
echo "penis mouth"
echo "Checking if JAR file exists..."
REM Check if the JAR file exists before running it
IF EXIST "build\libs\coursework-client-1.0-SNAPSHOT.jar" (
    echo "JAR file exists. Running the JAR..."
    ".\bin\jdk-17.0.4.101-hotspot\bin\java.exe" -jar "build\libs\coursework-client-1.0-SNAPSHOT.jar"
) ELSE (
    echo "JAR file not found! Check if the build was successful."
)