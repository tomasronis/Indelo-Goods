package com.indelo.goods

import android.app.Application

class IndeloGoodsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: IndeloGoodsApplication
            private set
    }
}
