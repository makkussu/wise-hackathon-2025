package com.wise.wisepay.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
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