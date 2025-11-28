package com.wise.wisepay

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.Euro
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wise.wisepay.model.PosState
import com.wise.wisepay.ui.MainAppContent
import com.wise.wisepay.ui.theme.GrayBg
import com.wise.wisepay.ui.theme.WiseTypography
import com.wise.wisepay.ui.theme.currenciesList
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import com.wise.wisepay.views.Revenue
import com.wise.wisepay.views.SampleHome
import com.wise.wisepay.views.Transaction

val Forest = Color(0xFF163300)
val Lime = Color(0xFF9FE870)
val White = Color(0xFFFFFFFF)
val GrayBg = Color(0xFFF2F5F7)

val InterFont = FontFamily.SansSerif

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private val appState = mutableStateOf(PosState.INPUT)

    private var transactionAmount = mutableStateOf("0,00")
    private var transactionCurrencyCode = mutableStateOf("EUR")
    private var transactionDescription = mutableStateOf("")

    private var transactionFlag = mutableIntStateOf(currenciesList[1].flag)

    override fun onTagDiscovered(tag: Tag?) {
        if (appState.value == PosState.PAYING) {
            runOnUiThread {
                vibratePhone(true)
                appState.value = PosState.CUSTOMER_SUCCESS
            }
        }
    }

    fun vibratePhone(success: Boolean) {
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
        nfcAdapter?.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    private fun simulateSignal(context: Context) {
        try {
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
        } catch (e: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val balanceTest: List<Revenue> = listOf(
            Revenue(
                id = "1",
                name = "EUR",
                totalSum = 213.45,
                currency = R.drawable.ic_flag_eu
            ),
            Revenue(
                id = "2",
                name = "USD",
                totalSum = 135.89,
                currency = R.drawable.ic_flag_us
            ),
            Revenue(
                id = "3",
                name = "NOK",
                totalSum = 253.61,
                currency = R.drawable.ic_flag_norway
            ),
            Revenue(
                id = "4",
                name = "KRW",
                totalSum = 105086.00,
                currency = R.drawable.ic_flag_south_korea
            ),
        )

        val transactionList = listOf(
            Transaction(
                id = "1",
                sum = 45.50,
                currency = Icons.Rounded.ArrowDownward,
                description = "Local berries"
            ),
            Transaction(
                id = "2",
                sum = 120.00,
                currency = Icons.Rounded.ArrowDownward,
                description = "Mobile Phone"
            ),
            Transaction(
                id = "3",
                sum = 25.75,
                currency = Icons.Rounded.ArrowDownward,
                description = "Sweatshirt"
            ),
            Transaction(
                id = "4",
                sum = 350.00,
                currency = Icons.Rounded.ArrowDownward,
                description = "8GB DDR5 RAM"
            ),
            Transaction(
                id = "5",
                sum = 89.99,
                currency = Icons.Rounded.ArrowDownward,
                description = "Online Shopping"
            ),
            Transaction(
                id = "6",
                sum = 15.00,
                currency = Icons.Rounded.ArrowDownward,
                description = "Macha Latte"
            ),
            Transaction(
                id = "7",
                sum = 200.00,
                currency = Icons.Rounded.ArrowDownward,
                description = "Concert Ticket"
            ),
            Transaction(
                id = "8",
                sum = 50.25,
                currency = Icons.Rounded.Euro,
                description = "Shampoo"
            ),
            Transaction(
                id = "9",
                sum = 12.99,
                currency = Icons.Rounded.ArrowDownward,
                description = "Headphones Sale"
            ),
            Transaction(
                id = "10",
                sum = 75.00,
                currency = Icons.Rounded.Euro,
                description = "Gaming Console"
            )
        )

        setContent {
            MaterialTheme(typography = WiseTypography) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    color = GrayBg
                ) {
                    SampleHome(
                        balanceTest,
                        transactionList
                    )
                }
            }
        }
    }
}
//                Surface(modifier = Modifier.fillMaxSize(), color = GrayBg) {
//                    val currentState = appState.value
//
//                    LaunchedEffect(currentState) {
//                        if (currentState == PosState.CUSTOMER_SUCCESS) {
//                            delay(2500)
//                            appState.value = PosState.MERCHANT_SUCCESS
//                        }
//                    }

//                    MainAppContent(
//                        currentState = currentState,
//                        amount = transactionAmount.value,
//                        currency = transactionCurrency.value,
//                        flagResId = transactionFlag.value,
//                        onConfirmInput = { amount, currency, flag ->
//                            transactionAmount.value = amount
//                            transactionCurrency.value = currency
//                            transactionFlag.value = flag
//                            appState.value = PosState.PAYING
//                        },
//
//                        onReset = { appState.value = PosState.INPUT },
//                        onPaymentSuccess = {
//                            vibratePhone(true)
//                            appState.value = PosState.CUSTOMER_SUCCESS
//                        },
//                        onPaymentError = {
//                            vibratePhone(false)
//                            appState.value = PosState.PAYMENT_ERROR
//                        },
//                        onRetry = { appState.value = PosState.PAYING }
//                    )
//                }
//            }

//            MaterialTheme(
//                typography = Typography(
//                    bodyLarge = TextStyle(fontFamily = InterFont),
//                    displayLarge = TextStyle(fontFamily = InterFont)
//                )
//            ) {
//                SimpleNfcApp(
//                    nfcTrigger = nfcTrigger,
//                    setScanningState = { active -> isReadyToScan = active },
//                    onSimulateSignal = { simulateSignal(this) }
//                )
//            }
//        }
//    }

//    fun vibratePhone(success: Boolean) {
//        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        if (Build.VERSION.SDK_INT >= 26) {
//            val amplitude = if (success) VibrationEffect.DEFAULT_AMPLITUDE else 255
//            val timing = if (success) 150L else 500L
//            vibrator.vibrate(VibrationEffect.createOneShot(timing, amplitude))
//        } else {
//            vibrator.vibrate(500)
//        }
//    }
//
//    fun onResume() {
//        super.onResume()
//        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
//    }
//
//    fun onPause() {
//        super.onPause()
//        nfcAdapter?.disableReaderMode(this)
//    }