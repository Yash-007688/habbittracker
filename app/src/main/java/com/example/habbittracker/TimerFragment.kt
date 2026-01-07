package com.example.habbittracker

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.habbittracker.databinding.FragmentTimerBinding
import java.util.Locale

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0

    private var pomodoroMode = false
    private var pomodoroSessionCount = 0
    private var isBreak = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.switchPomodoro.setOnCheckedChangeListener { _, isChecked ->
            pomodoroMode = isChecked
            if (isChecked) {
                binding.tvPomodoroStatus.visibility = View.VISIBLE
                binding.llTimeInput.visibility = View.GONE
                binding.tvTimerDisplay.visibility = View.VISIBLE
                resetPomodoro()
            } else {
                binding.tvPomodoroStatus.visibility = View.GONE
                resetTimer()
            }
        }

        binding.btnStartTimer.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                if (!pomodoroMode && timeLeftInMillis == 0L) {
                    val minutesStr = binding.etMinutes.text.toString()
                    val secondsStr = binding.etSeconds.text.toString()
                    
                    val minutes = if (minutesStr.isNotEmpty()) minutesStr.toLong() else 0
                    val seconds = if (secondsStr.isNotEmpty()) secondsStr.toLong() else 0
                    
                    if (minutes == 0L && seconds == 0L) {
                        Toast.makeText(context, "Please set a time", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    
                    startTimeInMillis = (minutes * 60 + seconds) * 1000
                    timeLeftInMillis = startTimeInMillis
                }
                startTimer()
            }
        }
        
        binding.btnResetTimer.setOnClickListener {
            if (pomodoroMode) resetPomodoro() else resetTimer()
        }
    }

    private fun resetPomodoro() {
        countDownTimer?.cancel()
        isRunning = false
        pomodoroSessionCount = 0
        isBreak = false
        setupFocusSession()
        updateCountDownText()
        binding.btnStartTimer.text = "Start"
        binding.btnResetTimer.visibility = View.INVISIBLE
    }

    private fun setupFocusSession() {
        isBreak = false
        timeLeftInMillis = 25 * 60 * 1000L
        binding.tvPomodoroStatus.text = "Focus Session (${pomodoroSessionCount + 1}/4)"
        binding.tvPomodoroStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        updateCountDownText()
    }

    private fun setupBreakSession() {
        isBreak = true
        pomodoroSessionCount++
        if (pomodoroSessionCount >= 4) {
            timeLeftInMillis = 15 * 60 * 1000L
            binding.tvPomodoroStatus.text = "Long Break"
            pomodoroSessionCount = 0
        } else {
            timeLeftInMillis = 5 * 60 * 1000L
            binding.tvPomodoroStatus.text = "Short Break"
        }
        binding.tvPomodoroStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        updateCountDownText()
    }

    private fun startTimer() {
        // Switch view from edit to display
        binding.llTimeInput.visibility = View.GONE
        binding.tvTimerDisplay.visibility = View.VISIBLE
        
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isRunning = false
                binding.btnStartTimer.text = "Start"
                
                if (pomodoroMode) {
                    if (isBreak) {
                        setupFocusSession()
                        Toast.makeText(context, "Break Finished! Time to focus.", Toast.LENGTH_SHORT).show()
                    } else {
                        setupBreakSession()
                        Toast.makeText(context, "Focus Session Finished! Take a break.", Toast.LENGTH_SHORT).show()
                    }
                    binding.btnResetTimer.visibility = View.VISIBLE
                } else {
                    binding.btnStartTimer.visibility = View.INVISIBLE
                    binding.btnResetTimer.visibility = View.VISIBLE
                    timeLeftInMillis = 0
                    Toast.makeText(context, "Timer Finished!", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()

        isRunning = true
        binding.btnStartTimer.text = "Pause"
        binding.btnResetTimer.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        binding.btnStartTimer.text = "Start"
        binding.btnResetTimer.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        timeLeftInMillis = 0
        updateCountDownText()
        
        // Switch back to input mode
        binding.tvTimerDisplay.visibility = View.GONE
        binding.llTimeInput.visibility = View.VISIBLE
        
        binding.btnResetTimer.visibility = View.INVISIBLE
        binding.btnStartTimer.visibility = View.VISIBLE
        binding.btnStartTimer.text = "Start"
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.tvTimerDisplay.text = timeFormatted
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}