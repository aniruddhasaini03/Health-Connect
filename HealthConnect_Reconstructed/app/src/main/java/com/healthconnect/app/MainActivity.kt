package com.healthconnect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.healthconnect.app.navigation.HealthConnectNavGraph
import com.healthconnect.app.ui.theme.HealthConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthConnectTheme { HealthConnectNavGraph() }
        }
    }
}
