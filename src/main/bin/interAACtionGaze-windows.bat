@echo on

SET SCRIPT_PATH=%~dp0
SET JAR_PATH="%SCRIPT_PATH%..\lib\interAACtionGaze.jar"
SET JAVA_HOME=%SCRIPT_PATH%..\lib\jre
SET PATH=%JAVA_HOME%\bin;%PATH%;%LocalAppData%\TobiiStreamEngineForJava\lib\tobii\x64

start /min java -cp "..\lib\*" -Xms256m -Xmx1g -Dlogging.appender.console.level=OFF -jar %JAR_PATH%
