package com.lanrhyme.shardlauncher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsibleCard(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TitleAndSummary(
                    modifier = Modifier.weight(1f),
                    title = title,
                    summary = summary
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CombinedCard(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            TitleAndSummary(title = title, summary = summary)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

/**
 * An animated button that scales on press for tactile feedback, with a default gradient background.
 *
 * @param onClick The action to perform when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param icon The icon to display in the button.
 * @param text The text to display in the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be clickable
 * and will appear disabled to the user.
 */
@Composable
fun ScalingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String? = null,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 4.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "buttonScale")

    val backgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary,
        )
    )
    val buttonShape = RoundedCornerShape(100.dp)

    val buttonModifier = modifier
        .scale(scale)
        .background(backgroundBrush, shape = buttonShape)

    Button(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        shape = buttonShape
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null, // Decorative icon
                    modifier = Modifier.size(24.dp)
                )
                if (text != null) {
                    Spacer(Modifier.size(8.dp))
                }
            }
            text?.let {
                Text(it)
            }
        }
    }
}

/**
 * A composable that displays a title with a smaller, semi-transparent summary below it.
 *
 * @param title The main title text.
 * @param summary The summary text, displayed below the title.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun TitleAndSummary(
    title: String,
    summary: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
        summary?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SegmentedNavigationBar(
    modifier: Modifier = Modifier,
    title: String,
    selectedPage: T,
    onPageSelected: (T) -> Unit,
    pages: List<T>,
    getTitle: (T) -> String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .glow(
                    color = MaterialTheme.colorScheme.primary,
                    cornerRadius = 22.dp
                )
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        PrimaryTabRow(
            selectedTabIndex = pages.indexOf(selectedPage),
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(22.dp)),
            divider = { },
        ) {
            pages.forEach { page ->
                Tab(
                    modifier = Modifier.height(40.dp),
                    selected = selectedPage == page,
                    onClick = { onPageSelected(page) },
                    text = { Text(text = getTitle(page)) }
                )
            }
        }
    }
}

@Composable
fun SubPageNavigationBar(
    title: String = "返回",
    description: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (description != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * A modifier that applies a glow effect around the composable's bounds.
 *
 * @param color The color of the glow.
 * @param cornerRadius The corner radius of the glowing shape.
 * @param blurRadius The blur radius of the glow effect.
 * @param enabled Toggles the glow effect on or off.
 */
fun Modifier.glow(
    color: Color,
    cornerRadius: Dp = 0.dp,
    blurRadius: Dp = 12.dp,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this

    val shadowColor = color.copy(alpha = 0.7f).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()

    this.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = transparent
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                0f,
                0f,
                shadowColor
            )
            it.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                cornerRadius.toPx(),
                cornerRadius.toPx(),
                paint
            )
        }
    }
}
