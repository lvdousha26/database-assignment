@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

title 油水井成本管理系统 - 一键启动
echo ==========================================
echo      一键启动
echo ==========================================
echo.

:: ==========================================
:: 0. 项目路径
:: ==========================================
set "PROJECT_DIR=%~dp0"
set "BACKEND_DIR=%PROJECT_DIR%oil-well-system"
set "FRONTEND_DIR=%PROJECT_DIR%oil-well-vue"
set "SQL_FILE=%PROJECT_DIR%tb_school_establishment.sql"

:: MySQL 配置（与 application.yml 默认值一致）
set "MYSQL_HOST=localhost"
set "MYSQL_PORT=3306"
set "MYSQL_USER=root"
set "MYSQL_PASS=123456"
set "MYSQL_DB=tb_school"

:: ==========================================
:: 1. 检查 Java 版本
:: ==========================================
echo [1/7] 检查环境依赖...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo   [X] 未找到 Java，请安装 JDK 17+
    pause
    exit /b 1
)

:: 提取 Java 版本号并获取主版本
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JVER=%%i
set JVER=%JVER:"=%
for /f "tokens=1 delims=." %%a in ("%JVER%") do set JMAJ=%%a
:: JDK 8 版本号格式为 "1.8.0"，取第二个数字
if "%JMAJ%"=="1" for /f "tokens=2 delims=." %%b in ("%JVER%") do set JMAJ=%%b

if %JMAJ% lss 17 (
    echo   当前 Java 版本: %JVER% ^(需要 JDK 17+^)
    echo   正在搜索 JDK 17+...

    :: 尝试 JAVA_HOME
    if defined JAVA_HOME (
        for /f "tokens=3" %%v in ('"%JAVA_HOME%\bin\java" -version 2^>^&1 ^| findstr /i "version"') do set JHVER=%%v
        set JHVER=!JHVER:"=!
        for /f "tokens=1 delims=." %%v in ("!JHVER!") do set JHMAJ=%%v
        if "!JHMAJ!"=="1" for /f "tokens=2 delims=." %%v in ("!JHVER!") do set JHMAJ=%%v
        if !JHMAJ! geq 17 (
            set "PATH=%JAVA_HOME%\bin;%PATH%"
            echo   [√] 已切换到 JAVA_HOME: %JAVA_HOME%
            goto :java_ok
        )
    )

    :: 搜索常见安装路径
    for %%d in (
        "D:\coding\IDE\java\jdk-17.0.11.9-hotspot"
        "C:\Program Files\Java\jdk-17"
        "C:\Program Files\Eclipse Adoptium\jdk-17-hotspot"
        "C:\Program Files\Microsoft\jdk-17"
    ) do (
        if exist "%%~d\bin\java.exe" (
            for /f "tokens=3" %%v in ('"%%~d\bin\java" -version 2^>^&1 ^| findstr /i "version"') do set FVER=%%v
            set FVER=!FVER:"=!
            for /f "tokens=1 delims=." %%v in ("!FVER!") do set FMAJ=%%v
            if "!FMAJ!"=="1" for /f "tokens=2 delims=." %%v in ("!FVER!") do set FMAJ=%%v
            if !FMAJ! geq 17 (
                set "PATH=%%~d\bin;%PATH%"
                echo   [√] 已切换到: %%~d
                goto :java_ok
            )
        )
    )

    echo   [X] 未找到 JDK 17+，请安装或设置 JAVA_HOME 环境变量
    pause
    exit /b 1
) else (
    echo   [√] Java %JMAJ% 已就绪
)

:java_ok

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo   [X] 未找到 Maven，请安装 Maven 3.8+
    pause
    exit /b 1
)
echo   [√] Maven 已就绪

where node >nul 2>nul
if %errorlevel% neq 0 (
    echo   [X] 未找到 Node.js，请安装 Node.js 18+
    pause
    exit /b 1
)
echo   [√] Node.js 已就绪

where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo   [X] 未找到 npm
    pause
    exit /b 1
)
echo   [√] npm 已就绪

:: ==========================================
:: 2. 启动 MySQL 服务
:: ==========================================
echo.
echo [2/7] MySQL 数据库服务...

:: 先检查端口是否已监听
powershell -Command "exit (Test-NetConnection -ComputerName %MYSQL_HOST% -Port %MYSQL_PORT% -WarningAction SilentlyContinue).TcpTestSucceeded" 2>nul
if !errorlevel! equ 1 (
    echo   [√] MySQL %MYSQL_PORT% 端口已监听
    set "MYSQL_WAS_RUNNING=1"
    goto :mysql_ready
)

:: 端口未监听，尝试启动 Windows 服务
echo   端口未监听，尝试启动 MySQL 服务...
set "MYSQL_WAS_RUNNING=0"
set "SVC_STARTED=0"

:: 动态查找 MySQL 服务名
for /f "tokens=1" %%a in ('sc query state^= inactive ^| findstr /i "SERVICE_NAME:" ^| findstr /i "mysql"') do (
    set "MYSQL_SVC=%%a"
)
for /f "tokens=1" %%a in ('sc query state^= all ^| findstr /i "SERVICE_NAME:" ^| findstr /i "mysql"') do (
    if not defined MYSQL_SVC set "MYSQL_SVC=%%a"
)

if defined MYSQL_SVC (
    echo   发现服务: !MYSQL_SVC!，正在启动...
    net start "!MYSQL_SVC!" >nul 2>&1
    if !errorlevel! equ 0 (
        set "SVC_STARTED=1"
        echo   [√] 服务 !MYSQL_SVC! 已启动
        timeout /t 3 /nobreak >nul
        goto :mysql_ready
    ) else (
        echo   [!] 服务 !MYSQL_SVC! 启动失败
    )
)

:: 兜底：尝试常见服务名
for %%s in (MySQL91 MySQL80 MySQL mysql MariaDB) do (
    if !SVC_STARTED! equ 0 (
        sc query "%%s" >nul 2>&1
        if !errorlevel! equ 0 (
            echo   发现服务: %%s，正在启动...
            net start "%%s" >nul 2>&1
            if !errorlevel! equ 0 (
                set "SVC_STARTED=1"
                set "MYSQL_SVC=%%s"
                echo   [√] 服务 %%s 已启动
                :: 等待 MySQL 就绪
                echo   等待 MySQL 就绪...
                timeout /t 3 /nobreak >nul
                goto :mysql_ready
            ) else (
                echo   [!] 服务 %%s 启动失败，尝试下一个...
            )
        )
    )
)

:: 如果所有服务启动都失败
if !SVC_STARTED! equ 0 (
    echo   [!] 未能启动 MySQL 服务
    echo   提示: 如果使用 Docker/XAMPP 请手动启动 MySQL
    echo   默认连接: %MYSQL_HOST%:%MYSQL_PORT% 用户: %MYSQL_USER%
    echo.
    echo   按任意键继续 (跳过数据库检查)...
    pause >nul
    goto :mysql_skip
)

:: ==========================================
:: 3. 检查并初始化数据库
:: ==========================================
:mysql_ready
echo.
echo [3/7] 检查数据库...

:: 查找 mysql 命令行客户端
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    :: 尝试从 MySQL 安装目录找 bin\mysql.exe
    for /d %%d in ("C:\Program Files\MySQL\MySQL Server *") do (
        if exist "%%d\bin\mysql.exe" (
            set "PATH=%%d\bin;%PATH%"
            echo   [√] 找到 mysql CLI: %%d\bin
            goto :mysql_cli_found
        )
    )
    echo   [!] 未找到 mysql 命令行客户端
    echo   请手动执行: mysql -u %MYSQL_USER% -p ^< tb_school_establishment.sql
    goto :mysql_skip
)
:mysql_cli_found

:: 检查数据库是否已存在
mysql -u%MYSQL_USER% -p%MYSQL_PASS% -h%MYSQL_HOST% -P%MYSQL_PORT% -e "SELECT 1 FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='%MYSQL_DB%'" 2>nul | findstr "1" >nul
if !errorlevel! equ 0 (
    echo   [√] 数据库 %MYSQL_DB% 已存在，跳过初始化
) else (
    echo   数据库 %MYSQL_DB% 不存在，正在执行初始化脚本...
    if not exist "%SQL_FILE%" (
        echo   [X] 未找到 SQL 脚本: %SQL_FILE%
        goto :mysql_skip
    )
    mysql -u%MYSQL_USER% -p%MYSQL_PASS% -h%MYSQL_HOST% -P%MYSQL_PORT% < "%SQL_FILE%" 2>nul
    if !errorlevel! equ 0 (
        echo   [√] 数据库初始化完成
    ) else (
        echo   [X] 数据库初始化失败，请检查密码或手动执行
        echo   mysql -u%MYSQL_USER% -p ^< tb_school_establishment.sql
    )
)

:mysql_skip

:: ==========================================
:: 4. Redis 检查
:: ==========================================
echo.
echo [4/7] Redis 缓存服务...
powershell -Command "exit (Test-NetConnection -ComputerName localhost -Port 6379 -WarningAction SilentlyContinue).TcpTestSucceeded" 2>nul
if !errorlevel! equ 1 (
    echo   [√] Redis 6379 端口已监听
) else (
    echo   [-] Redis 未检测到 (可选，不影响运行)
)

:: ==========================================
:: 5. 前端依赖
:: ==========================================
echo.
echo [5/7] 前端依赖...

if not exist "%FRONTEND_DIR%\node_modules" (
    echo   正在安装前端依赖...
    cd /d "%FRONTEND_DIR%"
    call npm install
    if !errorlevel! neq 0 (
        echo   [X] 前端依赖安装失败
        pause
        exit /b 1
    )
    echo   [√] 前端依赖安装完成
) else (
    echo   [√] 前端依赖已就绪
)

:: ==========================================
:: 6. 启动后端
:: ==========================================
echo.
echo [6/7] 启动后端 (端口 8080)...
start "Reid-后端-8080" cmd /k "cd /d "%BACKEND_DIR%" && title Reid后端 :8080 && echo 后端编译启动中... && mvn spring-boot:run"
echo   [√] 后端已在独立窗口启动

:: 等待后端编译
echo   等待后端编译 (首次约30-60秒)...
timeout /t 5 /nobreak >nul

:: 启动前端
echo   启动前端 (端口 8082)...
start "Reid-前端-8082" cmd /k "cd /d "%FRONTEND_DIR%" && title Reid前端 :8082 && npm run dev"
echo   [√] 前端已在独立窗口启动

:: ==========================================
:: 7. 打开浏览器
:: ==========================================
echo.
echo [7/7] 启动完成
echo ==========================================
echo   后端 API : http://localhost:8080
echo   前端页面 : http://localhost:8082
echo.
echo   关闭对应窗口即可停止服务
echo ==========================================
echo.

echo   等待前端就绪 (8秒)...
timeout /t 8 /nobreak >nul
start http://localhost:8082

echo   浏览器已打开，按任意键关闭此窗口...
pause >nul
