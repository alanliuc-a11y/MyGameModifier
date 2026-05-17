@echo off
cd /d "%~dp0"
echo ========================================
echo    GitHub 创建并推送
echo ========================================
echo.
echo 正在使用用户名密码创建仓库...
echo.

REM 使用curl和基本认证创建仓库
curl -s -X POST "https://api.github.com/user/repos" ^
  -u "alanliuc@gmail.com:Ckgroove1" ^
  -H "Accept: application/vnd.github.v3+json" ^
  -d "{\"name\":\"MyGameModifier\",\"description\":\"自开发安卓游戏修改器\",\"private\":false}" > response.json

echo 创建结果：
type response.json
echo.

REM 检查是否成功
findstr "html_url" response.json >nul
if %errorlevel% == 0 (
    echo.
    echo 仓库创建成功！
    del response.json
    
    echo.
    echo 正在推送代码...
    git push -u origin main
    
    if %errorlevel% == 0 (
        echo.
        echo ========================================
        echo    完成！
        echo ========================================
        echo 仓库地址: https://github.com/alanliuc-a11y/MyGameModifier
    ) else (
        echo.
        echo 推送失败，尝试重新配置远程仓库...
        git remote remove origin
        git remote add origin https://github.com/alanliuc-a11y/MyGameModifier.git
        git push -u origin main
    )
) else (
    echo.
    echo 创建失败，请检查：
    echo 1. 用户名密码是否正确
    echo 2. 网络连接是否正常
    echo 3. 是否启用了两步验证（需要Token）
    type response.json
)

del response.json 2>nul
pause
