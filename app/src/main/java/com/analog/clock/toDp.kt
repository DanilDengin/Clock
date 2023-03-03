package com.analog.clock

import android.content.res.Resources

internal fun Float.toDp() = this * Resources.getSystem().displayMetrics.density