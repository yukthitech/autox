@echo off

cd %~dp0
echo "Current directory: " %cd%

md "../logs"

echo "Checking for updates..."
java -classpath "../lib/*;../config/*;" com.yukthitech.autox.ide.IdeUpgradeChecker

IF NOT EXIST "../lib-new" GOTO STARTIDE

echo "Deleting current lib folder..."
RMDIR /S /Q "../lib"

echo "Renaming new lib dir..."
REN "../lib-new" "lib"

:STARTIDE
echo "Starting ide..."
java -classpath "../lib/*;../config/*;" com.yukthitech.autox.ide.AutoxIDE > ../logs/console.log 2>&1
