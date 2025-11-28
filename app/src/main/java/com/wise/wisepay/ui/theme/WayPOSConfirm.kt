package com.wise.wisepay.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import java.math.BigDecimal
import java.math.RoundingMode
import com.wise.wisepay.R

val WiseForest = Color(0xFF163300)
val WiseLime = Color(0xFF9FE870)
val WiseWhite = Color(0xFFFFFFFF)
val WiseLightGray = Color(0xFFF2F5F7)
val WiseGreyText = Color(0xFFA8AAAC)

val InterFont = FontFamily.SansSerif

data class Currency(val code: String, val flag: Int, val symbol: String, val rate: Double)

val currenciesList = listOf(
    Currency(code = "GBP", flag = R.drawable.flag_gbp, symbol = "£", rate = 0.79),
    Currency(code = "EUR", flag = R.drawable.flag_eu, symbol = "€", rate = 0.92),
    Currency(code = "USD", flag = R.drawable.flag_us, symbol = "$", rate = 1.0),
)

@Composable
fun WayPosConfirmScreen(onConfirm: (String, String, Int) -> Unit) {
    var inputString by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(currenciesList[1]) }
    var expanded by remember { mutableStateOf(false) }

    var isDescriptionFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val displayAmount = if (inputString.isEmpty()) "0,00" else inputString.replace('.', ',')
    val isAmountValid = inputString.isNotEmpty() && inputString.toDoubleOrNull() != 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WiseWhite)
            .windowInsetsPadding(WindowInsets.ime)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(if (isDescriptionFocused) 16.dp else 48.dp))

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = WiseForest,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    inputString = ""
                    description = ""
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
        )

        Spacer(Modifier.height(if (isDescriptionFocused) 8.dp else 24.dp))

        Text(
            text = "Way POS",
            color = WiseForest,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1.5).sp
        )

        Spacer(Modifier.weight(0.5f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                Surface(
                    onClick = {
                        expanded = true
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    color = WiseLightGray,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = selectedCurrency.flag),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(selectedCurrency.code, color = WiseForest, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, null, tint = WiseForest)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(WiseWhite)
                ) {
                    currenciesList.forEach { newCurrency ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = newCurrency.flag),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("${newCurrency.code}", color = WiseForest)
                                }
                            },
                            onClick = {
                                if (inputString.isNotEmpty()) {
                                    try {
                                        val currentVal = BigDecimal(inputString)
                                        val oldRate = BigDecimal.valueOf(selectedCurrency.rate)
                                        val newRate = BigDecimal.valueOf(newCurrency.rate)

                                        val baseVal = currentVal.divide(oldRate, 10, RoundingMode.HALF_EVEN)
                                        val newVal = baseVal.multiply(newRate).setScale(2, RoundingMode.HALF_EVEN)

                                        inputString = newVal.toPlainString()
                                    } catch (e: Exception) {
                                    }
                                }
                                selectedCurrency = newCurrency
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = displayAmount,
                color = if (inputString.isEmpty()) WiseGreyText else WiseForest,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-3).sp,
                maxLines = 1,
                lineHeight = 64.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth().padding(start = 4.dp)) {
            if (description.isEmpty()) {
                Text("Payment details", color = WiseGreyText, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            BasicTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = TextStyle(color = WiseForest, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = InterFont),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }),
                cursorBrush = SolidColor(WiseForest),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isDescriptionFocused = focusState.isFocused
                    }
            )
        }

        Spacer(Modifier.weight(1f))


        Button(
            onClick = {
                if (isAmountValid) onConfirm(displayAmount, selectedCurrency.symbol, selectedCurrency.flag)
            },
            enabled = isAmountValid,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WiseLime,
                contentColor = WiseForest,
                disabledContainerColor = WiseLightGray,
                disabledContentColor = WiseGreyText
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text("Charge", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        if (!isDescriptionFocused) {
            Spacer(Modifier.height(24.dp))
            NumberPad(
                onNumberClick = { num ->
                    keyboardController?.hide()
                    if (inputString.length < 10) inputString += num
                },
                onDeleteClick = {
                    if (inputString.isNotEmpty()) inputString = inputString.dropLast(1)
                },
                onDotClick = {
                    if (!inputString.contains('.')) inputString += if (inputString.isEmpty()) "0." else "."
                }
            )
            Spacer(Modifier.height(32.dp))
        } else {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onDotClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(",", "0", "DEL")
        )

        for (row in rows) {
            Row(modifier = Modifier.fillMaxWidth().height(72.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (key in row) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().clickable {
                            when (key) {
                                "DEL" -> onDeleteClick()
                                "," -> onDotClick()
                                else -> onNumberClick(key)
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "DEL") {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Delete", tint = WiseForest, modifier = Modifier.size(26.dp))
                        } else {
                            Text(key, fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = WiseForest)
                        }
                    }
                }
            }
        }
    }
}