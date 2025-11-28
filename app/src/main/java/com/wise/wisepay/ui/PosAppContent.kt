package com.wise.wisepay.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import com.wise.wisepay.model.PosState
import com.wise.wisepay.ui.screens.*
import com.wise.wisepay.ui.theme.Forest
import com.wise.wisepay.ui.theme.White
import com.wise.wisepay.ui.theme.WayPosConfirmScreen
import kotlinx.coroutines.delay

@Composable
fun MainAppContent(
    currentState: PosState,
    amount: String,
    currency: String,
    onConfirmInput: (String, String) -> Unit,
    onReset: () -> Unit,
    onPaymentSuccess: () -> Unit,
    onPaymentError: () -> Unit,
    onRetry: () -> Unit
) {
    val targetRotation = when (currentState) {
        PosState.INPUT, PosState.MERCHANT_SUCCESS -> 0f
        PosState.PAYING, PosState.CUSTOMER_SUCCESS, PosState.PAYMENT_ERROR -> 180f
    }

    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "Flip"
    )

    val backgroundColor = if (rotation <= 90f) White else {
        if (currentState == PosState.PAYING) Forest else White
    }

    var tapCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            delay(400)
            if (currentState == PosState.PAYING) {
                if (tapCount >= 3) onPaymentError() else onPaymentSuccess()
            }
            tapCount = 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .graphicsLayer {
                rotationX = rotation
                cameraDistance = 32f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            if (currentState == PosState.MERCHANT_SUCCESS) {
                MerchantSuccessScreen(onReset)
            } else {
                WayPosConfirmScreen(
                    onConfirm = { amt, curr ->
                        onConfirmInput(amt, curr)
                    }
                )
            }
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationX = 180f; rotationZ = 180f }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = currentState == PosState.PAYING
                    ) { tapCount++ }
            ) {
                when (currentState) {
                    PosState.CUSTOMER_SUCCESS, PosState.MERCHANT_SUCCESS -> CustomerSuccessView()
                    PosState.PAYMENT_ERROR, PosState.INPUT -> CustomerErrorScreen(onRetry, onReset)

                    else -> CustomerPaymentScreen(amount, currency)
                }
            }
        }
    }
}