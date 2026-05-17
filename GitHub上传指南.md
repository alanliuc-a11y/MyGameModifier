# GitHub自动编译指南

## 🎯 目标
使用GitHub Actions自动编译你的游戏修改器APK

---

## 📦 方案一：直接上传到GitHub（推荐）

### 步骤1: 准备项目文件
确保你电脑上有完整的项目文件夹：
```
MyGameModifier/
├── build.gradle
├── settings.gradle
├── README.md
├── app/
│   ├── build.gradle
│   └── src/
└── .github/workflows/
    └── build.yml          # 已配置好自动编译
```

### 步骤2: 创建GitHub仓库

#### 方法A: 使用网页创建（简单）
1. 打开 https://github.com/new
2. Repository name: `MyGameModifier`
3. Description: `自开发安卓游戏修改器`
4. 选择 `Public` (公开)
5. 勾选 `Add a README file`
6. 点击 `Create repository`

#### 方法B: 使用命令行（已提供脚本）
如果你有Git Bash或终端，可以使用提供的脚本：
```bash
# 在项目目录运行
bash 上传GitHub.sh
```

### 步骤3: 上传代码

#### 方式A: 网页上传（最简单）
1. 打开你创建的仓库页面
2. 点击 `<> Code` 标签
3. 点击 `Add file` → `Upload files`
4. 将整个 `MyGameModifier` 文件夹拖入
5. 点击 `Commit changes`

#### 方式B: 使用Git命令
```bash
# 安装Git: https://git-scm.com/

# 进入项目目录
cd MyGameModifier

# 初始化Git
git init
git add .
git commit -m "Initial commit"

# 连接远程仓库（替换用户名）
git remote add origin https://github.com/alanliuc-a11y/MyGameModifier.git

# 推送代码
git branch -M main
git push -u origin main
```

### 步骤4: 等待自动编译

代码上传后，GitHub Actions会自动开始编译：

1. 打开仓库页面
2. 点击 `Actions` 标签
3. 看到 `Build APK` 工作流正在运行
4. 等待约 3-5 分钟

### 步骤5: 下载APK

编译完成后：
1. 点击 Actions 页面中的最新运行记录
2. 滚动到底部 `Artifacts` 部分
3. 点击 `MyModifier-APK` 下载
4. 解压得到 `app-debug.apk`

---

## 📦 方案二：使用GitHub Desktop（图形界面）

### 步骤1: 下载GitHub Desktop
- 下载: https://desktop.github.com/
- 安装并登录你的GitHub账号

### 步骤2: 创建本地仓库
1. 打开 GitHub Desktop
2. File → New repository
3. Name: `MyGameModifier`
4. Local path: 选择项目所在文件夹
5. 点击 Create repository

### 步骤3: 发布到GitHub
1. 点击 Publish repository
2. 勾选 Keep this code private（可选）
3. 点击 Publish repository

### 步骤4: 等待编译
自动跳转到方案一的步骤4

---

## ⚙️ 工作流说明

已配置的 `.github/workflows/build.yml`：

```yaml
name: Build APK
on:
  push:
    branches: [ main ]    # main分支推送时触发

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3           # 检出代码
      - uses: actions/setup-java@v3         # 设置JDK 17
      - uses: android-actions/setup-android@v2  # 设置Android SDK
      - run: ./gradlew assembleDebug        # 编译APK
      - uses: actions/upload-artifact@v3    # 上传APK
```

---

## 📥 下载APK的另一种方式

如果Artifacts下载不方便，可以修改工作流自动发布Release：

在 `build.yml` 末尾添加：
```yaml
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: app/build/outputs/apk/debug/app-debug.apk
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

这样APK会出现在Releases页面。

---

## 🐛 常见问题

### Q: Actions运行失败？
**检查**：
1. 是否上传了所有文件（包括 `gradlew`）
2. `gradlew` 是否有执行权限
3. 项目结构是否正确

### Q: 编译超时？
GitHub Actions免费版有6小时限制，Android编译通常5-10分钟完成。

### Q: 如何重新编译？
修改任意文件并push，或手动触发：Actions → Build APK → Run workflow

---

## ✅ 验证清单

- [ ] 创建了GitHub仓库
- [ ] 上传了完整项目代码
- [ ] Actions工作流成功运行
- [ ] 下载了APK文件
- [ ] 在手机上安装测试

---

## 📞 需要帮助？

GitHub Actions文档: https://docs.github.com/cn/actions

---

**推荐**: 使用方案一（网页上传）最简单！
