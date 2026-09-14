@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@if "%DEBUG%" == "" @echo off
@setlocal

set ERROR_CODE=0

@REM set HOME directory of the wrapper
set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR%" == "" set MAVEN_PROJECTBASEDIR=%CD%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.zip"

set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.8.8-bin\apache-maven-3.8.8

if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
    goto end
)

if exist "%MAVEN_HOME%\bin\mvn" (
    "%MAVEN_HOME%\bin\mvn" %*
    goto end
)

echo Downloading Apache Maven 3.8.8 wrapper...
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $parent = Split-Path -Path '%MAVEN_HOME%' -Parent; New-Item -ItemType Directory -Force -Path $parent | Out-Null; $zip = Join-Path $parent 'maven.zip'; Invoke-WebRequest -Uri %DOWNLOAD_URL% -OutFile $zip; Expand-Archive -Path $zip -DestinationPath $parent -Force; Remove-Item $zip -Force }"

if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
    goto end
)

if exist "%WRAPPER_JAR%" (
    java -cp "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*
) else (
    echo Error: Could not find maven-wrapper.jar
    exit /b 1
)

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
