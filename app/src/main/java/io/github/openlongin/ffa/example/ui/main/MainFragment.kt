package io.github.openlongin.ffa.example.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.github.openlongin.ffa.example.R
import io.github.openlongin.ffa.example.databinding.ExampleFragmentMainBinding
import io.github.openlongin.ffa.example.ui.animation.AnimationActivity
import io.github.openlongin.ffa.example.ui.animation2.Animation2Activity
import io.github.openlongin.ffa.example.ui.imageview2.ImageView2Activity
import io.github.openlongin.ffa.player.util.LogUtil
import io.github.openlongin.ffa.player.util.objectTag

class MainFragment : Fragment(R.layout.example_fragment_main) {

    private val mObjectTag by lazy { objectTag(this@MainFragment) }
    private lateinit var mViewBinding: ExampleFragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LogUtil.d { "$mObjectTag onCreate" }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = ExampleFragmentMainBinding.bind(view)

        mViewBinding.exampleAnimation.setOnClickListener {
            directToExampleAnimation()
        }
        mViewBinding.exampleImageview2.setOnClickListener {
            directToExampleImageView2()
        }
        mViewBinding.exampleAnimation2.setOnClickListener {
            directToExampleAnimation2()
        }
    }

    private fun directToExampleAnimation() {
        val context = context ?: return
        val intent = Intent(context, AnimationActivity::class.java)
        context.startActivity(intent)
    }

    private fun directToExampleImageView2() {
        val context = context ?: return
        val intent = Intent(context, ImageView2Activity::class.java)
        context.startActivity(intent)
    }

    private fun directToExampleAnimation2() {
        val context = context ?: return
        val intent = Intent(context, Animation2Activity::class.java)
        context.startActivity(intent)
    }

}
