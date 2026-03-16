package com.indianext.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianext.app.ui.theme.BackgroundWhite
import com.indianext.app.ui.theme.DeepCharcoal

// 1. Data Model: Preparing for server-driven UI
data class RoleConfig(val title: String, val roleId: String, val icon: ImageVector)

@Composable
fun GatewayScreen(onRoleSelected: (String) -> Unit) {
    // This list can eventually be fetched from your ViewModel/Backend
    val availableRoles = listOf(
        RoleConfig("I am a Farmer", "Farmer", Icons.Default.Agriculture),
        RoleConfig("I am a Transporter", "Transporter", Icons.Default.LocalShipping),
        RoleConfig("I am a Retailer", "Retailer", Icons.Default.Storefront),
        RoleConfig("I am a Consumer", "Consumer", Icons.Default.ShoppingBag)
    )

    val vibrantGreen = Color(0xFF10B981)
    val lightGreenBorder = Color(0xFFD1FAE5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER LOGO ---
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(16.dp),
            color = vibrantGreen
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DECENTRALIZED SUPPLY CHAIN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.width(48.dp),
            thickness = 3.dp,
            color = lightGreenBorder
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select your role to explore the ecosystem",
            fontSize = 14.sp,
            color = Color.DarkGray,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- DYNAMIC ROLE CARDS ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(availableRoles) { role ->
                RoleCard(
                    config = role,
                    vibrantGreen = vibrantGreen,
                    borderColor = lightGreenBorder,
                    onClick = { onRoleSelected(role.roleId) }
                )
            }
        }

        // --- FOOTER ---
        Text(
            text = "POWERED BY DECENTRALIZED LEDGER TECHNOLOGY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RoleCard(
    config: RoleConfig,
    vibrantGreen: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = config.title,
                tint = vibrantGreen,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = config.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "TAP TO CONTINUE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }
    }
}