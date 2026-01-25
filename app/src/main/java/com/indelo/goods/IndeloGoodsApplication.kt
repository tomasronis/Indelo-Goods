package com.indelo.goods

import android.app.Application
import com.indelo.goods.data.supabase.SupabaseClientProvider

class IndeloGoodsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Supabase client and load saved session
        SupabaseClientProvider.initialize(this)
    }

    companion object {
        lateinit var instance: IndeloGoodsApplication
            private set
    }
}
