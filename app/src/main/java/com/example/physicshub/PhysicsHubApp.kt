package com.example.physicshub

import android.app.Application
import com.google.firebase.FirebaseApp

class PhysicsHubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
