
Kotlin（推荐）
Google官方首推的Android开发语言



安装 Android Studio  https://developer.android.com/studio?hl=zh-cn
配置好 Android SDK
确保后端服务正在运行（您已经完成）

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

app/
├── src/main/
│   ├── java/com/example/stockapp/     # 应用的Java/Kotlin源代码
│   │   ├── adapter/                   # RecyclerView适配器类
│   │   ├── model/                     # 数据模型类
│   │   ├── network/                   # 网络请求相关类
│   │   ├── ui/                        # 界面Activity和Fragment
│   │   └── viewmodel/                # ViewModel类（MVVM架构）
│   ├── res/                          # 应用资源文件
│   │   ├── layout/                   # 布局文件（XML）
│   │   └── values/                   # 资源值文件（字符串、颜色、样式等）
│   └── AndroidManifest.xml           # 应用配置文件
└── readme.md                         # 项目说明文档


1. UI布局文件 (res/layout/)
activity_main.xml - 主界面布局文件，包含股票和用户信息展示、查询功能等
activity_buy.xml - 购买股票界面布局
item_stock.xml - 股票列表项的布局样式
item_user.xml - 用户列表项的布局样式
2. 资源文件 (res/values/)
colors.xml - 定义应用中使用的颜色值
strings.xml - 定义应用中的字符串资源，便于国际化
styles.xml - 定义应用的样式和主题