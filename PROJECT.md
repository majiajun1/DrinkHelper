# DrinkHelper 项目文档

## 简介
- 应用名称：DrinkHelper
- 类型：Android 应用（Java 业务，ViewBinding + ConstraintLayout 布局）
- 包名：`com.example.drinkhelper`
- 主要功能：主页面包含两个模块
  - 倒计时模块（布局就绪，未接入逻辑）
  - 喝水进度模块（展示目标值与当前进度，按钮位于进度条下方）

## 技术栈与版本
- 构建系统：Gradle（版本目录管理）
- Android Gradle Plugin：`8.13.2`（见 `gradle/libs.versions.toml:2`）
- Kotlin：`2.0.21`（见 `gradle/libs.versions.toml:3`）
- Java 版本：`11`（见 `app/build.gradle:31-36`）
- `compileSdk` / `targetSdk`：`36`（见 `app/build.gradle:10-18`）
- `minSdk`：`23`（见 `app/build.gradle:16`）
- 启用特性：`viewBinding=true`（见 `app/build.gradle:37-41`）

## 构建与运行
- 命令行构建：
  - 调试构建：`./gradlew assembleDebug`
  - 安装到设备：`./gradlew installDebug`
  - 连接设备运行仪器测试：`./gradlew connectedAndroidTest`
- IDE（Android Studio）：
  - 使用 Run 按钮选择 `MainActivity` 启动
  - 在 AVD 管理器创建模拟器后运行

## 目录结构
- 根目录
  - `settings.gradle` 与仓库配置 `settings.gradle:1-23`
  - 顶层 `build.gradle` 插件管理 `build.gradle:2-6`
  - `gradle/libs.versions.toml` 版本目录
  - `gradle.properties` 全局构建配置（AndroidX、R 类等）`gradle.properties:17-23`
- 模块 `app`
  - `app/build.gradle` 模块构建与依赖
  - `src/main/AndroidManifest.xml` 应用清单（入口 Activity）`app/src/main/AndroidManifest.xml:15-24`
  - 代码：`src/main/java/com/example/drinkhelper`
    - `MainActivity.java` 入口 Activity `app/src/main/java/com/example/drinkhelper/MainActivity.java:25-33`
  - 资源：`src/main/res`
    - 布局
      - `mainpage.xml` 主页面，包含两个模块 `app/src/main/res/layout/mainpage.xml:10-17,26-32`
      - `time_module.xml` 倒计时模块布局 `app/src/main/res/layout/time_module.xml:12-23,26-36,39-72`
      - `water_count_module.xml` 喝水模块布局 `app/src/main/res/layout/water_count_module.xml:10-21,25-35,35-49,52-80`
    - 其他资源（图标、colors、xml 备份规则等）
  - 测试
    - `src/test/java/.../ExampleUnitTest.kt` 单元测试示例 `app/src/test/java/com/example/drinkhelper/ExampleUnitTest.kt:12-16`
    - `src/androidTest/java/.../ExampleInstrumentedTest.kt` 仪器测试示例 `app/src/androidTest/java/com/example/drinkhelper/ExampleInstrumentedTest.kt:18-23`

## 代码概览
### MainActivity
- 入口与绑定
  - 加载主页面绑定并设置内容视图 `app/src/main/java/com/example/drinkhelper/MainActivity.java:28-30`
  - 通过主页面绑定直接获取两个包含的模块绑定字段：
    - 倒计时：`timeModuleBinding=mainpageBinding.includeCountdown;` `app/src/main/java/com/example/drinkhelper/MainActivity.java:31`
    - 喝水进度：`waterCountModuleBinding=mainpageBinding.includeWaterCount;` `app/src/main/java/com/example/drinkhelper/MainActivity.java:32`
- 当前未实现事件逻辑与数据更新；类内的 `currentWater`、`targetWater` 尚未使用 `app/src/main/java/com/example/drinkhelper/MainActivity.java:22-23`

### 布局模块
- 主页面 `mainpage.xml`
  - 垂直线性布局，依次包含倒计时模块、分割线、喝水模块 `app/src/main/res/layout/mainpage.xml:10-17,19-23,26-32`
  - 两个 `include` 都使用 `wrap_content` 高度以便稳定测量
- 喝水模块 `water_count_module.xml`
  - 标题与数值居中约束到顶部 `app/src/main/res/layout/water_count_module.xml:10-21,25-33`
  - 进度条：`0dp` 宽度，约束到父左右边，适配不同屏宽 `app/src/main/res/layout/water_count_module.xml:35-49`
  - 三按钮并排的水平约束链（位于进度条下方）：
    - 左按钮：`设置喝水目标` 锚到父左边，并连接中间按钮 `app/src/main/res/layout/water_count_module.xml:62-70`
    - 中按钮：`设置喝水量` 连接左右按钮，并指定链样式 `spread_inside` `app/src/main/res/layout/water_count_module.xml:52-60`
    - 右按钮：`增加喝水量` 锚到父右边，并连接中间按钮 `app/src/main/res/layout/water_count_module.xml:72-80`
- 倒计时模块 `time_module.xml`
  - 标题居中于顶部 `app/src/main/res/layout/time_module.xml:12-23`
  - 时间显示在标题下方居中 `app/src/main/res/layout/time_module.xml:26-36`
  - 四个按钮成两列分布，纵向排列于时间显示下方 `app/src/main/res/layout/time_module.xml:39-72`

## 依赖清单（关键）
- AndroidX 核心与生命周期：`core-ktx`、`lifecycle-runtime-ktx`
- AppCompat 与 ConstraintLayout：`appcompat`、`constraintlayout`
- Compose 相关依赖保留但 `compose` 特性关闭（可清理或后续启用）
- 测试：`junit`、`androidx.test.ext:junit`、`espresso-core`
- 详见 `app/build.gradle:43-61` 与版本目录 `gradle/libs.versions.toml:14-31`

## 交互与数据
- 当前项目未实现喝水与倒计时的业务交互逻辑；按钮尚未绑定点击事件。
- 可选的后续实现方向：
  - 在 `MainActivity` 中为三个按钮添加监听，维护 `currentWater` / `targetWater` 并刷新进度与文本
  - 添加倒计时控制类，管理时间显示与四个按钮事件
  - 使用 `SharedPreferences` 持久化喝水数据，应用重启后恢复

## 布局约束说明
- ConstraintLayout 运行时必须使用真实约束（`app:layout_constraintStart/End/Top/Bottom`）；设计器专用的 `tools:*` 属性不参与运行时布局。
- 横向自适应推荐使用 `layout_width="0dp"` 并约束到父容器的左右边。
- 水平约束链可通过互相的 `Start/End` 连接并设置 `app:layout_constraintHorizontal_chainStyle` 实现等距或紧凑排列。

## 约定与风格
- 视图绑定：通过 `MainpageBinding` 拿到包含模块的绑定对象，避免 `findViewById`
- 模块化布局：主页面用 `<include>` 组织模块，模块自身使用 `wrap_content` 高度以稳定参与测量
- 版本统一：依赖版本集中在 `libs.versions.toml` 管理，便于升级与一致性

## 后续工作建议
- 为按钮接入逻辑与持久化（喝水模块）
- 创建倒计时控制类并接入事件（倒计时模块）
- 根据实际需求清理或启用 Compose 特性，避免冗余依赖
- 增加 UI 测试覆盖按钮交互与进度更新

 