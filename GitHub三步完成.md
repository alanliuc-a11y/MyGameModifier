# GitHub自动编译 - 完整操作指南

## 🎯 三步完成自动编译

---

## 第一步：准备项目

### 确认你的项目位置
项目已在你的电脑和手机中：
- **电脑**: `C:\Users\alanl\Documents\WPSDrive\123479445\WPS云盘\stepfun\MyGameModifier\`
- **手机**: `/sdcard/Projects/MyGameModifier/`

### 项目包含的文件
```
MyGameModifier/
├── build.gradle                      ✓
├── settings.gradle                   ✓
├── README.md                         ✓
├── README_GitHub.md                  ✓  (GitHub专用)
├── 开发指南.md                       ✓
├── GitHub上传指南.md                 ✓
├── 初始化Git.bat                     ✓  (双击运行)
├── 上传GitHub.sh                     ✓
└── app/                              ✓  (完整源码)
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/my/modifier/
        │   ├── MainActivity.java
        │   ├── GameModifierActivity.java
        │   ├── engine/MemoryEngine.java
        │   └── service/FloatingService.java
        └── res/
            ├── layout/
            ├── drawable/
            └── values/
```

---

## 第二步：上传到GitHub

### 方案A: 使用GitHub Desktop（推荐）

#### 1. 下载安装
- 官网: https://desktop.github.com/
- 下载安装并登录账号: **alanliuc-a11y**

#### 2. 创建本地仓库
1. 打开 GitHub Desktop
2. File → New repository
3. Name: `MyGameModifier`
4. Local path: 选择 `MyGameModifier` 文件夹
5. 点击 Create repository

#### 3. 发布到GitHub
1. 点击 Publish repository
2. 保持 Public（公开）
3. 点击 Publish repository

#### 4. 等待自动编译
1. 打开浏览器访问: `https://github.com/alanliuc-a11y/MyGameModifier`
2. 点击 Actions 标签
3. 看到 Build APK 正在运行（约3-5分钟）
4. 完成后点击下载

---

### 方案B: 使用命令行

#### 1. 安装Git
- 下载: https://git-scm.com/download/win
- 安装时选择默认选项

#### 2. 运行初始化脚本
1. 进入项目文件夹
2. 双击运行 `初始化Git.bat`
3. 等待完成

#### 3. 创建GitHub仓库
1. 打开 https://github.com/new
2. Repository name: `MyGameModifier`
3. Description: `自开发安卓游戏修改器`
4. 选择 Public
5. 点击 Create repository

#### 4. 上传代码
在命令行执行：
```cmd
cd "C:\Users\alanl\Documents\WPSDrive\123479445\WPS云盘\stepfun\MyGameModifier"
git remote add origin https://github.com/alanliuc-a11y/MyGameModifier.git
git branch -M main
git push -u origin main
```

---

### 方案C: 网页直接上传

#### 1. 创建仓库
1. 访问 https://github.com/new
2. 填写信息创建

#### 2. 打包项目
1. 将 `MyGameModifier` 文件夹压缩为 ZIP
2. 注意：不要包含 `.git` 文件夹

#### 3. 上传文件
1. 打开仓库页面
2. 点击 `Add file` → `Upload files`
3. 拖拽或选择 ZIP 文件
4. 点击 `Commit changes`

---

## 第三步：下载APK

### 1. 查看Actions
1. 打开: `https://github.com/alanliuc-a11y/MyGameModifier/actions`
2. 点击最新的 workflow run

### 2. 下载APK
1. 滚动到底部 `Artifacts` 部分
2. 点击 `MyModifier-APK` 下载
3. 解压 ZIP 得到 `app-debug.apk`

### 3. 安装到手机
```
方式1: adb install app-debug.apk
方式2: 复制到手机，点击安装
```

---

## 📸 操作截图示意

### GitHub Desktop操作
```
GitHub Desktop
├── File → New repository
│   ├── Name: MyGameModifier
│   ├── Local path: [选择文件夹]
│   └── Create repository ← 点击
│
└── Publish repository
    ├── 确认信息
    └── Publish repository ← 点击
```

### Actions编译
```
GitHub页面
├── Actions 标签
│   └── Build APK ← 绿色表示成功
│       └── click to download
│
└── Artifacts
    └── MyModifier-APK.zip ← 点击下载
```

---

## ✅ 验证清单

- [ ] GitHub Desktop 已安装
- [ ] 创建了 MyGameModifier 仓库
- [ ] 代码已上传到GitHub
- [ ] Actions 工作流成功运行
- [ ] 下载了 MyModifier-APK.zip
- [ ] 解压得到 app-debug.apk
- [ ] 成功安装到手机
- [ ] 应用正常运行

---

## 🐛 常见问题

### Q: Actions 运行失败？
**解决**:
1. 确认上传了所有文件
2. 确认包含 `.github/workflows/build.yml`
3. 重新上传触发编译

### Q: 没有 Actions 标签？
**解决**: 等待几分钟或刷新页面

### Q: APK下载不了？
**解决**: 
- 方法1: 登录GitHub账号
- 方法2: 检查网络连接

### Q: 安装失败？
**解决**: 
- 开启开发者模式
- 允许安装未知来源应用

---

## 📦 项目信息

- **仓库地址**: https://github.com/alanliuc-a11y/MyGameModifier
- **工作流配置**: `.github/workflows/build.yml`
- **编译时间**: 约 3-5 分钟
- **APK位置**: Actions → Artifacts

---

## 🎉 完成！

按照以上步骤，你将：
1. ✅ 拥有GitHub仓库
2. ✅ 代码自动编译
3. ✅ 获得APK文件
4. ✅ 可以持续更新

**下一步**: 在手机上安装测试！
