package com.openimgs.android.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            PremiumSection()
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            UsageSection()
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            AboutSection()
        }
    }
}

@Composable
private fun PremiumSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Upgrade to Premium",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Unlimited albums, search, and cleanup",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UsageSection() {
    Text(
        "Usage",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
    ListItem(
        headlineContent = { Text("Smart Searches") },
        trailingContent = { Text("0/10", color = Color(0xFF6E6E73)) }
    )
    ListItem(
        headlineContent = { Text("Duplicate Deletes") },
        trailingContent = { Text("0/5", color = Color(0xFF6E6E73)) }
    )
}

@Composable
private fun AboutSection() {
    Text(
        "About",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
    ListItem(
        headlineContent = { Text("Version") },
        trailingContent = { Text("1.0.0", color = Color(0xFF6E6E73)) }
    )
    ListItem(
        headlineContent = { Text("Privacy Policy") },
        trailingContent = {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
    ListItem(
        headlineContent = { Text("Terms of Service") },
        trailingContent = {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
    Spacer(modifier = Modifier.height(32.dp))
}
