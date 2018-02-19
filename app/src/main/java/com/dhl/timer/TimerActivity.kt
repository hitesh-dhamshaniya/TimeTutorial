package com.dhl.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.dhl.timer.utils.PrefUtil
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSecond = 0L;
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer_black_24dp)
        supportActionBar?.title = "  Timer"

        fab_start.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }


    }

    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm(this)
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(this)
        if (timerState == TimerState.Stopped) {
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused) {
            PrefUtil.getSecondsRemaining(this)
        } else {
            timerLengthSecond
        }

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0) {
            secondsRemaining -= nowSeconds - alarmSetTime
        }

        if (alarmSetTime <= 0) {
            onTimerFinished()
        } else
            if (timerState == TimerState.Running) {
                startTimer()
            }

        updateButtons()
        updateCountDownUI()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }

            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }

            TimerState.Paused -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }

        }
    }

    private fun updateCountDownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        tv_count_down.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progressbar_countdown.progress = (timerLengthSecond - secondsRemaining).toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSecond = PrefUtil.getPreviousTimerLengthSeconds(this)
        progressbar_countdown.max = timerLengthSecond.toInt()
    }

    private fun startTimer() {
        timerState = TimerState.Running
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountDownUI()
            }
        }.start()

    }

    fun onTimerFinished() {
        timerState = TimerState.Stopped
        setNewTimerLength()
        progressbar_countdown.progress = 0
        PrefUtil.setSecondsRemaining(this, timerLengthSecond)
        secondsRemaining = timerLengthSecond
        updateButtons()
        updateCountDownUI()
    }

    private fun setNewTimerLength() {
        val lengthInMinute = PrefUtil.getTimerLength(this)
        timerLengthSecond = (lengthInMinute * 60L)
        progressbar_countdown.max = timerLengthSecond.toInt()
    }

    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            //TODO start background
        } else if (timerState == TimerState.Paused) {
            //TODO show notification
        }

        PrefUtil.setPrevisouTimeLengthSeconds(this, timerLengthSecond)
        PrefUtil.setSecondsRemaining(this, secondsRemaining)
        PrefUtil.setTimerState(this, timerState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun setAlarm(mContext: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManage = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(mContext, TimerExpireReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)
            alarmManage.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(mContext, nowSeconds)
            return wakeUpTime
        }

        fun removeAlarm(mContext: Context) {
            val intent = Intent(mContext, TimerExpireReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)
            val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(mContext, 0)
        }

        val nowSeconds: Long get() = Calendar.getInstance().timeInMillis / 1000
    }
}
