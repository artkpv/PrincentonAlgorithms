@echo off

:: ***************************************************
:: javac-algs4
:: ------------------
:: Wrapper for javac that includes algs4.jar
:: ***************************************************

set install=%USERPROFILE%\algs4

javac -cp ".;%install%\algs4.jar" -g -encoding UTF-8 %*

