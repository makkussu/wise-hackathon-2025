package com.wise.wisepay.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wise.wisepay.R
import com.wise.wisepay.ui.theme.GrayBg
import com.wise.wisepay.ui.theme.PaleGreen

@Composable
fun CloseButton() {
    Button(
        {},
        modifier = Modifier
            .size(36.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.inverseSurface
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close"
        )
    }
}

@Composable
fun SmallCard(
    icon: ImageVector?,
    resourceId: Int?,
    title: String,
    description: String,
    vExpand: Boolean = false,
    isInline: Boolean = false,
    cardBackgroundColour: Color?,
    modifier: Modifier = Modifier
) {
    val cardHeight: Dp = if (vExpand) 200.dp else 80.dp

    val titleWeight: FontWeight = if (!vExpand || !isInline) FontWeight.Bold else FontWeight.Normal

    val titleTextStyle: TextStyle = if (!vExpand || isInline) MaterialTheme.typography.headlineSmall else
        MaterialTheme.typography.titleLarge.copy(fontWeight = titleWeight)
    val descTextStyle: TextStyle = when {
        isInline -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        vExpand -> MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
        else -> MaterialTheme.typography.bodyMedium
    } as TextStyle

    @Composable
    fun DynamicHeader() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (vExpand) {
                Surface(
                    modifier = Modifier.size(if (vExpand) 48.dp else 36.dp),
                    shape = CircleShape,
                    color = PaleGreen
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(resourceId ?: R.drawable.ic_flag_eu),
                            contentDescription = description,
                            tint = Color.Unspecified
                        )
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = titleWeight)
                )
            } else {
                Surface(
                    modifier = if (isInline)
                            Modifier
                                .width(48.dp)
                                .height(48.dp)
                        else
                            Modifier.size(36.dp),
                    shape = CircleShape,
                    color = if (cardBackgroundColour != null) GrayBg else PaleGreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon!!,
                            contentDescription = description
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DynamicBody() {

        @Composable
        fun TitleDescCombo(
            spaceModifier: Modifier = Modifier
        ) {
            Text(
                title,
                style = titleTextStyle
            )

            Spacer(modifier = modifier)

            Text(
                text = description,
                style = descTextStyle
            )
        }

        if (vExpand) {
            Text(
                text = description,
                style = descTextStyle
            )
        } else {
            if (isInline)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        style = titleTextStyle
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = description,
                        style = descTextStyle
                    )
                }
            else
                Column {
                    TitleDescCombo()
                }
        }
    }

    @Composable
    fun PrimaryContent(
        modifier: Modifier = Modifier
    ) {
        DynamicHeader()

        Spacer(modifier = if (vExpand) modifier else Modifier.size(8.dp))

        DynamicBody()
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColour ?: GrayBg
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = if (vExpand)
                modifier
                    .width(240.dp)
                    .height(cardHeight)
            else
                modifier
                    .fillMaxWidth()
                    .height(cardHeight)
    ) {
        if (vExpand)
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                PrimaryContent(
                    modifier = Modifier.weight(1f)
                )
            }
        else
            Row(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                PrimaryContent()
            }
    }
}