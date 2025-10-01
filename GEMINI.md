# ShardLauncher 项目

## 1. 项目概述

本项目名为 **ShardLauncher**，是一款适用于 Android 平台的 Minecraft Java版 启动器。

## 2. 核心技术

- **语言**: Kotlin
- **UI 工具包**: Jetpack Compose
- **构建系统**: Gradle

## 3. 架构设计

本启动器遵循前后端分离的设计模式：

- **前端**: 用户界面和用户体验逻辑将使用 Kotlin 和 Jetpack Compose 从零开始构建。前端源代码位于 `app/` 目录下。
- **后端**: 核心的游戏启动、版本管理和安装逻辑由 **Amethyst-Android** 项目提供。后端代码位于 `backend/amethyst/` 目录下，应被视为一个库或依赖项。
- **参考与灵感**: 设计、功能和整体用户体验应主要参考 **FoldCraftLauncher (FCL)**。

## 4. AI 开发指南

在协助开发时，请遵循以下原则：

- **技术栈**: 在 `app` 模块中，所有新的UI和应用逻辑都必须严格使用 Kotlin 和 Jetpack Compose。
- **代码风格**: 编写整洁、地道的 Kotlin 代码。遵循现代 Android 开发最佳实践和 Material Design 3 设计准则。
- **后端交互**: 除非有明确要求，否则不要修改 `backend/amethyst/` 中的后端逻辑。应将其视为用于游戏启动的稳定API。所有与 Minecraft 相关的交互（例如，安装、启动、管理版本）都应委托给后端处理。
- **开发焦点**: 主要的开发焦点是在 `app` 模块中构建一个高质量、用户友好的前端，并确保其能正确地与 Amethyst 后端进行交互。

控件统一为圆角矩形，我若没有明确提及统一22.dp圆角。
控件设计参考https://zread.ai/ZalithLauncher/ZalithLauncher2/9-ui-components-and-layout-design
参考以下文档进行设计
# UI 组件文档

> 采用模块化组件架构，将 UI 元素组织为专门类别，促进可重用性和可维护性。该组件系统基于 Jetpack Compose 和 Material Design 3 原则构建。

---

## 1. 布局组件

构成 UI 结构的基础，为组织界面元素提供灵活且可重用的构建块。

### 1.1 ScalingLabel 组件

带缩放效果的动画标签元素，适合突出重要信息。

**来源：** `Layouts.kt`

```kotlin
@Composable
fun ScalingLabel(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = itemLayoutColor(),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shadowElevation: Dp = 1.dp
)
```

**核心特性：**
- 组合时从 0.95f 到 1.0f 的动画缩放
- 可自定义形状、颜色、阴影高度
- 带 `onClick` 的可点击变体
- 一致的内边距和样式

---

### 1.2 SimpleListLayout 组件

通过流畅动画处理选择、展开和项管理的强大列表组件。

**来源：** `Layouts.kt`

```kotlin
@Composable
fun <E> SimpleListLayout(
    modifier: Modifier = Modifier,
    items: List<E>,
    currentId: String,
    defaultId: String,
    title: String,
    summary: String? = null,
    getItemText: @Composable (E) -> String,
    getItemId: (E) -> String,
    getItemSummary: (@Composable (E) -> Unit)? = null,
    enabled: Boolean = true,
    autoCollapse: Boolean = true,
    onValueChange: (E) -> Unit = {}
)
```

**核心特性：**
- 支持任意数据类型的泛型
- 动画展开/折叠过渡
- 单选按钮选择带视觉反馈
- 选择后自动折叠
- 禁用状态支持（alpha 透明度）

---

### 1.3 SwitchLayout 组件

标准化切换开关组件，具有一致样式和交互模式。

**来源：** `Layouts.kt`

```kotlin
@Composable
fun SwitchLayout(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(22.0.dp),
    trailingIcon: @Composable RowScope.() -> Unit? = null
)
```

**核心特性：**
- Material Design 3 开关实现
- 标题和摘要布局模式
- 可自定义形状和尾部图标
- 整行点击处理
- 启用/禁用状态管理

---

## 2. 交互式按钮组件

提供丰富的视觉反馈和无障障支持。

### 2.1 ScalingActionButton 组件

动画按钮，按下时缩放，提供触觉反馈。

**来源：** `Buttons.kt`

```kotlin
@Composable
fun ScalingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
)
```

**核心特性：**
- 弹簧动画（按下时 0.95f 缩放）
- 全自定义 Material 3 按钮样式
- 按压检测与无障碍支持

---

### 2.2 IconTextButton 组件

结合图标与文本的多功能按钮。

**来源：** `Buttons.kt`

```kotlin
@Composable
fun IconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    iconSize: Dp = 24.dp,
    painter: Painter,
    text: String,
    contentDescription: String? = text,
    style: TextStyle = MaterialTheme.typography.labelMedium
)
```

**核心特性：**
- 支持 `Painter` 和 `ImageVector` 图标
- 可自定义图标大小和文本样式
- 一致间距与对齐方式
- 可点击表面与形状裁剪

---

### 2.3 TooltipIconButton 组件

带集成提示功能的图标按钮。

**来源：** `Buttons.kt`

```kotlin
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TooltipIconButton(
    modifier: Modifier = Modifier,
    tooltipTitle: String,
    tooltipMessage: String,
    content: @Composable () -> Unit
)
```

**核心特性：**
- 支持标题和消息的丰富提示
- 点击切换可见性
- 持久化提示状态管理
- Material 3 `RichTooltip` 实现

---

## 3. 文本与排版组件

提供高级排版功能，支持自动调整大小和跑马灯效果。

### 3.1 AutoSizeText 组件

智能文本组件，自动调整字体大小以适应容器边界。

**来源：** `Texts.kt`

```kotlin
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    autoSize: TextAutoSize? = null
)
```

**核心特性：**
- 完整文本样式自定义
- 自动调整大小功能（`TextAutoSize`）
- 溢出处理与文本换行
- 布局结果回调支持

---

### 3.2 MarqueeText 组件

带水平滚动动画的文本组件，适用于长文本内容。

**来源：** `Buttons.kt`

```kotlin
@Composable
fun MarqueeText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null
)
```

**核心特性：**
- 溢出文本连续水平滚动
- 可自定义颜色与文本对齐方式
- 单行约束与自动滚动

---

## 4. 主题系统集成

UI 组件与主题系统深度集成，支持多种配色方案。

### 4.1 可用配色方案

启动器支持多种预定义配色方案，**默认主题为绿色**。

### 4.2 主题实现

**来源：** `Theme.kt`

```kotlin
@Composable
fun ZalithTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeType: ThemeType = AllSettings.themeType.get(),
    content: @Composable () -> Unit
)
```

**核心特性：**
- Android 12+ 动态颜色支持
- 多种主题类型与用户偏好
- 带动画的流畅主题过渡
- 系统主题检测与回退

---

## 5. 高级布局模式

### 5.1 标题和摘要模式

在应用中显示层次信息的一致布局模式。

**来源：** `Layouts.kt`

```kotlin
@Composable
fun TitleAndSummary(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null
)
```

**实现模式：**
- 标题使用 `MaterialTheme.typography.titleSmall`
- 摘要使用带 0.7f alpha 的 `MaterialTheme.typography.labelSmall`
- 元素间 4.dp 垂直间距
- 所有组件保持一致

---

### 5.2 焦点管理

`FocusableBox` 提供程序化焦点控制，支持无障碍和键盘导航。

**来源：** `Layouts.kt`

```kotlin
@Composable
fun FocusableBox(
    modifier: Modifier = Modifier,
    requestKey: Any? = null
)
```

**核心特性：**
- 组合时自动请求焦点
- 基于键的焦点管理
- 与 Compose 焦点系统集成
- 无障碍支持

---

## 6. 性能优化

UI 组件系统包含多项性能优化，确保流畅体验：

- 广泛使用 `remember{}` 最小化重组
- 动画使用弹簧规范，响应自然
- 使用 `BasicText` 实现高效文本渲染

### 6.1 动画优化

```kotlin
// 示例：弹簧动画参数
val springSpec = spring<Float>(
    dampingRatio = 0.4f,
    stiffness = Spring.StiffnessLow
)
```

### 6.2 内存管理

- 合理状态管理实现组件复用
- 轻量级修饰符链，避免嵌套
- 控制动画生命周期，减少渲染开销
