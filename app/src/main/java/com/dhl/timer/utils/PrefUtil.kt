package com.dhl.timer.utils

import android.content.Context
import android.preference.PreferenceManager
import com.dhl.timer.TimerActivity

/**
 * Created by Hitesh on 2/12/2018.
 */
class PrefUtil {
    companion object {
        fun getTimerLength(mContext: Context): Int {
            return 1;
        }

        private const val PREVIOUS_TIMER_LENTGH_SECONDS_ID = "com.dhl.privious_time_lenght_second"

        fun getPreviousTimerLengthSeconds(mContext: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            return preferences.getLong(PREVIOUS_TIMER_LENTGH_SECONDS_ID, 0)
        }

        fun setPrevisouTimeLengthSeconds(mContext: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
            editor.putLong(PREVIOUS_TIMER_LENTGH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.dhl.timer_id"
        fun getTimerState(mContext: Context): TimerActivity.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(mContext: Context, state: TimerActivity.TimerState) {
            val editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECOND_REMAING_ID = "com.dhl.remaining_id"
        fun getSecondsRemaining(mContext: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            return preferences.getLong(SECOND_REMAING_ID, 0)
        }

        fun setSecondsRemaining(mContext: Context, seconds: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
            editor.putLong(SECOND_REMAING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.dhl.alarm_time_id"

        fun getAlarmSetTime(mContext: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            return preferences.getLong(ALARM_SET_TIME_ID, 0);
        }

        fun setAlarmSetTime(mContext: Context, time: Long) {
            val editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }

    }
}