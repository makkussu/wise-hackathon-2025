package com.wise.wisepay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wise.wisepay.ui.theme.*

// Локальные цвета
val WiseTextPrimary = Color(0xFF0E0E0E)
val WiseRangeHighlight = Color(0xFFD8F2C4)

@Composable
fun MonthSelector(currentLabel: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = WiseLightGray,
        modifier = Modifier.height(56.dp).clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(text = currentLabel, color = WiseForest, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.KeyboardArrowDown, null, tint = WiseForest, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun TimeRangeSelector(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("All time", "Year", "Month", "Day")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) WiseLime else Color.Transparent)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) WiseForest else WiseGreyText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun WiseRangeCalendarPicker(onDismiss: () -> Unit, onRangeSelected: (Int, Int, String) -> Unit) {
    var startDate by remember { mutableStateOf<Int?>(null) }
    var endDate by remember { mutableStateOf<Int?>(null) }
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    var currentMonthIndex by remember { mutableIntStateOf(10) }
    var currentYear by remember { mutableIntStateOf(2025) }
    val currentMonthName = months[currentMonthIndex]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = WiseWhite),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (currentMonthIndex == 0) { currentMonthIndex = 11; currentYear-- } else { currentMonthIndex-- }; startDate = null; endDate = null }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = WiseForest, modifier = Modifier.size(32.dp)) }
                    Text(text = "$currentMonthName $currentYear", fontSize = 20.sp, fontWeight = FontWeight.Black, color = WiseForest)
                    IconButton(onClick = { if (currentMonthIndex == 11) { currentMonthIndex = 0; currentYear++ } else { currentMonthIndex++ }; startDate = null; endDate = null }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = WiseForest, modifier = Modifier.size(32.dp)) }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day -> Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = WiseTextPrimary, fontSize = 14.sp) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(260.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val shift = (currentMonthIndex * 2) % 7
                    items(shift) { Spacer(Modifier) }
                    items(30) { index ->
                        val day = index + 1
                        val isStart = startDate == day
                        val isEnd = endDate == day
                        val isSelected = isStart || isEnd
                        val isInRange = if (startDate != null && endDate != null) day > minOf(startDate!!, endDate!!) && day < maxOf(startDate!!, endDate!!) else false
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp).clickable {
                            if (startDate == null) startDate = day
                            else if (endDate == null) { if (day < startDate!!) { endDate = startDate; startDate = day } else endDate = day }
                            else { startDate = day; endDate = null }
                        }) {
                            if (isInRange || (endDate != null && isSelected && startDate != endDate)) Box(modifier = Modifier.fillMaxSize().background(WiseRangeHighlight))
                            if (isStart && endDate != null) Box(modifier = Modifier.fillMaxSize().background(WiseRangeHighlight, RoundedCornerShape(topStart = 22.dp, bottomStart = 22.dp)))
                            if (isEnd && startDate != null) Box(modifier = Modifier.fillMaxSize().background(WiseRangeHighlight, RoundedCornerShape(topEnd = 22.dp, bottomEnd = 22.dp)))
                            if (isSelected) Box(modifier = Modifier.size(40.dp).background(WiseLime, CircleShape))
                            Text(text = day.toString(), color = if (isSelected) WiseForest else WiseTextPrimary, fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium, fontSize = 16.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { val s = startDate ?: 1; val e = endDate ?: s; onRangeSelected(s, e, currentMonthName.substring(0, 3)) }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = WiseLime), shape = RoundedCornerShape(28.dp)) { Text("Apply Date", color = WiseForest, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            }
        }
    }
}