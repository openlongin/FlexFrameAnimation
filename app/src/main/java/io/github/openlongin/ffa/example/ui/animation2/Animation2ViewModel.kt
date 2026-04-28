package io.github.openlongin.ffa.example.ui.animation2

import androidx.lifecycle.ViewModel
import io.github.idonans.appcontext.AppContext
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.player.PlayState
import io.github.openlongin.ffa.player.ffab.Ffab
import io.github.openlongin.ffa.player.layer.FlexFrameLayer
import io.github.openlongin.ffa.player.layer.FlexFrameLayerClipInfo
import io.github.openlongin.ffa.player.layer.FlexFrameLayerGroup
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag
import kotlinx.coroutines.flow.MutableStateFlow

class Animation2ViewModel : ViewModel() {

    private val mObjectTag by lazy { objectTag(this@Animation2ViewModel) }

    val layer: MutableStateFlow<FlexFrameLayer?> = MutableStateFlow(null)

    /**
     * 电池电量百分比，0.0~1.0
     */
    val batteryPercent: MutableStateFlow<Float> = MutableStateFlow(1f)

    init {
        val resources = AppContext.getContext().resources
        this.layer.value = FlexFrameLayerGroup.Builder().apply {
            // 使用 Group 的 PlayState 统一管理子 layer 的播放状态
            this.playState = PlayState.Builder().build()
            // 背景图
            this.children.add(FlexFrameLayer.Builder().apply {
                this.ffab = Ffab.of(R.raw.example_ffa_animation_car_flow_bg)
                this.x = 0f
                this.y = 0f
                this.width = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_width
                ).toFloat()
                this.height = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_height
                ).toFloat()
                this.rate = 1f
                this.fillStart = true
                this.fillEnd = true
            }.build())
            // 车外壳
            this.children.add(FlexFrameLayer.Builder().apply {
                this.ffab = Ffab.of(R.raw.example_ffa_animation_car_outline)
                this.x = 0f
                this.y = 0f
                this.width = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_width
                ).toFloat()
                this.height = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_height
                ).toFloat()
                this.rate = 1f
                this.fillStart = true
                this.fillEnd = true
            }.build())
            // 车轮胎
            this.children.add(FlexFrameLayer.Builder().apply {
                this.ffab = Ffab.of(R.raw.example_ffa_animation_car_wheel)
                this.x = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_wheel_x
                ).toFloat()
                this.y = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_wheel_y
                ).toFloat()
                this.width = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_wheel_width
                ).toFloat()
                this.height = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_wheel_height
                ).toFloat()
                this.rate = 60f
                this.fillStart = true
                this.fillEnd = true
            }.build())
            // 电池
            this.children.add(FlexFrameLayer.Builder().apply {
                this.ffab = Ffab.of(R.raw.example_ffa_animation_car_battery)
                this.x = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_battery_x
                ).toFloat()
                this.y = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_battery_y
                ).toFloat()
                this.width = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_battery_width
                ).toFloat()
                this.height = resources.getDimensionPixelSize(
                    R.dimen.example_ffa_animation2_render_car_battery_height
                ).toFloat()
                this.rate = 1f
                this.fillStart = true
                this.fillEnd = true
            }.build())
        }.build()

        applyBatteryPercentToLayer()
    }

    fun setBatteryPercent(percent: Float) {
        this.batteryPercent.value = percent.coerceIn(0f, 1f)
        applyBatteryPercentToLayer()
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

    private fun applyBatteryPercentToLayer() {
        val layer = (this.layer.value as? FlexFrameLayerGroup) ?: return
        val batteryPercent = this.batteryPercent.value
        val newLayer = layer.toBuilder().apply {
            // 更新电池 child 的裁剪区域
            val batteryChildIndex = 3
            val batteryChild = this.children[batteryChildIndex]
            val newBatteryChild = batteryChild.toBuilder().apply {
                this.clipInfo = FlexFrameLayerClipInfo.ClipRect(
                    x = batteryChild.x,
                    y = batteryChild.y,
                    width = batteryChild.width * batteryPercent,
                    height = batteryChild.height,
                )
            }.build()
            this.children[batteryChildIndex] = newBatteryChild
        }.build()
        this.layer.value = newLayer
    }

}
