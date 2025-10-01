package com.lanrhyme.shardlauncher.ui.custom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

@Composable
fun XamlRenderer(nodes: List<XamlNode>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        nodes.forEach { node ->
            when (node) {
                is CardNode -> RenderCard(node)
                is StackPanelNode -> RenderStackPanel(node)
                is TextBlockNode -> RenderTextBlock(node)
                is ButtonNode -> RenderButton(node)
                is TextButtonNode -> RenderTextButton(node)
                is IconTextButtonNode -> RenderIconTextButton(node)
                is IconButtonNode -> RenderIconButton(node)
                is PathNode -> RenderPath(node)
                is HintNode -> RenderHint(node)
                is ImageNode -> RenderImage(node)
                is ListItemNode -> RenderListItem(node)
                is UnknownNode -> {
                     XamlRenderer(nodes = node.children)
                }
            }
        }
    }
}

@Composable
fun RenderCard(node: CardNode) {
    val title = node.attributes["Title"] ?: ""
    val margin = parseMargin(node.attributes["Margin"])
    val canSwap = node.attributes["CanSwap"] == "True"
    val isSwappedInitially = node.attributes["IsSwapped"] == "True"
    var isSwapped by remember { mutableStateOf(isSwappedInitially) }

    Card(
        modifier = Modifier.padding(margin).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical=8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (canSwap) Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isSwapped = !isSwapped }
                        else Modifier
                    )
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                if (canSwap) {
                    Icon(
                        imageVector = if (isSwapped) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isSwapped) "Expand" else "Collapse"
                    )
                }
            }
            AnimatedVisibility(visible = !isSwapped) {
                 XamlRenderer(nodes = node.children, modifier = Modifier.padding(bottom=8.dp))
            }
        }
    }
}

@Composable
fun RenderStackPanel(node: StackPanelNode) {
    val margin = parseMargin(node.attributes["Margin"])
    val orientation = node.attributes["Orientation"]
    val horizontalAlignment = parseHorizontalAlignment(node.attributes["HorizontalAlignment"])

    if (orientation == "Horizontal") {
        Row(
            modifier = Modifier.padding(margin).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, horizontalAlignment),
            verticalAlignment = Alignment.CenterVertically
        ) {
            node.children.forEach { child ->
                 when (child) {
                    is CardNode -> RenderCard(child)
                    is StackPanelNode -> RenderStackPanel(child)
                    is TextBlockNode -> RenderTextBlock(child)
                    is ButtonNode -> RenderButton(child)
                    is TextButtonNode -> RenderTextButton(child)
                    is IconTextButtonNode -> RenderIconTextButton(child)
                    is IconButtonNode -> RenderIconButton(child)
                    is PathNode -> RenderPath(child)
                    is HintNode -> RenderHint(child)
                    is ImageNode -> RenderImage(child)
                    is ListItemNode -> RenderListItem(child)
                    is UnknownNode -> {
                        XamlRenderer(nodes = child.children)
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.padding(margin).fillMaxWidth(), horizontalAlignment = horizontalAlignment) {
            XamlRenderer(nodes = node.children)
        }
    }
}

@Composable
fun RenderTextBlock(node: TextBlockNode) {
    val text = node.attributes["Text"]?.replace("&#xA;", "\n") ?: ""
    val margin = parseMargin(node.attributes["Margin"])
    val fontSize = node.attributes["FontSize"]?.let { (it.toFloatOrNull() ?: 14f).sp } ?: TextUnit.Unspecified
    val fontWeight = if (node.attributes["FontWeight"] == "Bold") FontWeight.Bold else FontWeight.Normal
    val color = parseColor(node.attributes["Foreground"]) ?: Color.Unspecified
    val textAlign = when(node.attributes["HorizontalAlignment"]){
        "Center" -> TextAlign.Center
        "Right" -> TextAlign.End
        else -> TextAlign.Start
    }
    var modifier = Modifier.padding(margin)
    if (textAlign == TextAlign.Center || textAlign == TextAlign.End) {
        modifier = modifier.fillMaxWidth()
    }

    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun RenderButton(node: ButtonNode) {
    val context = LocalContext.current
    val text = node.attributes["Text"] ?: ""
    val margin = parseMargin(node.attributes["Margin"])
    val width = parseSize(node.attributes["Width"])
    val height = parseSize(node.attributes["Height"])
    val padding = parseMargin(node.attributes["Padding"])
    val eventType = node.attributes["EventType"]
    val eventData = node.attributes["EventData"]

    Button(
        onClick = { handleEvent(context, eventType, eventData) },
        modifier = Modifier
            .padding(margin)
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier)
            .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier),
        contentPadding = padding
    ) {
        Text(text)
    }
}

@Composable
fun RenderTextButton(node: TextButtonNode) {
    val context = LocalContext.current
    val text = node.attributes["Text"] ?: ""
    val margin = parseMargin(node.attributes["Margin"])
    val eventType = node.attributes["EventType"]
    val eventData = node.attributes["EventData"]

    TextButton(
        onClick = { handleEvent(context, eventType, eventData) },
        modifier = Modifier.padding(margin)
    ) {
        Text(text)
    }
}

@Composable
fun RenderIconTextButton(node: IconTextButtonNode) {
    val context = LocalContext.current
    val text = node.attributes["Text"] ?: ""
    val logo = node.attributes["Logo"]
    val colorType = node.attributes["ColorType"]
    val eventType = node.attributes["EventType"]
    val eventData = node.attributes["EventData"]
    val margin = parseMargin(node.attributes["Margin"])
    val height = parseSize(node.attributes["Height"])
    val color = if (colorType == "Highlight") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Button(
        onClick = { handleEvent(context, eventType, eventData) },
        modifier = Modifier.padding(margin).then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
    ) {
        if (logo != null) {
            val painter = rememberVectorPainter(pathData = logo, tintColor = color)
            Icon(painter = painter, contentDescription = null, tint = color)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = color)
    }
}

@Composable
fun RenderIconButton(node: IconButtonNode) {
    val context = LocalContext.current
    val logo = node.attributes["Logo"]
    val eventType = node.attributes["EventType"]
    val eventData = node.attributes["EventData"]
    val margin = parseMargin(node.attributes["Margin"])
    val width = parseSize(node.attributes["Width"])
    val height = parseSize(node.attributes["Height"])

    if (logo != null) {
        IconButton(
            onClick = { handleEvent(context, eventType, eventData) },
            modifier = Modifier.padding(margin)
        ) {
            val painter = rememberVectorPainter(pathData = logo)
            Icon(
                painter = painter, 
                contentDescription = null, 
                modifier = Modifier.then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier)
                                .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
            )
        }
    }
}

@Composable
fun RenderPath(node: PathNode) {
    val data = node.attributes["Data"]
    val fill = parseColor(node.attributes["Fill"])
    val width = parseSize(node.attributes["Width"])
    val height = parseSize(node.attributes["Height"])
    val margin = parseMargin(node.attributes["Margin"])
    
    if (data != null) {
        val painter = rememberVectorPainter(pathData = data, tintColor = fill)
        Image(
            painter = painter, 
            contentDescription = null, 
            modifier = Modifier
                .padding(margin)
                .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier)
                .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
        )
    }
}


@Composable
fun RenderHint(node: HintNode) {
    val text = node.attributes["Text"]?.replace("&#xA;", "\n") ?: ""
    val margin = parseMargin(node.attributes["Margin"])
    val theme = node.attributes["Theme"]
    val backgroundColor = when (theme) {
        "Blue" -> Color(0xFFE7F3FF)
        "Yellow" -> Color(0xFFFFFBE6)
        "Red" -> Color(0xFFFDE2E2)
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = when (theme) {
        "Blue" -> Color(0xFF00529B)
        "Yellow" -> Color(0xFF8C7721)
        "Red" -> Color(0xFFD8000C)
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(modifier = Modifier.padding(margin).background(backgroundColor, MaterialTheme.shapes.medium).padding(12.dp).fillMaxWidth()) {
        Text(text, color = textColor)
    }
}

@Composable
fun RenderImage(node: ImageNode) {
    val source = node.attributes["Source"]
    if (source == null) return

    val height = parseSize(node.attributes["Height"])
    val horizontalAlignment = parseHorizontalAlignment(node.attributes["HorizontalAlignment"])

    Box(modifier = Modifier.fillMaxWidth()) {
        SubcomposeAsyncImage(
            model = source,
            contentDescription = null,
            modifier = Modifier
                .align(
                    when (horizontalAlignment) {
                        Alignment.CenterHorizontally -> Alignment.Center
                        Alignment.End -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                )
                .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier),
            loading = {
                Box(
                    modifier = Modifier.then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier.size(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        )
    }
}

@Composable
fun RenderListItem(node: ListItemNode) {
    val context = LocalContext.current
    val logo = node.attributes["Logo"]
    val title = node.attributes["Title"] ?: ""
    val info = node.attributes["Info"]
    val eventType = node.attributes["EventType"]
    val eventData = node.attributes["EventData"]
    val type = node.attributes["Type"]
    val margin = parseMargin(node.attributes["Margin"])
    
    val modifier = if (type == "Clickable") {
        Modifier.clickable { handleEvent(context, eventType, eventData) }
    } else {
        Modifier
    }

    Row(
        modifier = modifier.padding(margin).padding(vertical = 8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (logo != null) {
            if (logo.startsWith("pack://")) {
                // Handle built-in pack images later
            } else {
                 SubcomposeAsyncImage(
                    model = logo,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    loading = {
                        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                )
            }
           
            Spacer(Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            if (info != null) {
                Text(text = info, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

fun parseMargin(marginStr: String?): PaddingValues {
    if (marginStr == null) return PaddingValues(0.dp)
    val parts = marginStr.split(',').map { it.trim().toFloatOrNull() ?: 0f }
    return when (parts.size) {
        1 -> PaddingValues(parts[0].dp)
        2 -> PaddingValues(horizontal = parts[0].dp, vertical = parts[1].dp)
        4 -> PaddingValues(start = parts[0].dp, top = parts[1].dp, end = parts[2].dp, bottom = parts[3].dp)
        else -> PaddingValues(0.dp)
    }
}

fun parseSize(sizeStr: String?): Dp {
    return sizeStr?.toFloatOrNull()?.dp ?: Dp.Unspecified
}

@Composable
fun parseColor(colorStr: String?): Color? {
    return colorStr?.let { 
        if (it.startsWith("#")) {
            try {
                Color(android.graphics.Color.parseColor(it))
            } catch (e: IllegalArgumentException) {
                null
            }
        } else if (it.startsWith("{DynamicResource")) {
            // Basic theme color parsing
            val colorName = it.substringAfter(" ").substringBefore("}")
            when(colorName) {
                "ColorBrush1" -> return MaterialTheme.colorScheme.primary
                "ColorBrush2" -> return MaterialTheme.colorScheme.secondary
                "ColorBrush3" -> return MaterialTheme.colorScheme.tertiary
                "ColorBrush5" -> return MaterialTheme.colorScheme.surface
                "ColorBrush6" -> return MaterialTheme.colorScheme.onSurface
                else -> return null
            }
        } 
        else null
    }
}

fun parseHorizontalAlignment(alignStr: String?): Alignment.Horizontal {
    return when(alignStr) {
        "Center" -> Alignment.CenterHorizontally
        "Right" -> Alignment.End
        else -> Alignment.Start
    }
}
