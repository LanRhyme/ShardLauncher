package com.lanrhyme.shardlauncher.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.graphics.PathParser

@Composable
fun rememberVectorPainter(pathData: String, tintColor: Color? = null): Painter {
    val path = remember(pathData) {
        try {
            PathParser.createPathFromPathData(pathData).asComposePath()
        } catch (e: Exception) {
            // Return an empty path if parsing fails
            Path()
        }
    }
    val brush = remember(tintColor) {
        tintColor?.let { SolidColor(it) }
    }
    return remember(path, brush) {
        VectorPainter(path, brush)
    }
}

class VectorPainter(
    private val path: Path,
    private val brush: SolidColor?
) : Painter() {

    override val intrinsicSize: Size
        get() {
            val bounds = path.getBounds()
            return Size(bounds.width, bounds.height)
        }

    override fun DrawScope.onDraw() {
        val pathWidth = path.getBounds().width
        val pathHeight = path.getBounds().height
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (pathWidth == 0f || pathHeight == 0f) return

        val scaleX = canvasWidth / pathWidth
        val scaleY = canvasHeight / pathHeight
        val scale = minOf(scaleX, scaleY)

        val translateX = (canvasWidth - pathWidth * scale) / 2
        val translateY = (canvasHeight - pathHeight * scale) / 2

        translate(left = translateX, top = translateY) {
            scale(scale = scale, pivot = path.getBounds().topLeft) {
                if (brush != null) {
                    drawPath(
                        path = path,
                        brush = brush
                    )
                } else {
                     drawPath(
                        path = path,
                        color = Color.Unspecified // Let the composable decide the color
                    )
                }
            }
        }
    }
}