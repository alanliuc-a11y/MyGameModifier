# 我的游戏修改器 (MyGameModifier)

[![Build APK](https://github.com/alanliuc-a11y/MyGameModifier/actions/workflows/build.yml/badge.svg)](https://github.com/alanliuc-a11y/MyGameModifier/actions)

一个自开发的Android游戏内存修改器，支持内存搜索和数值修改功能。

## ✨ 功能特性

- 🔍 **内存搜索** - 支持精确搜索和模糊搜索
- ✏️ **数值修改** - 修改游戏金币、血量等数值
- 🔒 **数值锁定** - 防止游戏刷新（待实现）
- 🎈 **悬浮窗** - 游戏内快捷操作
- 📱 **免Root** - 支持虚拟机和Root两种模式

## 📦 下载APK

### 自动编译版本
- 点击 [Actions](https://github.com/alanliuc-a11y/MyGameModifier/actions) 查看最新构建
- 下载 `MyModifier-APK` 工件
- 解压安装 `app-debug.apk`

### 手动编译
```bash
./gradlew assembleDebug
```

## 🚀 使用方法

### 1. 启动应用
- 打开修改器
- 选择要修改的游戏APK（如果未安装）
- 授予悬浮窗权限

### 2. 搜索数值
```
1. 在游戏中查看当前金币数（如：1000）
2. 在修改器输入1000
3. 点击"搜索"
4. 等待搜索结果
```

### 3. 精确定位
```
1. 在游戏中消耗一些金币（变为900）
2. 在修改器输入900
3. 点击"改善"（Refine）
4. 重复直到只剩1-3个地址
```

### 4. 修改数值
```
1. 输入新数值（如：999999）
2. 点击"修改"
3. 返回游戏查看效果
```

## ⚙️ 系统要求

- Android 5.0+ (API 21+)
- 存储权限
- 悬浮窗权限

### 运行模式

| 模式 | 要求 | 说明 |
|-----|------|------|
| Root模式 | 需要Root权限 | 直接修改系统内存 |
| 虚拟机模式 | VMOS等虚拟机 | 在虚拟空间内修改 |

## 🏗️ 项目结构

```
MyGameModifier/
├── app/src/main/java/com/my/modifier/
│   ├── MainActivity.java              # 主界面
│   ├── GameModifierActivity.java      # 修改器界面
│   ├── engine/
│   │   └── MemoryEngine.java         # 内存引擎核心
│   └── service/
│       └── FloatingService.java       # 悬浮窗服务
└── .github/workflows/
    └── build.yml                      # CI/CD配置
```

## 🛠️ 技术实现

### 内存搜索
- 读取 `/proc/pid/maps` 获取内存映射
- 扫描可读写内存区域
- 4字节对齐搜索数值

### 数值修改
- 写入 `/proc/pid/mem` 修改内存
- 支持多种数值类型（int/float）
- 小端序字节处理

### 悬浮窗
- `SYSTEM_ALERT_WINDOW` 权限
- `WindowManager` 悬浮球
- `TYPE_APPLICATION_OVERLAY` 类型

## 📝 开发文档

### 核心类

#### MemoryEngine
```java
// 搜索数值
List<SearchResult> results = engine.searchValue(target, ValueType.INT_32);

// 修改数值
boolean success = engine.modifyValue(address, newValue, ValueType.INT_32);

// 锁定数值（循环写入）
engine.lockValue(address, value, ValueType.INT_32);
```

### 扩展功能

你可以添加：
- 模糊搜索（范围搜索）
- 多地址批量修改
- 自动脚本录制/回放
- 游戏存档导入/导出

## ⚠️ 注意事项

1. **仅限学习研究使用**
2. 联网游戏修改可能导致封号
3. 部分游戏有防作弊检测
4. Android 12+ 可能需要额外权限

## 📄 开源许可

仅供学习研究使用，请勿用于非法用途。

## 🤝 贡献

欢迎提交Issue和PR！

## 📧 联系

GitHub Issues: https://github.com/alanliuc-a11y/MyGameModifier/issues

---

**作者**: alanliuc-a11y  
**版本**: 1.0  
**日期**: 2026-05-17
