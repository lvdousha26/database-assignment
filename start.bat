@echo off
chcp 65001 >nul
title 油水井成本管理系统 - Docker 一键启动

echo ==========================================
echo   油水井成本管理系统 - 一键启动
echo ==========================================
echo.

:: ==========================================
:: 1. 检查 Docker
:: ==========================================
echo [1/3] 检查 Docker 环境...

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo   [X] 未找到 Docker，请先安装 Docker Desktop
    echo   下载地址: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

docker info >nul 2>nul
if %errorlevel% neq 0 (
    echo   [X] Docker 未运行，请先启动 Docker Desktop
    pause
    exit /b 1
)
echo   [√] Docker 已就绪

:: ==========================================
:: 2. 拉取并启动容器
:: ==========================================
echo.
echo [2/3] 启动系统容器...

:: 检查是否已有同名容器在运行
docker inspect oil-well >nul 2>nul
if %errorlevel% equ 0 (
    echo   容器已存在，正在启动...
    docker start oil-well >nul 2>&1
) else (
    echo   正在拉取镜像并创建容器（首次启动约1-2分钟）...
    docker run -d --name oil-well -p 82:80 lvdousha26/database-lvdousha:latest >nul 2>&1
)

if %errorlevel% neq 0 (
    echo   [X] 容器启动失败
    pause
    exit /b 1
)
echo   [√] 容器已启动

:: ==========================================
:: 3. 等待就绪并打开浏览器
:: ==========================================
echo.
echo [3/3] 等待系统就绪...

echo   等待 MySQL 初始化完成（约15秒）...
timeout /t 15 /nobreak >nul

echo.
echo ==========================================
echo   启动完成！
echo.
echo   访问地址 : http://localhost:82
echo   管理员   : admin / admin123
echo   普通用户 : user1 / user123
echo.
echo   关闭命令 : docker stop oil-well
echo   重新启动 : docker start oil-well
echo ==========================================
echo.

start http://localhost:82

echo   浏览器已打开，按任意键关闭此窗口...
pause >nul
