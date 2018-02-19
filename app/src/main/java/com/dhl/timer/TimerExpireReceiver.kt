package com.dhl.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dhl.timer.utils.PrefUtil

class TimerExpireReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        PrefUtil.setTimerState(context, TimerActivity.TimerState.Stopped)
        PrefUtil.setAlarmSetTime(context, 0)
    }
}
