package com.analog.clock.themePickerFragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.analog.clock.MainActivity
import com.analog.clock.R
import com.analog.clock.databinding.FragmentThemePickerBinding

class ThemePickerFragment : Fragment(R.layout.fragment_theme_picker)  {

    private val binding by viewBinding(FragmentThemePickerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity: MainActivity = activity as MainActivity
        val themeDelegate = mainActivity.themeDelegate
        setSelectedButton(themeDelegate)
        binding.radioGroupThemePicker.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonSystemTheme -> themeDelegate.setSystemMode()
                R.id.radioButtonLightTheme -> themeDelegate.setLightMode()
                R.id.radioButtonNightTheme -> themeDelegate.setNightMode()
            }
        }
    }

    private fun setSelectedButton(themeDelegate: ThemeDelegate?) {
        themeDelegate?.setThemeButton(
            { binding.radioButtonLightTheme.isChecked = true },
            { binding.radioButtonNightTheme.isChecked = true },
            { binding.radioButtonSystemTheme.isChecked = true }
        )
    }
}
