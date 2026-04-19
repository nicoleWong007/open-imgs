package com.openimgs.android.ui.clean

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(title = { Text("Clean") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) { Text("Duplicates") }
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) { Text("Storage") }
            }

            if (selectedTab == 0) {
                DuplicatesContent()
            } else {
                StorageContent()
            }
        }
    }
}

@Composable
private fun DuplicatesContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.height(48.dp),
            tint = Color(0xFF34C759)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No duplicates found", style = MaterialTheme.typography.titleMedium)
        Text(
            "Your library is clean!",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StorageContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StorageCategoryRow(Icons.Default.DeleteSweep, "Screenshots", 128, "1.2 GB")
        StorageCategoryRow(Icons.Default.DeleteSweep, "Large Videos", 15, "4.8 GB")
        StorageCategoryRow(Icons.Default.DeleteSweep, "Duplicates", 0, "0 B")
        StorageCategoryRow(Icons.Default.DeleteSweep, "Similar Bursts", 0, "0 B")
        StorageCategoryRow(Icons.Default.DeleteSweep, "Blurred Photos", 0, "0 B")
    }
}

@Composable
private fun StorageCategoryRow(
    icon: ImageVector,
    name: String,
    count: Int,
    size: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    size,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6E6E73)
                )
            }
            Text(
                "$count items",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
