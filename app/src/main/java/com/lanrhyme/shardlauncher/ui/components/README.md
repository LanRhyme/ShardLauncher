# UI 组件文档

本目录包含 ShardLauncher 项目的各种 Jetpack Compose UI 组件。这些组件遵循 Material Design 3 指南，并提供一致的样式和行为。

所有组件均以可重用性为设计理念，旨在简化整个应用的 UI 开发。

## 组件列表

### 1. 通用组件 (`CommonComponents.kt`)

**`ScalingActionButton`**

带缩放动画的按钮，可在按下时提供视觉反馈。

- **`onClick`**: `() -> Unit` - 点击按钮时执行的操作。
- **`modifier`**: `Modifier` - 应用于按钮的修饰符。
- **`icon`**: `ImageVector?` - (可选) 按钮中显示的图标。
- **`text`**: `String?` - (可选) 按钮中显示的文本。
- **`enabled`**: `Boolean` - 控制按钮的启用状态。

**`TitleAndSummary`**

显示标题及其下方较小的半透明摘要的组合。

- **`title`**: `String` - 主标题文本。
- **`summary`**: `String?` - (可选) 显示在标题下方的摘要文本。
- **`modifier`**: `Modifier` - 应用于布局的修饰符。

**`CombinedCard`**

带有标题、摘要和内容的卡片容器。

- **`modifier`**: `Modifier` - 应用于卡片的修饰符。
- **`title`**: `String` - 卡片的标题。
- **`summary`**: `String?` - (可选) 卡片的摘要。
- **`content`**: `@Composable () -> Unit` - 卡片内部显示的内容。

**`SegmentedNavigationBar`**

分段导航栏，用于在不同页面或选项卡之间切换。

- **`title`**: `String` - 导航栏的标题。
- **`selectedPage`**: `T` - 当前选定的页面。
- **`onPageSelected`**: `(T) -> Unit` - 选择新页面时调用的回调。
- **`pages`**: `List<T>` - 要显示的页面列表。
- **`getTitle`**: `(T) -> String` - 从页面对象获取标题的函数。

**`StyledFilterChip`**

带样式的筛选条，用于选择和取消选择。

- **`selected`**: `Boolean` - 指示筛选条是否已选择。
- **`onClick`**: `() -> Unit` - 点击筛选条时调用的回调。
- **`label`**: `@Composable () -> Unit` - 筛选条上显示的标签。
- **`modifier`**: `Modifier` - 应用于筛选条的修饰符。

**`CollapsibleCard`**

一个可展开和折叠的卡片，用于显示或隐藏其内容。

- **`modifier`**: `Modifier` - 应用于卡片的修饰符。
- **`title`**: `String` - 卡片的标题。
- **`summary`**: `String?` - (可选) 显示在标题旁边的摘要。
- **`content`**: `@Composable () -> Unit` - 展开时在卡片内部显示的内容。

**`.glow` 修饰符**

一个 `Modifier` 扩展函数，为任何 Composable 添加可配置的发光效果。

- **`color`**: `Color` - 辉光的颜色。
- **`cornerRadius`**: `Dp` - 发光效果的圆角半径。
- **`blurRadius`**: `Dp` - 辉光的模糊半径。
- **`enabled`**: `Boolean` - 控制辉光效果是否启用。

### 2. 自定义视图

**`CustomCard.kt`**

一个简单的 `Card` 封装，提供一致的默认样式。

- **`modifier`**: `Modifier` - 应用于卡片的修饰符。
- **`shape`**: `Shape` - 卡片的形状。
- **`content`**: `@Composable ColumnScope.() -> Unit` - 卡片内部显示的内容。

**`CustomButton.kt`**

可自定义样式的按钮，支持填充、描边和文本三种样式。

- **`onClick`**: `() -> Unit` - 点击按钮时执行的操作。
- **`modifier`**: `Modifier` - 应用于按钮的修饰符。
- **`type`**: `ButtonType` - 按钮的样式 (`FILLED`, `OUTLINED`, `TEXT`)。
- **`enabled`**: `Boolean` - 控制按钮的启用状态。

**`CustomDialog.kt`**

一个简单的 `AlertDialog` 封装。

- **`onDismissRequest`**: `() -> Unit` - 当请求关闭对话框时调用。
- **`confirmButton`**: `@Composable () -> Unit` - 对话框的确认按钮。
- **`dismissButton`**: `@Composable (() -> Unit)?` - (可选) 对话框的取消按钮。
- **`icon`**: `@Composable (() -> Unit)?` - (可选) 对话框的图标。
- **`title`**: `@Composable (() -> Unit)?` - (可选) 对话框的标题。
- **`text`**: `@Composable (() -> Unit)?` - (可选) 对话框的正文。

**`CustomTextField.kt`**

一个 `OutlinedTextField` 的封装，提供一致的样式和行为。

- **`value`**: `String` - 文本字段的当前值。
- **`onValueChange`**: `(String) -> Unit` - 当值更改时调用。
- **`modifier`**: `Modifier` - 应用于文本字段的修饰符。
- ...以及其他 `OutlinedTextField` 参数。

### 3. 布局 (`Layouts.kt`)

**`SwitchLayout`**

带有标题、摘要和开关的布局。

- **`checked`**: `Boolean` - 开关是否已打开。
- **`onCheckedChange`**: `() -> Unit` - 当开关状态更改时调用。
- **`title`**: `String` - 布局的标题。
- **`summary`**: `String?` - (可选) 布局的摘要。

**`SimpleListLayout`**

用于显示项目列表并允许单选的布局。

- **`items`**: `List<E>` - 要显示的项目列表。
- **`selectedItem`**: `E` - 当前选定的项目。
- **`title`**: `String` - 列表的标题。
- **`getItemText`**: `@Composable (E) -> String` - 从项目对象获取文本的函数。
- **`onValueChange`**: `(E) -> Unit` - 当选择更改时调用。

**`SliderLayout`**

带有标题、摘要和滑块的布局。

- **`value`**: `Float` - 滑块的当前值。
- **`onValueChange`**: `(Float) -> Unit` - 当值更改时调用。
- **`title`**: `String` - 布局的标题。
- **`summary`**: `String?` - (可选) 布局的摘要。
- **`valueRange`**: `ClosedFloatingPointRange<Float>` - 滑块的值范围。

### 4. 特效 (`BackgroundLightEffect.kt`)

**`BackgroundLightEffect`**

创建平滑的、动画的背景光效。

- **`modifier`**: `Modifier` - 应用于效果的修饰符。
- **`themeColor`**: `Color` - 效果的基础颜色。
- **`animationSpeed`**: `Float` - 动画的速度。
