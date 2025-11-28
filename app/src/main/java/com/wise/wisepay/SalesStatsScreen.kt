package com.wise.wisepay

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wise.wisepay.ui.theme.WisePayTheme

val WiseLightBg = Color(0xFFFFFFFF)
val WiseLightSurface = Color(0xFFF2F5F7)
val WiseTextPrimary = Color(0xFF0E0E0E)
val WiseTextSecondary = Color(0xFF5D6B75)
val WiseGreenDark = Color(0xFF163300)
val WiseLime = Color(0xFF9FE870)
val WiseRangeHighlight = Color(0xFFD8F2C4)

data class CurrencyItem(
    val amount: String,
    val currencyCode: String,
    val name: String,
    val flagEmoji: String
)

@Composable
fun SalesStatsScreen() {
    var showCalendar by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("Select Date") }
    var isStatsVisible by remember { mutableStateOf(false) }
    var graphAnimationKey by remember { mutableIntStateOf(0) }
    var selectedTimeFilter by remember { mutableStateOf("Month") }

    val graphDataMap = remember {
        mapOf(
            "All time" to listOf(0.2f, 0.3f, 0.5f, 0.4f, 0.6f, 0.8f, 0.7f, 0.9f, 0.85f, 1.0f),
            "Year" to listOf(0.5f, 0.6f, 0.4f, 0.7f, 0.5f, 0.6f, 0.8f, 0.7f, 0.9f, 0.8f),
            "Month" to listOf(0.4f, 0.6f, 0.3f, 0.7f, 0.5f, 0.85f, 0.6f, 0.95f, 0.7f, 0.8f),
            "Day" to listOf(0.3f, 0.4f, 0.35f, 0.5f, 0.45f, 0.55f, 0.5f, 0.6f, 0.55f, 0.7f)
        )
    }

    val currentGraphData = graphDataMap[selectedTimeFilter] ?: listOf()

    val currencyList = remember {
        listOf(
            CurrencyItem("1,250.00", "EUR", "Euro", "🇪🇺"),
            CurrencyItem("890.50", "USD", "US Dollar", "🇺🇸"),
            CurrencyItem("4,320.00", "PLN", "Polish Zloty", "🇵🇱")
        )
    }

    if (showCalendar) {
        WiseRangeCalendarPicker(
            onDismiss = { showCalendar = false },
            onRangeSelected = { start, end, monthName ->
                val newText = if (start == end) "$monthName $start" else "$monthName $start - $end"
                if (newText != selectedDateText || !isStatsVisible) {
                    selectedDateText = newText
                    graphAnimationKey++
                    isStatsVisible = true
                }
                showCalendar = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WiseLightBg)
    ) {
        if (!isStatsVisible) {
            // Начальный экран (центрированный)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calendar_large),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Check your sales",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = WiseTextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                MonthSelector(
                    currentLabel = selectedDateText,
                    onClick = { showCalendar = true }
                )
            }
        }

        AnimatedVisibility(
            visible = isStatsVisible,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(initialOffsetY = { it / 10 }, animationSpec = tween(800)),
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                containerColor = WiseLightBg,
                // TopBar убираем из Scaffold, чтобы он скроллился вместе со всем контентом
                // BottomBar оставляем, чтобы кнопка была прибита к низу
                bottomBar = {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WiseLime),
                            shape = RoundedCornerShape(32.dp)
                        ) {
                            Text("Back", color = WiseGreenDark, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            ) { paddingValues ->
                // Используем LazyColumn для ВСЕГО контента
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 24.dp,
                        bottom = paddingValues.calculateBottomPadding() + 24.dp, // Учитываем место под кнопку
                        start = 24.dp,
                        end = 24.dp
                    )
                ) {
                    // 1. Заголовок
                    item {
                        Text(
                            text = "Way POS",
                            color = WiseTextPrimary,
                            fontSize = 34.sp,
                            fontFamily = InterFont,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // 2. Кнопка выбора даты
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MonthSelector(
                                currentLabel = selectedDateText,
                                onClick = { showCalendar = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // 3. Общая сумма
                    item {
                        TotalSalesBlock()
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // 4. График
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = WiseLightSurface),
                            shape = RoundedCornerShape(32.dp),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    InteractiveWiseGraph(
                                        data = currentGraphData,
                                        minY = 1.15f,
                                        maxY = 1.25f,
                                        lineColor = WiseGreenDark,
                                        animationKey = graphAnimationKey
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                TimeRangeSelector(
                                    selectedFilter = selectedTimeFilter,
                                    onFilterSelected = { filter ->
                                        selectedTimeFilter = filter
                                        graphAnimationKey++
                                        selectedDateText = "Date"
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    // 5. Заголовок списка
                    item {
                        Text(
                            text = "Breakdown",
                            color = WiseTextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = InterFont,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }

                    // 6. Список валют
                    items(currencyList) { item ->
                        LightCurrencyRow(item)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TimeRangeSelector(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All time", "Year", "Month", "Day")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) WiseLime else Color.Transparent)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) WiseGreenDark else WiseTextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun MonthSelector(currentLabel: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = WiseLightSurface,
        modifier = Modifier
            .height(56.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = currentLabel,
                color = WiseGreenDark,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.KeyboardArrowDown,
                null,
                tint = WiseGreenDark,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TotalSalesBlock() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Total sales", color = WiseTextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = InterFont)
        Text(text = "€ 2,540.50", color = WiseTextPrimary, fontSize = 48.sp, fontWeight = FontWeight.Black, fontFamily = InterFont, letterSpacing = (-2).sp)
    }
}

@Composable
fun LightCurrencyRow(item: CurrencyItem) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(WiseLightSurface).padding(20.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = item.flagEmoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = item.currencyCode, color = WiseTextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = InterFont)
        }
        Text(text = item.amount, color = WiseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = InterFont)
    }
}

@Composable
fun WiseRangeCalendarPicker(
    onDismiss: () -> Unit,
    onRangeSelected: (Int, Int, String) -> Unit
) {
    var startDate by remember { mutableStateOf<Int?>(null) }
    var endDate by remember { mutableStateOf<Int?>(null) }
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    var currentMonthIndex by remember { mutableIntStateOf(10) }
    var currentYear by remember { mutableIntStateOf(2025) }
    val currentMonthName = months[currentMonthIndex]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentMonthIndex == 0) { currentMonthIndex = 11; currentYear-- } else { currentMonthIndex-- }
                        startDate = null; endDate = null
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = WiseGreenDark, modifier = Modifier.size(32.dp))
                    }
                    Text(text = "$currentMonthName $currentYear", fontSize = 20.sp, fontWeight = FontWeight.Black, color = WiseGreenDark)
                    IconButton(onClick = {
                        if (currentMonthIndex == 11) { currentMonthIndex = 0; currentYear++ } else { currentMonthIndex++ }
                        startDate = null; endDate = null
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = WiseGreenDark, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                        Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = WiseTextPrimary, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val shift = (currentMonthIndex * 2) % 7
                    items(shift) { Spacer(Modifier) }
                    items(30) { index ->
                        val day = index + 1
                        val isStart = startDate == day
                        val isEnd = endDate == day
                        val isSelected = isStart || isEnd
                        val isInRange = if (startDate != null && endDate != null) day > minOf(startDate!!, endDate!!) && day < maxOf(startDate!!, endDate!!) else false

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).clickable {
                            if (startDate == null) startDate = day
                            else if (endDate == null) { if (day < startDate!!) { endDate = startDate; startDate = day } else endDate = day }
                            else { startDate = day; endDate = null }
                        }) {
                            if (isInRange || (endDate != null && isSelected && startDate != endDate)) Box(modifier = Modifier.fillMaxSize().background(WiseRangeHighlight))
                            if (isStart && endDate != null) Box(modifier = Modifier.fillMaxSize().background(WiseRangeHighlight, RoundedCornerShape(topStart = 22.dp, bottomStart = 22.dp)))
                            if (isEnd && startDate != null) Box(modifier = Modifier.fillMaxSize().background(WiseRangeHighlight, RoundedCornerShape(topEnd = 22.dp, bottomEnd = 22.dp)))

                            // Изменено на WiseLime (светло-зеленый)
                            if (isSelected) Box(modifier = Modifier.size(40.dp).background(WiseLime, CircleShape))

                            Text(text = day.toString(), color = if (isSelected) WiseGreenDark else WiseTextPrimary, fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium, fontSize = 16.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val s = startDate ?: 1
                        val e = endDate ?: s
                        onRangeSelected(s, e, currentMonthName.substring(0, 3))
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WiseLime), // Светлая кнопка
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Apply Date", color = WiseGreenDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
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

        val textPaint = Paint().apply { color = WiseTextSecondary.toArgb(); textSize = 32f; textAlign = Paint.Align.RIGHT; typeface = Typeface.DEFAULT_BOLD }
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
                    colors = listOf(WiseLime.copy(alpha = 0.3f), Color.Transparent),
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
                    color = WiseGreenDark.copy(alpha = 0.5f),
                    start = Offset(currentCursorX, topPadding),
                    end = Offset(currentCursorX, height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )

                drawCircle(WiseLightBg, 8.dp.toPx(), Offset(currentCursorX, currentY))
                drawCircle(WiseGreenDark, 5.dp.toPx(), Offset(currentCursorX, currentY))

                drawContext.canvas.nativeCanvas.apply {
                    val p = Paint().apply { color = WiseTextPrimary.toArgb(); textSize = 40f; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
                    drawText(label, currentCursorX, currentY - 60f, p)
                }
            } else {
                drawCircle(WiseTextPrimary, 6.dp.toPx(), Offset(currentCursorX, currentY))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LightStatsPreview() {
    WisePayTheme { SalesStatsScreen() }
}