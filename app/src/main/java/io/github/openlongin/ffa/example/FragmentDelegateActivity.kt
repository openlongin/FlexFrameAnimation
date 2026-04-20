package io.github.openlongin.ffa.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class FragmentDelegateActivity<T : Fragment>(val delegateFragmentCreator: () -> T) :
    AppCompatActivity(R.layout.example_activity_fragment_delegate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_delegate_container,
                delegateFragmentCreator.invoke(),
            ).commitNow()
        }
    }

}