package io.github.openlongin.ffa.example.ui.animation

import androidx.lifecycle.ViewModel
import io.github.idonans.appcontext.AppContext
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.player.PlayState
import io.github.openlongin.ffa.player.ffab.Ffab
import io.github.openlongin.ffa.player.layer.FlexFrameLayer
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag
import kotlinx.coroutines.flow.MutableStateFlow

class AnimationViewModel : ViewModel() {

    private val mObjectTag by lazy { objectTag(this@AnimationViewModel) }

    val layer: MutableStateFlow<FlexFrameLayer?> = MutableStateFlow(null)

    init {
        val resources = AppContext.getContext().resources
        this.layer.value = FlexFrameLayer.Builder().apply {
            this.ffab = Ffab.of(R.raw.example_ffa_animation)
            this.x = 0f
            this.y = 0f
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation_render_width
            ).toFloat()
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation_render_height
            ).toFloat()
            this.rate = 30f
            this.playState = PlayState.Builder().build()
            this.fillStart = true
            this.fillEnd = true
        }.build()
    }

    fun startAnimation() {
        val layer = this.layer.value
        val playState = layer?.playState
        if (playState == null) {
            LogUtil.e { "$mObjectTag start playState is null" }
            return
        }
        val newPlayState = playState.toBuilder().apply {
            this.start(true)
        }.build()
        val newLayer = layer.toBuilder().apply {
            this.playState = newPlayState
        }.build()
        this.layer.value = newLayer
    }

    fun stopAnimation() {
        val layer = this.layer.value
        val playState = layer?.playState
        if (playState == null) {
            LogUtil.e { "$mObjectTag stop playState is null" }
            return
        }
        val newPlayState = playState.toBuilder().apply {
            this.stop(true)
        }.build()
        val newLayer = layer.toBuilder().apply {
            this.playState = newPlayState
        }.build()
        this.layer.value = newLayer
    }

}
