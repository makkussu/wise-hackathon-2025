package com.wise.wisepay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.ui.components.WisePrimaryButton
import com.wise.wisepay.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    onBackClick: () -> Unit = {},
    onRefundClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Forest //
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White //
                )
            )
        },
        containerColor = White,
        bottomBar = {
            // Кнопки действий для мерчанта
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {

                WisePrimaryButton(
                    text = "Download receipt",
                    onClick = onRefundClick,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Хедер: Сумма положительная, статус "Received"
            TransactionHeaderAmount(
                amount = "+15.00",
                currency = "EUR",
                status = "Received • Today at 10:23",
                payerInitials = "AF"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Таймлайн получения
            TransactionTimelineReceived()

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider(color = GrayBg, thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Детали: "From" вместо "To"
            TransactionDetailRow(label = "From", value = "Alice Freeman")
            TransactionDetailRow(label = "Payment ref", value = "Latte & Muffin ☕")
            TransactionDetailRow(label = "Transaction #", value = "#882910293")

            // Блок комиссий
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Wise fees",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
                )
                Text(
                    text = "-0.12 EUR", // Комиссия вычитается
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Forest,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            // Итоговая сумма зачисления
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Net amount",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Forest,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "14.88 EUR",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Forest,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TransactionHeaderAmount(
    amount: String,
    currency: String,
    status: String,
    payerInitials: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Кружок с инициалами плательщика
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(GrayBg), //
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = payerInitials,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Сумма
        Text(
            text = "$amount $currency",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Forest,
                letterSpacing = (-1).sp
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Статус
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextGray, //
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun TransactionTimelineReceived() {
    Column(modifier = Modifier.fillMaxWidth()) {
        TimelineItem(
            title = "Alice Freeman paid",
            subtitle = "Via Contactless •• 1234",
            isCompleted = true,
            isFirst = true
        )
        TimelineItem(
            title = "Payment processed",
            subtitle = "Wise checked the transaction",
            isCompleted = true
        )
        TimelineItem(
            title = "Added to your account",
            subtitle = "14.88 EUR available to spend",
            isCompleted = true,
            isLast = true
        )
    }
}

@Composable
fun TimelineItem(
    title: String,
    subtitle: String? = null,
    isCompleted: Boolean = false,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Линия и точка
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            // Верхняя линия
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (isCompleted) Lime else GrayBg) // Используем Lime как акцент
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Точка
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Lime else GrayBg)
            )

            // Нижняя линия
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (isCompleted) Lime else GrayBg)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Текст справа
        Column(
            modifier = Modifier
                .padding(vertical = if (isFirst || isLast) 0.dp else 16.dp)
                .padding(bottom = if (isLast) 0.dp else 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Forest
                )
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextGray
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextGray)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Forest,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionDetailsPreview() {
    MaterialTheme {
        TransactionDetailsScreen()
    }
}