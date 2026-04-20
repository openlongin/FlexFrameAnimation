package io.github.openlongin.ffa.example.ui.imageview2

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.example.databinding.ExampleFragmentImageview2Binding
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag

class ImageView2Fragment : Fragment(R.layout.example_fragment_imageview2) {

    private val mObjectTag by lazy { objectTag(this@ImageView2Fragment) }
    private lateinit var mViewBinding: ExampleFragmentImageview2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LogUtil.d { "$mObjectTag onCreate" }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = ExampleFragmentImageview2Binding.bind(view)

        mViewBinding.imageResource1.setOnClickListener {
            mViewBinding.imageView2.setImageResource(R.raw.example_ffa_bg)
        }
        mViewBinding.imageResource2.setOnClickListener {
            mViewBinding.imageView2.setImageResource(R.raw.example_ffa_bg2)
        }

        mViewBinding.btnScaleTypeFitXY.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.FIT_XY)
        }
        mViewBinding.btnScaleTypeFitStart.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.FIT_START)
        }
        mViewBinding.btnScaleTypeFitCenter.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.FIT_CENTER)
        }
        mViewBinding.btnScaleTypeFitEnd.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.FIT_END)
        }
        mViewBinding.btnScaleTypeCenter.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.CENTER)
        }
        mViewBinding.btnScaleTypeCenterCrop.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP)
        }
        mViewBinding.btnScaleTypeCenterInside.setOnClickListener {
            mViewBinding.imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
        }
    }

}

