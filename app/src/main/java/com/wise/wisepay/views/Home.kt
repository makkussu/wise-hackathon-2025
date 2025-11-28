package com.wise.wisepay.views

import android.R.attr.height
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Euro
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wise.wisepay.Forest
import com.wise.wisepay.Lime
import com.wise.wisepay.R
import com.wise.wisepay.components.SmallCard
import com.wise.wisepay.ui.theme.Black
import com.wise.wisepay.ui.theme.GrayBg
import com.wise.wisepay.ui.theme.PaleGreen
import com.wise.wisepay.ui.theme.TextGray
import com.wise.wisepay.ui.theme.White

data class Transaction(
    val id: String,
    val sum: Double,
    val currency: ImageVector,
    val description: String
)

data class Revenue(
    val id: String,
    val name: String,
    val totalSum: Double,
    val currency: Int
)

@Composable
fun HomeRevenueRow(
    balance: List<Revenue>
) {
    Text(
        "Balance",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(balance) { item ->
            SmallCard(
                icon = null,
                resourceId = item.currency,
                title = item.name,
                description = item.totalSum.toString(),
                vExpand = true,
                cardBackgroundColour = GrayBg
            )
        }
    }
}

@Composable
fun RoundedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Lime,
            contentColor = Black
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun SampleHome(
    balance: List<Revenue>,
    transactions: List<Transaction>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
        ) {
            item {
                Row {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp),
                        shape = CircleShape,
                        color = PaleGreen
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            item {
                HomeRevenueRow(balance)
            }

            item {
                Spacer(modifier = Modifier.size(8.dp))
            }

            item {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(transactions) { item ->
                SmallCard(
                    icon = Icons.Rounded.ArrowDownward,
                    resourceId = null,
                    title = item.description,
                    description = item.sum.toString(),
                    vExpand = false,
                    isInline = true,
                    cardBackgroundColour = White
                )
            }
        }

        Divider()

        RoundedButton(
            text = "Ask for Payment",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

//@Preview
//@Composable
//fun SampleScreenPreview() {
//    MaterialTheme {
//        Surface(
//            color = White
//        ) {
//            SampleHome(
//                balance = balanceTest,
//                transactions = transactionList
//            )
//        }
//    }
//}