package io.github.openlongin.ffa.example

import android.app.Application
import io.github.openlongin.ffa.player.Debug
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag

class MainApplication : Application() {

    private val mObjectTag by lazy { objectTag(this@MainApplication) }

    override fun onCreate() {
        super.onCreate()
        LogUtil.d { "$mObjectTag onCreate" }

        Debug.enable = true
    }

}
