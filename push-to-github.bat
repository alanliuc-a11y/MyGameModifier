@echo off
chcp 65001
echo ========================================
echo    推送到GitHub
chcp 65001 >nul
echo ========================================
echo.

echo [提示] 你需要先在GitHub创建仓库:
echo 1. 访问: https://github.com/new
echo 2. 仓库名: MyGameModifier
echo 3. 点击 Create repository
echo.
pause

cd /d "%~dp0"

echo.
echo [1/3] 添加远程仓库...
git remote add origin https://github.com/alanliuc-a11y/MyGameModifier.git 2>nul
git remote set-url origin https://github.com/alanliuc-a11y/MyGameModifier.git

echo.
echo [2/3] 切换到main分支...
git branch -M main

echo.
echo [3/3] 推送到GitHub...
echo 将弹出窗口要求登录GitHub...
git push -u origin main

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo    推送成功!
    echo ========================================
    echo.
    echo 仓库地址: https://github.com/alanliuc-a11y/MyGameModifier
    echo.
    echo 下一步: 访问Actions查看编译进度
    echo https://github.com/alanliuc-a11y/MyGameModifier/actions
    echo.
) else (
    echo.
    echo ========================================
    echo    推送失败
    echo ========================================
    echo.
    echo 请检查:
    echo 1. 是否在GitHub创建了仓库
    echo 2. GitHub用户名是否正确
    echo 3. 网络连接是否正常
    echo.
)

pause
