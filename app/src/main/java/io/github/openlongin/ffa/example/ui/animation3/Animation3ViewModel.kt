package io.github.openlongin.ffa.example.ui.animation3

import androidx.lifecycle.ViewModel
import io.github.idonans.appcontext.AppContext
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.player.PlayState
import io.github.openlongin.ffa.player.ffab.Ffab
import io.github.openlongin.ffa.player.layer.FlexFrameLayer
import io.github.openlongin.ffa.player.layer.FlexFrameLayerGroup
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.MeasureHelper
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

    init {
        animationIn()
    }

    /**
     * 计算此时切换到进场动画的最佳 offset。如果返回 null 表示当前正在播放进场动画。
     */
    private fun findBestAnimationInOffset(): Long? {
        val currentLayer = this.layer.value
        if (currentLayer != null && currentLayer == this.mLayerAnimationIn) {
            // 当前正在播放进场动画
            return null
        }

        // 播放一帧需要的时间，帧率 30fps
        val durationPerFrame = 1000f / 30

        if (currentLayer != null && currentLayer == this.mLayerAnimationOut) {
            // 当前正在播放出场动画
            // 出场动画由三部分组成，总帧数 150 = in 30 + loop 60 + out 60，其中 loop 只播放一次
            val layer = currentLayer as FlexFrameLayerGroup
            val context = AppContext.getContext()
            val measureResult = MeasureHelper.measureLayer(context, layer)

            val frameIndexToDrawOfAnimIn = measureResult.getFrameIndexToDraw(layer.children[0])
            val frameIndexToDrawOfAnimLoop = measureResult.getFrameIndexToDraw(layer.children[1])
            val frameIndexToDrawOfAnimOut = measureResult.getFrameIndexToDraw(layer.children[2])

            // 进场动画由三部分组成，总帧数 150 = out 60 + in 30 + loop 60, 其中 loop 无限循环
            var bestOffset: Float

            if (frameIndexToDrawOfAnimIn != null && frameIndexToDrawOfAnimIn >= 0) {
                // 正在播放 in 30 帧
                // bestOffset = out 60 + in frameIndexToDrawOfAnimIn
                bestOffset = (60 + frameIndexToDrawOfAnimIn) * durationPerFrame
            } else if (frameIndexToDrawOfAnimLoop != null && frameIndexToDrawOfAnimLoop >= 0) {
                // 正在播放 loop 60 帧
                // bestOffset = out 60 + in 30 + loop frameIndexToDrawOfAnimLoop
                bestOffset = (60 + 30 + frameIndexToDrawOfAnimLoop) * durationPerFrame
            } else if (frameIndexToDrawOfAnimOut != null && frameIndexToDrawOfAnimOut >= 0) {
                // 正在播放 out 60 帧
                // bestOffset = out frameIndexToDrawOfAnimOut
                bestOffset = frameIndexToDrawOfAnimOut * durationPerFrame
            } else {
                // 动画没有开始
                // bestOffset = out 60
                bestOffset = 60 * durationPerFrame
            }

            LogUtil.d {
                "findBestAnimationInOffset frameIndexToDrawOfAnimIn:$frameIndexToDrawOfAnimIn," +
                        //
                        " frameIndexToDrawOfAnimLoop:$frameIndexToDrawOfAnimLoop," +
                        //
                        " frameIndexToDrawOfAnimOut:$frameIndexToDrawOfAnimOut," +
                        //
                        " durationPerFrame:$durationPerFrame, bestOffset:$bestOffset"
            }

            return bestOffset.toLong()
        }

        // bestOffset = out 60
        return (60 * durationPerFrame).toLong()
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

        // 播放一帧需要的时间，帧率 30fps
        val durationPerFrame = 1000f / 30

        if (currentLayer != null && currentLayer == this.mLayerAnimationIn) {
            // 当前正在播放进场动画
            // 进场动画由三部分组成，总帧数 150 = out 60 + in 30 + loop 60, 其中 loop 无限循环
            val layer = currentLayer as FlexFrameLayerGroup
            val context = AppContext.getContext()
            val measureResult = MeasureHelper.measureLayer(context, layer)

            val frameIndexToDrawOfAnimOut = measureResult.getFrameIndexToDraw(layer.children[0])
            val frameIndexToDrawOfAnimIn = measureResult.getFrameIndexToDraw(layer.children[1])
            val frameIndexToDrawOfAnimLoop = measureResult.getFrameIndexToDraw(layer.children[2])


            // 出场动画由三部分组成，总帧数 150 = in 30 + loop 60 + out 60，其中 loop 只播放一次
            var bestOffset: Float

            if (frameIndexToDrawOfAnimOut != null && frameIndexToDrawOfAnimOut >= 0) {
                // 正在播放 out 60 帧
                // bestOffset = in 30 + loop 60 + out frameIndexToDrawOfAnimOut
                bestOffset = (30 + 60 + frameIndexToDrawOfAnimOut) * durationPerFrame
            } else if (frameIndexToDrawOfAnimIn != null && frameIndexToDrawOfAnimIn >= 0) {
                // 正在播放 in 30 帧
                // bestOffset = in frameIndexToDrawOfAnimIn
                bestOffset = frameIndexToDrawOfAnimIn * durationPerFrame
            } else if (frameIndexToDrawOfAnimLoop != null && frameIndexToDrawOfAnimLoop >= 0) {
                // 正在播放 loop 60 帧
                // bestOffset = in 30 + loop frameIndexToDrawOfAnimLoop
                bestOffset = (30 + frameIndexToDrawOfAnimLoop) * durationPerFrame
            } else {
                // 动画没有开始
                // bestOffset = in 30 + loop 60
                bestOffset = (30 + 60) * durationPerFrame
            }

            LogUtil.d {
                "findBestAnimationOutOffset frameIndexToDrawOfAnimOut:$frameIndexToDrawOfAnimOut," +
                        //
                        " frameIndexToDrawOfAnimIn:$frameIndexToDrawOfAnimIn," +
                        //
                        " frameIndexToDrawOfAnimLoop:$frameIndexToDrawOfAnimLoop," +
                        //
                        " durationPerFrame:$durationPerFrame, bestOffset:$bestOffset"
            }
            return bestOffset.toLong()
        }

        // bestOffset = in 30 + loop 60
        return ((30 + 60) * durationPerFrame).toLong()
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
