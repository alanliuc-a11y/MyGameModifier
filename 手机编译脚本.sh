#!/system/bin/sh
# 手机端编译脚本
# 需要安装Termux

echo "===================================="
echo "  我的游戏修改器 - 手机编译脚本"
echo "===================================="
echo ""

# 检查Termux
if [ ! -d "/data/data/com.termux" ]; then
    echo "❌ 请先安装Termux!"
    echo "下载: https://f-droid.org/packages/com.termux/"
    exit 1
fi

echo "✓ Termux已安装"
echo ""

# 安装必要包
echo "[1/5] 安装编译环境..."
pkg update -y
pkg install -y openjdk-17 gradle git

echo ""
echo "[2/5] 配置环境..."
export JAVA_HOME=/data/data/com.termux/files/usr
export PATH=$PATH:$JAVA_HOME/bin

echo ""
echo "[3/5] 进入项目..."
cd /sdcard/Projects/MyGameModifier || exit 1

echo ""
echo "[4/5] 编译APK..."
gradle assembleDebug

echo ""
echo "[5/5] 复制APK到Download..."
mkdir -p /sdcard/Download/MyModifier
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/MyModifier/
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/MyModifier-我的修改器.apk

echo ""
echo "===================================="
echo "✓ 编译完成!"
echo "APK位置: /sdcard/Download/MyModifier-我的修改器.apk"
echo "===================================="
echo ""
echo "使用方法:"
echo "1. 在文件管理器找到APK"
echo "2. 点击安装"
echo "3. 打开使用"
echo ""
