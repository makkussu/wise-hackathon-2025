package com.wise.wisepay

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.ui.theme.WayPosConfirmScreen

val Forest = Color(0xFF163300)
val Lime = Color(0xFF9FE870)
val White = Color(0xFFFFFFFF)
val GrayBg = Color(0xFFF2F5F7)

val InterFont = FontFamily.SansSerif

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private val appState = mutableStateOf(PosState.INPUT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme (
                typography = WiseTypography
            ){
                Surface(modifier = Modifier.fillMaxSize(), color = GrayBg) {
                    val currentState = appState.value

                    LaunchedEffect(currentState) {
                        if (currentState == PosState.CUSTOMER_SUCCESS) {
                            delay(2500)
                            appState.value = PosState.MERCHANT_SUCCESS
                        }
                    }

                    MainAppContent(
                        currentState = currentState,
                        onStartPayment = { appState.value = PosState.PAYING },
                        onReset = { appState.value = PosState.INPUT },
                        onPaymentSuccess = {
                            vibratePhone(success = true)
                            appState.value = PosState.CUSTOMER_SUCCESS
                        },
                        onPaymentError = {
                            vibratePhone(success = false)
                            appState.value = PosState.PAYMENT_ERROR
                        },
                        onRetry = { appState.value = PosState.PAYING }
                    )
                }
            }
        }
    }

    private fun vibratePhone(success: Boolean) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            val amplitude = if (success) VibrationEffect.DEFAULT_AMPLITUDE else 255
            val timing = if (success) 150L else 500L
            vibrator.vibrate(VibrationEffect.createOneShot(timing, amplitude))
        } else {
            vibrator.vibrate(500)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        if (isReadyToScan) {
            isReadyToScan = false
            nfcTrigger.value = !nfcTrigger.value
            simulateSignal(this)
        }
    }
}

enum class AppStage { IDLE, SCANNING, SUCCESS }

@Composable
fun SimpleNfcApp(
    nfcTrigger: MutableState<Boolean>,
    setScanningState: (Boolean) -> Unit,
    onSimulateSignal: () -> Unit
) {
    var stage by remember { mutableStateOf(AppStage.IDLE) }
    var amountToPay by remember { mutableStateOf("") }

    LaunchedEffect(stage) {
        setScanningState(stage == AppStage.SCANNING)
    }

    LaunchedEffect(nfcTrigger.value) {
        if (stage == AppStage.SCANNING) {
            stage = AppStage.SUCCESS
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (stage == AppStage.IDLE) White else Forest)
    ) {
        when (stage) {
            AppStage.IDLE -> WayPosConfirmScreen(
                onConfirm = { amount, currency ->
                    amountToPay = "$amount $currency"
                    stage = AppStage.SCANNING
                }
            )
            AppStage.SCANNING -> ScanningScreen(
                amountText = amountToPay,
                onCancel = { stage = AppStage.IDLE },
                onSimulateTap = {
                    onSimulateSignal()
                    stage = AppStage.SUCCESS
                }
            )
            AppStage.SUCCESS -> SuccessScreen(
                amountText = amountToPay,
                onDone = { stage = AppStage.IDLE }
            )
        }
    }
}

@Composable
fun ScanningScreen(amountText: String, onCancel: () -> Unit, onSimulateTap: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .clickable { onSimulateTap() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp)) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Cancel",
                tint = White,
                modifier = Modifier.size(32.dp).clickable { onCancel() }
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PulsingIcon()
            Spacer(Modifier.height(48.dp))
            Text("Pay $amountText", color = White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Hold card to back of device", color = Lime, fontSize = 16.sp, modifier = Modifier.padding(top = 16.dp))
        }

        Text("Wise Terminal", color = White.copy(0.3f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 48.dp))
    }
}

@Composable
fun SuccessScreen(amountText: String, onDone: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Forest,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(32.dp))
        Text("Success!", color = Forest, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(amountText, color = Forest, fontSize = 24.sp, modifier = Modifier.padding(top = 8.dp))

        Spacer(Modifier.height(80.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Done", color = Lime, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
fun PulsingIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart), label = "S"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart), label = "A"
    )
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(140.dp)) {
            drawCircle(color = Lime, radius = size.minDimension / 2 * scale, alpha = alpha)
        }
        Box(
            Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Lime),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Share, null, tint = Forest, modifier = Modifier.size(40.dp))
        }
    }
}
