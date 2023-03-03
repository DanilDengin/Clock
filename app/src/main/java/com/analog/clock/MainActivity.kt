package com.analog.clock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.analog.clock.mainFragment.MainFragment
import com.analog.clock.themePickerFragment.ThemeDelegate

class MainActivity : AppCompatActivity() {

    val themeDelegate: ThemeDelegate by lazy(LazyThreadSafetyMode.NONE) {
        ThemeDelegate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, MainFragment())
                setReorderingAllowed(true)
            }
        }
        themeDelegate.setTheme()
    }
}