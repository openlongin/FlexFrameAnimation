package io.github.openlongin.ffa.example.ui.animation3

import androidx.lifecycle.ViewModel
import io.github.idonans.appcontext.AppContext
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.player.PlayState
import io.github.openlongin.ffa.player.ffab.Ffab
import io.github.openlongin.ffa.player.layer.FlexFrameLayer
import io.github.openlongin.ffa.player.layer.FlexFrameLayerGroup
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag
import kotlinx.coroutines.flow.MutableStateFlow

class Animation3ViewModel : ViewModel() {

    private val mObjectTag by lazy { objectTag(this@Animation3ViewModel) }

    // 入场动画
    // 入场动画由三部分组成，总帧数 150 = out 60 + in 30 + loop 60, 其中 loop 无限循环
    // 入场动画的总时长 = 5s + 2s无限循环
    // 帧率 30
    private var mLayerAnimationIn: FlexFrameLayerGroup? = null

    // 出场动画
    // 出场动画由三部分组成，总帧数 150 = in 30 + loop 60 + out 60，其中 loop 只播放一次
    // 出场动画三部分的总时长 = 5s
    // 帧率 30
    private var mLayerAnimationOut: FlexFrameLayerGroup? = null

    // 当前正在执行的动画
    val layer: MutableStateFlow<FlexFrameLayer?> = MutableStateFlow(null)

    /**
     * 计算此时切换到进场动画的最佳 offset。如果返回 null 表示当前正在播放进场动画。
     */
    private fun findBestAnimationInOffset(): Long? {
        val currentLayer = this.layer.value
        if (currentLayer != null && currentLayer == this.mLayerAnimationIn) {
            // 当前正在播放进场动画
            return null
        }

        if (currentLayer != null && currentLayer == this.mLayerAnimationOut) {
            // 当前正在播放出场动画
            val uptimeRunning = (currentLayer.playState?.uptimeRunning() ?: 0L).coerceAtLeast(0L)

            // 播放一帧需要的时间
            val durationPerFrame = 1000f / 30

            // 计算出场动画的此时刻 与 入场动画的哪一个时刻的内容是相同的
            val virtualIndex = (uptimeRunning / durationPerFrame).toInt()

            // 计算 virtualIndex 这一帧实际对应的是出场动画的哪一帧
            // 出场动画由三部分组成，总帧数 150 = in 30 + loop 60 + out 60，其中 loop 只播放一次
            var outFrameIndex = 149
            if (virtualIndex <= 149) {
                outFrameIndex = virtualIndex % 150
            }

            // 计算出场动画的 outFrameIndex 对应入场动画的哪一帧
            // 入场动画由三部分组成，总帧数 150 = out 60 + in 30 + loop 60, 其中 loop 无限循环
            val matchInFrameIndex = (outFrameIndex + 60) % 150
            val bestOffset = matchInFrameIndex * durationPerFrame

            LogUtil.d {
                "findBestAnimationInOffset uptimeRunning:$uptimeRunning, durationPerFrame:$durationPerFrame" +
                        //
                        " virtualIndex:$virtualIndex, outFrameIndex:$outFrameIndex," +
                        //
                        " matchInFrameIndex:$matchInFrameIndex, bestOffset:$bestOffset"
            }
            return bestOffset.toLong()
        }

        return 0L
    }

    /**
     * 创建进场动画
     */
    private fun buildAnimationIn(uptimeRunningOffset: Long): FlexFrameLayerGroup {
        val resources = AppContext.getContext().resources

        // 出场动画：帧率 30, 一共有 60 张图片
        val layerOut = FlexFrameLayer.Builder().apply {
            // 动画资源文件，raw 类型，要求白天黑夜使用同一个 id
            this.ffab = Ffab.of(R.raw.example_ffa_anim3_out)
            // 动画显示位置
            this.x = 0f
            // 动画显示位置
            this.y = 0f
            // 动画显示位置
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_width
            ).toFloat()
            // 动画显示位置
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_height
            ).toFloat()
            // 动画帧率 30fps
            this.rate = 30f
            // 动画执行时长 1999ms
            this.duration = 1999
            // 在动画开始执行前是否渲染第一帧
            this.fillStart = false
            // 在动画结束后是否渲染最后一帧
            this.fillEnd = false
        }.build()

        // 入场动画：帧率 30, 一共有 30 张图片
        val layerIn = FlexFrameLayer.Builder().apply {
            // 动画资源文件，raw 类型，要求白天黑夜使用同一个 id
            this.ffab = Ffab.of(R.raw.example_ffa_anim3_in)
            // 动画显示位置
            this.x = 0f
            // 动画显示位置
            this.y = 0f
            // 动画显示位置
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_width
            ).toFloat()
            // 动画显示位置
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_height
            ).toFloat()
            // 动画帧率 30fps
            this.rate = 30f
            // 动画执行时长 999ms
            this.duration = 999
            // 在动画开始执行前是否渲染第一帧
            this.fillStart = false
            // 在动画结束后是否渲染最后一帧
            this.fillEnd = false
            // 配置动画在 startDelay 之后才开始绘制第一帧，衔接在 layerOut 之后
            this.startDelay = layerOut.startDelay + layerOut.duration
        }.build()

        // 循环动画：帧率 30, 一共有 60 张图片
        val layerLoop = FlexFrameLayer.Builder().apply {
            this.ffab = Ffab.of(R.raw.example_ffa_anim3_loop)
            // 动画显示位置
            this.x = 0f
            // 动画显示位置
            this.y = 0f
            // 动画显示位置
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_width
            ).toFloat()
            // 动画显示位置
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_height
            ).toFloat()
            // 动画帧率 30fps
            this.rate = 30f
            // 动画执行时长 -1, 表示无限循环
            this.duration = -1
            // 在动画开始执行前是否渲染第一帧
            this.fillStart = false
            // 在动画结束后是否渲染最后一帧
            this.fillEnd = false
            // 配置动画在 startDelay 之后才开始绘制第一帧，衔接在 layerIn 之后
            this.startDelay = layerIn.startDelay + layerIn.duration
        }.build()

        // 组成一个动画组
        val layerGroup = FlexFrameLayerGroup.Builder().apply {
            this.children.add(layerOut)
            this.children.add(layerIn)
            this.children.add(layerLoop)
            this.playState = PlayState.Builder().apply {
                this.setUptimeRunningOffset(uptimeRunningOffset)
                this.start()
            }.build()
        }.build()
        return layerGroup
    }

    fun animationIn() {
        val bestAnimationInOffset = findBestAnimationInOffset()
        LogUtil.i { "animationIn bestAnimationInOffset:$bestAnimationInOffset" }
        if (bestAnimationInOffset != null) {
            this.mLayerAnimationIn = buildAnimationIn(bestAnimationInOffset)
            this.layer.value = this.mLayerAnimationIn
        }
    }


    /**
     * 计算此时切换到出场动画的最佳 offset。如果返回 null 表示当前正在播放出场动画。
     */
    private fun findBestAnimationOutOffset(): Long? {
        val currentLayer = this.layer.value
        if (currentLayer != null && currentLayer == this.mLayerAnimationOut) {
            // 当前正在播放出场动画
            return null
        }

        if (currentLayer != null && currentLayer == this.mLayerAnimationIn) {
            // 当前正在播放进场动画
            val uptimeRunning = (currentLayer.playState?.uptimeRunning() ?: 0L).coerceAtLeast(0L)

            // 播放一帧需要的时间
            val durationPerFrame = 1000f / 30

            // 计算进场动画的此时刻 与 出场动画的哪一个时刻的内容是相同的
            val virtualIndex = (uptimeRunning / durationPerFrame).toInt()

            // 计算 virtualIndex 这一帧实际对应的是进场动画的哪一帧
            // 入场动画由三部分组成，总帧数 150 = out 60 + in 30 + loop 60, 其中 loop 无限循环
            var inFrameIndex = 149
            if (virtualIndex <= 149) {
                inFrameIndex = virtualIndex % 150
            }

            // 计算进场动画的 outFrameIndex 对应出场动画的哪一帧
            // 入场动画由三部分组成，总帧数 150 = out 60 + in 30 + loop 60, 其中 loop 无限循环
            val matchOutFrameIndex = (inFrameIndex + 90) % 150
            val bestOffset = matchOutFrameIndex * durationPerFrame

            LogUtil.d {
                "findBestAnimationOutOffset uptimeRunning:$uptimeRunning, durationPerFrame:$durationPerFrame" +
                        //
                        " virtualIndex:$virtualIndex, inFrameIndex:$inFrameIndex," +
                        //
                        " matchOutFrameIndex:$matchOutFrameIndex, bestOffset:$bestOffset"
            }
            return bestOffset.toLong()
        }

        return 0L
    }

    /**
     * 创建出场动画, 该动画组从第一帧播放到最后一帧的总时长是：
     */
    private fun buildAnimationOut(uptimeRunningOffset: Long): FlexFrameLayerGroup {
        val resources = AppContext.getContext().resources

        // 入场动画：帧率 30, 一共有 30 张图片
        val layerIn = FlexFrameLayer.Builder().apply {
            // 动画资源文件，raw 类型，要求白天黑夜使用同一个 id
            this.ffab = Ffab.of(R.raw.example_ffa_anim3_in)
            // 动画显示位置
            this.x = 0f
            // 动画显示位置
            this.y = 0f
            // 动画显示位置
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_width
            ).toFloat()
            // 动画显示位置
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_height
            ).toFloat()
            // 动画帧率 30fps
            this.rate = 30f
            // 动画执行时长 999ms
            this.duration = 999
            // 在动画开始执行前是否渲染第一帧
            this.fillStart = false
            // 在动画结束后是否渲染最后一帧
            this.fillEnd = false
        }.build()

        // 循环动画：帧率 30, 一共有 60 张图片
        val layerLoop = FlexFrameLayer.Builder().apply {
            this.ffab = Ffab.of(R.raw.example_ffa_anim3_loop)
            // 动画显示位置
            this.x = 0f
            // 动画显示位置
            this.y = 0f
            // 动画显示位置
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_width
            ).toFloat()
            // 动画显示位置
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_height
            ).toFloat()
            // 动画帧率 30fps
            this.rate = 30f
            // 动画执行时长 1999
            this.duration = 1999
            // 在动画开始执行前是否渲染第一帧
            this.fillStart = false
            // 在动画结束后是否渲染最后一帧
            this.fillEnd = false
            // 配置动画在 startDelay 之后才开始绘制第一帧，衔接在 layerIn 之后
            this.startDelay = layerIn.startDelay + layerIn.duration
        }.build()

        // 出场动画：帧率 30, 一共有 60 张图片
        val layerOut = FlexFrameLayer.Builder().apply {
            // 动画资源文件，raw 类型，要求白天黑夜使用同一个 id
            this.ffab = Ffab.of(R.raw.example_ffa_anim3_out)
            // 动画显示位置
            this.x = 0f
            // 动画显示位置
            this.y = 0f
            // 动画显示位置
            this.width = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_width
            ).toFloat()
            // 动画显示位置
            this.height = resources.getDimensionPixelSize(
                R.dimen.example_ffa_animation3_render_height
            ).toFloat()
            // 动画帧率 30fps
            this.rate = 30f
            // 动画执行时长 1999ms
            this.duration = 1999
            // 在动画开始执行前是否渲染第一帧
            this.fillStart = false
            // 在动画结束后是否渲染最后一帧
            this.fillEnd = true
            // 配置动画在 startDelay 之后才开始绘制第一帧，衔接在 layerLoop 之后
            this.startDelay = layerLoop.startDelay + layerLoop.duration
        }.build()

        // 组成一个动画组
        val layerGroup = FlexFrameLayerGroup.Builder().apply {
            this.children.add(layerIn)
            this.children.add(layerLoop)
            this.children.add(layerOut)
            // 在动画组上配置 PlayState, 可以同时作用于 layerFirst 与 layerSecond
            this.playState = PlayState.Builder().apply {
                this.setUptimeRunningOffset(uptimeRunningOffset)
                this.start()
            }.build()
        }.build()
        return layerGroup
    }

    fun animationOut() {
        val bestAnimationOutOffset = findBestAnimationOutOffset()
        LogUtil.i { "animationOut bestAnimationOutOffset:$bestAnimationOutOffset" }
        if (bestAnimationOutOffset != null) {
            this.mLayerAnimationOut = buildAnimationOut(bestAnimationOutOffset)
            this.layer.value = this.mLayerAnimationOut
        }
    }

}
