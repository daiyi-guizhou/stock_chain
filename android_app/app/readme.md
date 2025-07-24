
Kotlin（推荐）
Google官方首推的Android开发语言


app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/stockapp/
│   │   ├── network/
│   │   │   ├── ApiService.kt
│   │   │   └── RetrofitClient.kt
│   │   ├── ui/
│   │   │   ├── MainActivity.kt
│   │   │   ├── BuyStockActivity.kt
│   │   │   └── ...
│   │   └── viewmodel/
│   │       └── StockViewModel.kt
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml
│       │   ├── activity_buy.xml
│       │   └── ...

安装 Android Studio  https://developer.android.com/studio?hl=zh-cn
配置好 Android SDK
确保后端服务正在运行（您已经完成）

2. 配置 Android SDK
打开 Android Studio
如果是首次启动，会进入欢迎界面，点击 "Start a new Android Studio project" 或 "Configure" > "SDK Manager"
在 SDK Manager 中：
    选择 "Android SDK" 标签页
    确认已安装最新版本的 "Android SDK Platform"
    推荐安装最新的 "Android SDK Build-Tools"
    确保安装了 "Android SDK Platform-Tools" 和 "Android SDK Tools"
点击 "Apply" 安装所选组件

二、打开项目
1. 打开项目文件夹
启动 Android Studio。
点击 "Open an existing Android Studio project"。
选择你的项目文件夹（即包含 build.gradle 的目录）。
等待项目同步完成。


三、连接设备
你可以选择以下任意一种方式运行应用：

方法一：使用真机调试
使用 USB 将手机连接到电脑。
在手机上允许 USB 调试模式（首次连接时会提示）。
在 Android Studio 中点击右上角的 "Select Device" 按钮（通常显示为设备名称或 "No Devices"）。
从设备列表中选择你的手机。


四、运行应用
1. 编译并运行
点击 Android Studio 工具栏上的 绿色运行按钮（▶️）或使用快捷键 Shift + F10。
Android Studio 会自动编译项目并安装到你选择的设备上。
2. 查看 Logcat 日志
底部点击 Logcat 标签页，可以查看应用运行时的日志输出，帮助调试

# Android Studio 配置步骤
打开项目：

    启动 Android Studio
    选择 "Open an existing Android Studio project"
    选择您的 android_app 项目根目录（确保选择的是包含 app 文件夹的目录）
等待同步：

    Android Studio 会自动检测并同步 Gradle 文件
    如果提示更新 Gradle wrapper，建议允许更新
检查 SDK 配置：

    如果提示缺少 SDK 组件，按照提示安装所需组件
    或者通过 File > Settings > Appearance & Behavior > System Settings > Android SDK 检查和安装 SDK
验证配置：

    确保项目能正常构建（点击 Build > Make Project）
    解决可能出现的任何导入或依赖问题
完成这些步骤后，您的项目应该能够在 Android Studio 中正常工作并运行。


