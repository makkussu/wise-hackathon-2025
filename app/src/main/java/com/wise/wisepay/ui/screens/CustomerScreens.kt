package com.wise.wisepay.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.ui.components.SwayingCardIcon
import com.wise.wisepay.ui.components.WisePrimaryButton
import com.wise.wisepay.ui.theme.*


@Composable
fun CustomerPaymentScreen(
    amount: String,
    currency: String,
    flagResId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Wise POS", color = White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 32.dp))
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(White)
                .padding(2.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = "Flag",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$currency$amount",
            color = White,
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-3).sp,
            lineHeight = 72.sp
        )
        Text("Total to pay", color = White.copy(alpha = 0.7f), fontSize = 18.sp)

        Spacer(modifier = Modifier.height(56.dp))
        SwayingCardIcon()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Hold card here", color = Lime, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1.2f))
    }
}


@Composable
fun CustomerSuccessView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val iconResId = remember(context) { context.resources.getIdentifier("wise_success_icon", "drawable", context.packageName) }

        if (iconResId != 0) Image(painterResource(iconResId), null, Modifier.size(120.dp))
        else Icon(Icons.Default.Check, null, tint = Forest, modifier = Modifier.size(100.dp).border(4.dp, Forest, CircleShape).padding(16.dp))

        Spacer(modifier = Modifier.height(32.dp))
        Text("Approved", color = Forest, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Payment successful", color = TextGray, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun CustomerErrorScreen(onRetry: () -> Unit, onCancel: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(Modifier.fillMaxWidth()) {
            IconButton(onClick = onCancel, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, null, tint = Forest, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val context = LocalContext.current
            val errorResId = remember(context) { context.resources.getIdentifier("error", "drawable", context.packageName) }
            if (errorResId != 0) Image(painterResource(errorResId), null, Modifier.size(120.dp))
            else Icon(Icons.Default.Close, null, tint = ErrorRed, modifier = Modifier.size(100.dp).border(4.dp, ErrorRed, CircleShape).padding(16.dp))

            Spacer(Modifier.height(32.dp))
            Text("Declined", color = Forest, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Please try a different card", color = TextGray, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        WisePrimaryButton(text = "Try again", onClick = onRetry)
    }
}