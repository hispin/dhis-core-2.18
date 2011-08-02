@ECHO OFF
SET TCLKITSH=runtime\tclkit-cli-86b1.2.exe
SET RUNTIME_WIN=runtime\tclkit-gui.exe

ECHO Preparing vfs
REM Place windoze binary extensions into vfs
rmdir /s /q mydatamart.vfs\libext\sqlite3.7.4
rmdir /s /q mydatamart.vfs\libext\tls1.6
xcopy /s /y /q windoze\libext mydatamart.vfs\libext

ECHO Assembling mydatamart.exe
REM Assemble starkit
%TCLKITSH% runtime\sdx.kit wrap mydatamart.kit
REM Combine with runtime into executable
%TCLKITSH% runtime\sdx.kit wrap mydatamart.exe -runtime %RUNTIME_WIN%
ECHO Done