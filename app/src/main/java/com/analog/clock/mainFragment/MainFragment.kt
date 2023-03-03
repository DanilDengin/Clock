package com.analog.clock.mainFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.analog.clock.R
import com.analog.clock.databinding.FragmentMainBinding
import com.analog.clock.databinding.FragmentThemePickerBinding
import com.analog.clock.themePickerFragment.ThemeDelegate
import com.analog.clock.themePickerFragment.ThemePickerFragment
import com.analog.clock.timeFragment.TimeFragment

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            themeButton.setOnClickListener {
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainer, ThemePickerFragment())
                    addToBackStack(ThemePickerFragment().javaClass.simpleName)
                }
            }
            timeButton.setOnClickListener {
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainer, TimeFragment())
                    addToBackStack(TimeFragment().javaClass.simpleName)
                }
            }
        }
    }
}