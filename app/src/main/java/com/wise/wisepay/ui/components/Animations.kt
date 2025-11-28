package com.wise.wisepay.ui.components


import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.ui.theme.*

@Composable
fun PulsingNfcIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val scale by infiniteTransition.animateFloat(1f, 1.8f, infiniteRepeatable(tween(1500), RepeatMode.Restart), "Scale")
    val alpha by infiniteTransition.animateFloat(0.5f, 0f, infiniteRepeatable(tween(1500), RepeatMode.Restart), "Alpha")

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(120.dp)) { drawCircle(color = Lime, radius = size.minDimension / 2 * scale, alpha = alpha) }
        Box(Modifier.size(80.dp).clip(CircleShape).background(Lime), contentAlignment = Alignment.Center) {
            Text(")))", color = Forest, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun SwayingCardIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "SwayAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -8f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Rotation"
    )

    Box(contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        val cardResId = remember(context) { context.resources.getIdentifier("card", "drawable", context.packageName) }

        if (cardResId != 0) {
            Image(
                painter = painterResource(id = cardResId),
                contentDescription = "Tap Card",
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer { rotationZ = rotation; cameraDistance = 8f * density }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 64.dp)
                    .graphicsLayer { rotationZ = rotation }
                    .background(Lime, RoundedCornerShape(8.dp))
                    .border(2.dp, White, RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun InteractiveWiseGraph(data: List<Float>, minY: Float, maxY: Float, lineColor: Color, animationKey: Int) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(animationKey) { animationProgress.snapTo(0f); animationProgress.animateTo(1f, animationSpec = tween(1500)) }
    var dragX by remember { mutableFloatStateOf(-1f) }
    var activeGraphWidth by remember { mutableFloatStateOf(0f) }

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().pointerInput(Unit) { detectTapGestures(onPress = { offset -> dragX = offset.x.coerceIn(0f, activeGraphWidth); tryAwaitRelease(); dragX = -1f }) }.pointerInput(Unit) { detectHorizontalDragGestures(onDragStart = { offset -> dragX = offset.x.coerceIn(0f, activeGraphWidth) }, onDragEnd = { dragX = -1f }, onHorizontalDrag = { change, _ -> change.consume(); dragX = change.position.x.coerceIn(0f, activeGraphWidth) }) }) {
        val width = size.width; val height = size.height
        val labelWidth = 60.dp.toPx()
        val graphWidth = width - labelWidth
        val topPadding = 60.dp.toPx()
        val effectiveHeight = height - topPadding

        if (activeGraphWidth != graphWidth) activeGraphWidth = graphWidth

        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f); val gridColor = Color.Gray.copy(alpha = 0.1f)
        drawLine(color = gridColor, start = Offset(0f, topPadding + effectiveHeight * 0.5f), end = Offset(graphWidth, topPadding + effectiveHeight * 0.5f), pathEffect = dashEffect, strokeWidth = 1.dp.toPx())

        val textPaint = Paint().apply { color = TextGray.toArgb(); textSize = 32f; textAlign = Paint.Align.RIGHT; typeface = Typeface.DEFAULT_BOLD }
        drawContext.canvas.nativeCanvas.apply {
            drawText(String.format("%.2f", maxY), width, topPadding + 10f, textPaint)
            drawText(String.format("%.2f", (minY + maxY) / 2), width, topPadding + effectiveHeight / 2 + 10f, textPaint)
            drawText(String.format("%.2f", minY), width, height - 10f, textPaint)
        }

        if (data.size < 2) return@Canvas
        val points = mutableListOf<Offset>(); val spacing = graphWidth / (data.size - 1)
        data.forEachIndexed { index, value -> points.add(Offset(index * spacing, topPadding + effectiveHeight - (value * effectiveHeight))) }

        val path = Path(); path.moveTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
            val p0 = points.getOrElse(i - 1) { points[i] }; val p1 = points[i]; val p2 = points[i + 1]; val p3 = points.getOrElse(i + 2) { points[i + 1] }
            val cp1x = p1.x + (p2.x - p0.x) * 0.2f; val cp1y = p1.y + (p2.y - p0.y) * 0.2f; val cp2x = p2.x - (p3.x - p1.x) * 0.2f; val cp2y = p2.y - (p3.y - p1.y) * 0.2f
            path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }

        val lineVisibleWidth = graphWidth * animationProgress.value; val currentCursorX = if (dragX != -1f) dragX else lineVisibleWidth

        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(points.last().x, height)
        fillPath.lineTo(points.first().x, height)
        fillPath.close()

        clipRect(right = currentCursorX) {
            drawPath(
                fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Lime.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )
        }

        clipRect(right = lineVisibleWidth) { drawPath(path = path, color = lineColor, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)) }

        if (animationProgress.value > 0.05f) {
            val activeIndexFloat = (currentCursorX / spacing).coerceIn(0f, (data.size - 1).toFloat()); val idx = activeIndexFloat.toInt(); val nextIdx = (idx + 1).coerceAtMost(data.size - 1); val fraction = activeIndexFloat - idx
            val pStart = points[idx]; val pEnd = points[nextIdx]; val mu2 = (1 - kotlin.math.cos(fraction * Math.PI)) / 2; val currentY = (pStart.y * (1 - mu2) + pEnd.y * mu2).toFloat()

            if (dragX != -1f) {
                val currentValue = 11.24
                val label = "+ $${currentValue}"

                drawLine(
                    color = Forest.copy(alpha = 0.5f),
                    start = Offset(currentCursorX, topPadding),
                    end = Offset(currentCursorX, height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )

                drawCircle(White, 8.dp.toPx(), Offset(currentCursorX, currentY))
                drawCircle(Forest, 5.dp.toPx(), Offset(currentCursorX, currentY))

                drawContext.canvas.nativeCanvas.apply {
                    val p = Paint().apply { color = Forest.toArgb(); textSize = 40f; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
                    drawText(label, currentCursorX, currentY - 60f, p)
                }
            } else {
                drawCircle(Forest, 6.dp.toPx(), Offset(currentCursorX, currentY))
            }
        }
    }
}