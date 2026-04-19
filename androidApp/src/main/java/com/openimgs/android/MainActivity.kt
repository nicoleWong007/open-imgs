package com.openimgs.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.openimgs.android.ui.navigation.MainNavigation
import com.openimgs.android.ui.onboarding.OnboardingScreen
import com.openimgs.android.ui.theme.OpenImgsTheme

class MainActivity : ComponentActivity() {

    var hasPermission by mutableStateOf(false)
        private set

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions.values.all { it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkPermission()
        val neededPermissions = getRequiredPermissions()
        hasPermission = neededPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        setContent {
            OpenImgsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if (hasPermission) {
                        MainNavigation()
                    } else {
                        OnboardingScreen(
                            onRequestPermission = { checkPermission() }
                        )
                    }
                }
            }
        }
    }

    private fun checkPermission() {
        permissionLauncher.launch(getRequiredPermissions())
    }

    private fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
