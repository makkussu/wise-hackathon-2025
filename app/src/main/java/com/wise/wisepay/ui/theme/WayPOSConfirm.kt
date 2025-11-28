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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wise.wisepay.util.CurrencyUtils
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

    val maxIntegerDigits = 7
    val maxDecimalDigits = 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WiseWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Box(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = WiseForest,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            inputString = ""
                            description = ""
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                )
            }

            Spacer(Modifier.height(24.dp))
            Text("Way POS", color = WiseForest, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp)
            Spacer(Modifier.height(64.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    Surface(
                        onClick = { expanded = true; focusManager.clearFocus(); keyboardController?.hide() },
                        color = WiseLightGray,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(selectedCurrency.flag, fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(selectedCurrency.code, color = WiseForest, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, null, tint = WiseForest)
                        }
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
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                AutoResizingText(
                    text = displayAmount,
                    color = if (inputString.isEmpty()) WiseGreyText else WiseForest,
                    maxFontSize = 64.sp,
                    minFontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            Box(modifier = Modifier.fillMaxWidth().padding(start = 4.dp)) {
                if (description.isEmpty()) {
                    Text("Payment details", color = WiseGreyText, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    textStyle = TextStyle(color = WiseForest, fontSize = 18.sp, fontWeight = FontWeight.Medium, fontFamily = InterFont),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    cursorBrush = SolidColor(WiseForest),
                    modifier = Modifier.fillMaxWidth().onFocusChanged { isDescriptionFocused = it.isFocused }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Button(
                onClick = {
                    if (isAmountValid) onConfirm(displayAmount, selectedCurrency.code, description)
                },
                enabled = isAmountValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WiseLime, contentColor = WiseForest, disabledContainerColor = WiseLightGray, disabledContentColor = WiseGreyText),
                shape = RoundedCornerShape(50)
            ) {
                Text("Charge", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                if (!isDescriptionFocused) {
                    NumberPad(
                        onNumberClick = { num ->
                            keyboardController?.hide()
                            val parts = inputString.split('.')
                            val integerPart = parts.getOrNull(0) ?: ""
                            val decimalPart = if (parts.size > 1) parts[1] else null

                            if (decimalPart != null) {
                                if (decimalPart.length < maxDecimalDigits) inputString += num
                            } else {
                                if (integerPart.length < maxIntegerDigits) inputString += num
                            }
                        },
                        onDeleteClick = { if (inputString.isNotEmpty()) inputString = inputString.dropLast(1) },
                        onDotClick = {
                            if (!inputString.contains('.')) {
                                if (inputString.length < 8) inputString += if (inputString.isEmpty()) "0." else "."
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NumberPad(onNumberClick: (String) -> Unit, onDeleteClick: () -> Unit, onDotClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(",", "0", "DEL")
        )
        for (row in rows) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (key in row) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { when (key) { "DEL" -> onDeleteClick(); "," -> onDotClick(); else -> onNumberClick(key) } }, contentAlignment = Alignment.Center) {
                        if (key == "DEL") Icon(Icons.AutoMirrored.Filled.ArrowBack, "Delete", tint = WiseForest, modifier = Modifier.size(26.dp))
                        else Text(key, fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = WiseForest)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    maxFontSize: TextUnit = 64.sp,
    minFontSize: TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily? = null
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd
    ) {
        val density = LocalDensity.current
        val fontFamilyResolver = LocalFontFamilyResolver.current

        val fontSize = remember(text, maxWidth, maxHeight) {
            var targetSize = maxFontSize.value
            val availableWidth = constraints.maxWidth * 0.95f

            val style = TextStyle(
                fontWeight = fontWeight,
                letterSpacing = letterSpacing,
                fontFamily = fontFamily
            )

            val paragraph = Paragraph(
                text = text,
                style = style.copy(fontSize = targetSize.sp),
                constraints = Constraints(maxWidth = Int.MAX_VALUE),
                density = density,
                fontFamilyResolver = fontFamilyResolver
            )

            val textWidth = paragraph.maxIntrinsicWidth
            if (textWidth > availableWidth) {
                val scaleFactor = availableWidth / textWidth
                targetSize *= scaleFactor
            }

            targetSize.coerceAtLeast(minFontSize.value).sp
        }

        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            fontFamily = fontFamily,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.End
        )
    }
}