@echo off
echo Running Gradle build...
gradlew.bat build

echo Build completed successfully.

echo Running the JAR file...
java -jar .\build\libs\coursework-client-1.0-SNAPSHOT.jar

pause