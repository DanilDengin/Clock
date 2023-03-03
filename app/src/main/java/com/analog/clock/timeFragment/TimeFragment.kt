package com.analog.clock.timeFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.analog.clock.R
import com.analog.clock.databinding.FragmentThemePickerBinding
import com.analog.clock.databinding.FragmentTimeBinding
import com.analog.clock.view.ClockView

class TimeFragment : Fragment(R.layout.fragment_time) {

    private val binding by viewBinding(FragmentTimeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val clock = view.findViewById(R.id.clockView) as ClockView
        binding.radioGroupTimePicker.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonBigSizeClock -> clock.setClockRadius(180f)
                R.id.radioButtonMediumSizeClock -> clock.setClockRadius(130f)
                R.id.radioButtonSmallSizeClock -> clock.setClockRadius(80f)
            }
        }
    }
}