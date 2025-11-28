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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.wise.wisepay.model.PosState
import com.wise.wisepay.ui.MainAppContent
import com.wise.wisepay.ui.theme.GrayBg
import com.wise.wisepay.ui.theme.WiseTypography
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private val appState = mutableStateOf(PosState.INPUT)

    private var transactionAmount = mutableStateOf("0,00")
    private var transactionCurrencyCode = mutableStateOf("EUR")
    private var transactionDescription = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme(typography = WiseTypography) {
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
                        amount = transactionAmount.value,
                        currencyCode = transactionCurrencyCode.value,
                        description = transactionDescription.value,

                        onConfirmInput = { amt, currCode, desc ->
                            transactionAmount.value = amt
                            transactionCurrencyCode.value = currCode
                            transactionDescription.value = desc
                            appState.value = PosState.PAYING
                        },

                        onReset = { appState.value = PosState.INPUT },
                        onPaymentSuccess = {
                            vibratePhone(true)
                            appState.value = PosState.CUSTOMER_SUCCESS
                        },
                        onPaymentError = {
                            vibratePhone(false)
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
        if (appState.value == PosState.PAYING) {
            runOnUiThread {
                vibratePhone(true)
                appState.value = PosState.CUSTOMER_SUCCESS
            }
        }
    }
}