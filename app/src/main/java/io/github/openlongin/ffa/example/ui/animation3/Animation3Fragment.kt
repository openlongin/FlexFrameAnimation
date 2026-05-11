package io.github.openlongin.ffa.example.ui.animation3

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.example.databinding.ExampleFragmentAnimation3Binding
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Animation3Fragment : Fragment(R.layout.example_fragment_animation3) {

    private val mObjectTag by lazy { objectTag(this@Animation3Fragment) }

    private val mViewModel: Animation3ViewModel by activityViewModels()
    private lateinit var mViewBinding: ExampleFragmentAnimation3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LogUtil.d { "$mObjectTag onCreate" }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    bindLayer()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = ExampleFragmentAnimation3Binding.bind(view)
        mViewBinding.ffaView.holder.setFormat(PixelFormat.TRANSLUCENT)

        applyLayer(fromViewCreated = true)

        mViewBinding.ffaBtnIn.setOnClickListener {
            mViewModel.animationIn()
        }
        mViewBinding.ffaBtnOut.setOnClickListener {
            mViewModel.animationOut()
        }
    }

    @OptIn(FlowPreview::class)
    private fun CoroutineScope.bindLayer() {
        launch {
            mViewModel.layer.collectLatest {
                applyLayer(updateLayer = true)
            }
        }
    }

    private fun applyLayer(
        fromViewCreated: Boolean = false,
        updateLayer: Boolean = false,
    ) {
        if (fromViewCreated || updateLayer) {
            mViewBinding.ffaView.flexFrameLayer = mViewModel.layer.value
        }
    }

}
