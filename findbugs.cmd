rem .\findbugs-3.0.0\bin\findbugs.bat -jvm %*
.\java.cmd -cp ".\stdlib" -jar .\findbugs-3.0.0\lib\findbugs.jar -textui -include findbugs.xml %*
