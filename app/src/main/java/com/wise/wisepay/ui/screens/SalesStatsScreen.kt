package com.wise.wisepay.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.R
import com.wise.wisepay.ui.components.* // Здесь живут InteractiveWiseGraph и WisePrimaryButton
import com.wise.wisepay.ui.theme.* // Здесь живут цвета Forest, White, GrayBg и т.д.

// Локальный черный цвет
val WiseTextBlack = Color(0xFF0E0E0E)

data class CurrencyItem(
    val amount: String,
    val currencyCode: String,
    val name: String,
    @DrawableRes val flagRes: Int
)

@Composable
fun SalesStatsScreen() {
    var showCalendar by remember { mutableStateOf(false) }

    // Текст на кнопке: "Select Date" (изначально) или "Date" (если выбран фильтр)
    var selectedDateText by remember { mutableStateOf("Select Date") }

    var isStatsVisible by remember { mutableStateOf(false) }
    var graphAnimationKey by remember { mutableIntStateOf(0) }

    // Фильтр: "Month" по умолчанию. Если "", значит выбрана конкретная дата.
    var selectedTimeFilter by remember { mutableStateOf("Month") }

    val graphDataMap = remember {
        mapOf(
            "All time" to listOf(0.2f, 0.3f, 0.5f, 0.4f, 0.6f, 0.8f, 0.7f, 0.9f, 0.85f, 1.0f),
            "Year" to listOf(0.5f, 0.6f, 0.4f, 0.7f, 0.5f, 0.6f, 0.8f, 0.7f, 0.9f, 0.8f),
            "Month" to listOf(0.4f, 0.6f, 0.3f, 0.7f, 0.5f, 0.85f, 0.6f, 0.95f, 0.7f, 0.8f),
            "Day" to listOf(0.3f, 0.4f, 0.35f, 0.5f, 0.45f, 0.55f, 0.5f, 0.6f, 0.55f, 0.7f),
            "" to listOf(0.4f, 0.5f, 0.4f, 0.6f, 0.5f, 0.7f, 0.6f, 0.8f, 0.7f, 0.6f)
        )
    }

    val currentGraphData = graphDataMap[selectedTimeFilter] ?: graphDataMap[""] ?: listOf()

    val currencyList = remember {
        listOf(
            CurrencyItem("1,250.00", "EUR", "Euro", R.drawable.flag_eu),
            CurrencyItem("890.50", "USD", "US Dollar", R.drawable.flag_us),
            CurrencyItem("4,320.00", "PLN", "Polish Zloty", R.drawable.pl_poland)
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

                // Сбрасываем фильтры времени, так как выбрана точная дата
                selectedTimeFilter = ""
                showCalendar = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        if (!isStatsVisible) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- ДОБАВЛЕННАЯ КАРТИНКА ---
                // Убедитесь, что файл calendar_large.png есть в res/drawable
                Image(
                    painter = painterResource(id = R.drawable.calendar_large), // (Note: using user's requested name)
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Check your sales", // Оставили как было
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = WiseTextBlack,
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
                containerColor = White,
                bottomBar = {
                    Box(modifier = Modifier.padding(16.dp)) {
                        WisePrimaryButton(
                            text = "Back",
                            onClick = {
                                isStatsVisible = false
                                selectedDateText = "Select Date"
                                selectedTimeFilter = "Month"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 24.dp,
                        bottom = paddingValues.calculateBottomPadding() + 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                ) {
                    item {
                        Text(
                            text = "Way POS",
                            color = WiseTextBlack,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MonthSelector(
                                currentLabel = selectedDateText,
                                onClick = { showCalendar = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    item {
                        TotalSalesBlock()
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GrayBg),
                            shape = RoundedCornerShape(32.dp),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier = Modifier.fillMaxWidth().height(360.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    InteractiveWiseGraph(
                                        data = currentGraphData,
                                        minY = 1.15f,
                                        maxY = 1.25f,
                                        lineColor = Forest,
                                        animationKey = graphAnimationKey
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                                TimeRangeSelector(
                                    selectedFilter = selectedTimeFilter,
                                    onFilterSelected = { filter ->
                                        selectedTimeFilter = filter
                                        selectedDateText = "Date"
                                        graphAnimationKey++
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    item {
                        Text(
                            text = "Breakdown",
                            color = WiseTextBlack,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }

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
fun TotalSalesBlock() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Total sales", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = "€ 2,540.50", color = WiseTextBlack, fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
    }
}

@Composable
fun LightCurrencyRow(item: CurrencyItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GrayBg)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = item.flagRes),
                contentDescription = item.currencyCode,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.currencyCode,
                color = WiseTextBlack,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
        Text(
            text = item.amount,
            color = WiseTextBlack,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}