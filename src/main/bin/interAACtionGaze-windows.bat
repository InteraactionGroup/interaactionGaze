@echo on

SET PATH=%~dp0..\lib\jre\bin;%PATH%;%LocalAppData%\TobiiStreamEngineForJava\lib\tobii\x64
SET JAVAFX_LIB=%~dp0..\lib\*
SET JAVAFX_MODULES=javafx.base,javafx.controls,javafx.graphics,javafx.media,javafx.swing,javafx.web

start /min java -cp "%JAVAFX_LIB%" --module-path ..\lib\ --add-modules %JAVAFX_MODULES% application.Main
