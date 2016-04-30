@echo off

:: ***************************************************
:: java-algs4
:: ------------------
:: Wrapper for java that includes algs4.jar
:: ***************************************************

set install=%USERPROFILE%\algs4

java -cp ".;%install%\algs4.jar" %*
