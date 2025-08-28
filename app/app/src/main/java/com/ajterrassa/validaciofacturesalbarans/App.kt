package com.ajterrassa.validaciofacturesalbarans

import android.app.Application
import androidx.work.Configuration

class App : Application(), Configuration.Provider {

    // ✅ En Kotlin és una propietat, no una funció
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()
}
