@rem
@rem Copyright 2007 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem %DEBUG% provides debugging for this script.
@rem %TRACE% provides tracing for this script.

setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_BASE_NAME=%~n0
set HOME=%HOMEDRIVE%%HOMEPATH%

@rem Add default JVM options here. You can also use JAVA_OPTS and MAVEN_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%"==0 goto init

echo.
echo ERROR: JAVA_HOME is not defined and no 'java' command could be found in your PATH.
echo.
echo Please install Java 17 or newer and try again.
echo.
goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%"=="Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1"=="x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:execute
@rem Setup the command line

set WRAPPER_JAR="%DIRNAME%.mvn\wrapper\maven-wrapper.jar"

@rem Execute Maven
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %CMD_LINE_ARGS%
endlocal
:fail
rem Set variable %ERRORLEVEL% to indicate failure
exit /b 1