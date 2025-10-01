package com.lanrhyme.shardlauncher.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * An animated button that scales on press for tactile feedback.
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
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "buttonScale")

    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale),
        enabled = enabled,
        interactionSource = interactionSource
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
    summary: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = summary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
