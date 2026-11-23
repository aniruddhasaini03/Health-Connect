package com.healthconnect.app

import android.app.Application
import com.google.firebase.FirebaseApp

class HealthConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
