package com.wise.wisepay.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.ui.components.WiseOutlineButton
import com.wise.wisepay.ui.components.WisePrimaryButton
import com.wise.wisepay.ui.theme.*

@Composable
fun MerchantInputScreen(onPayClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(Icons.Default.Close, null, tint = Forest)
            Box(Modifier.clip(RoundedCornerShape(12.dp)).background(PaleGreen).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text("EUR", color = Forest, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("25.00", fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Forest, letterSpacing = (-2).sp)
        Text("Lunch Combo", fontSize = 18.sp, color = TextGray)
        Spacer(modifier = Modifier.weight(1f))

        WisePrimaryButton(text = "Charge €25.00", onClick = onPayClick)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun MerchantSuccessScreen(onReset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val imageResId = remember(context) { context.resources.getIdentifier("coin_pile_up", "drawable", context.packageName) }

        if (imageResId != 0) Image(painterResource(imageResId), null, Modifier.size(120.dp))
        else Box(Modifier.size(80.dp).background(PaleGreen, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.ArrowDownward, null, tint = Forest, modifier = Modifier.size(40.dp)) }

        Spacer(Modifier.height(24.dp))
        Text("Payment Received", fontSize = 24.sp, color = Forest, fontWeight = FontWeight.Bold)
        Text("Transaction #8492 completes", color = TextGray)
        Spacer(Modifier.height(64.dp))
        WiseOutlineButton(text = "New Sale", onClick = onReset)
    }
}