@echo off
chcp 65001
echo ========================================
echo    GitHub项目初始化工具
echo ========================================
echo.

REM 检查Git是否安装
git --version >nul 2>&1
if errorlevel 1 (
    echo 请先安装Git: https://git-scm.com/
    pause
    exit
)

echo Git已安装，继续初始化...
echo.

REM 进入项目目录
cd /d "%~dp0"

echo [1/5] 初始化Git仓库...
git init

echo.
echo [2/5] 配置Git用户...
git config user.email "android@modifier.app"
git config user.name "My Game Modifier"

echo.
echo [3/5] 添加所有文件...
git add .

echo.
echo [4/5] 提交代码...
git commit -m "Initial commit: My Game Modifier v1.0"

echo.
echo [5/5] Git初始化完成！
echo.
echo ========================================
echo 下一步操作：
echo ========================================
echo.
echo 1. 在GitHub创建仓库: https://github.com/new
echo    仓库名: MyGameModifier
echo.
echo 2. 创建后运行以下命令：
echo    git remote add origin https://github.com/alanliuc-a11y/MyGameModifier.git
echo    git branch -M main
echo    git push -u origin main
echo.
echo 或查看 GitHub上传指南.md 获取详细步骤
echo.
pause
