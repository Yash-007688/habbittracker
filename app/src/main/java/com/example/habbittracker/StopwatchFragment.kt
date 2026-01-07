package com.example.habbittracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habbittracker.databinding.FragmentStopwatchBinding
import java.util.Locale

class StopwatchFragment : Fragment() {

    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = _binding!!

    private var isRunning = false
    private var startTime = 0L
    private var timeInMilliseconds = 0L
    private var timeSwapBuff = 0L
    private var updateTime = 0L
    
    private val handler = Handler(Looper.getMainLooper())
    
    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updateTime = timeSwapBuff + timeInMilliseconds
            
            val secs = (updateTime / 1000).toInt()
            val mins = secs / 60
            val hours = mins / 60
            val milliseconds = (updateTime % 1000).toInt() / 10
            
            val displaySecs = secs % 60
            val displayMins = mins % 60
            
            binding.tvStopwatchDisplay.text = String.format(
                Locale.getDefault(), 
                "%02d:%02d:%02d.%02d", 
                hours, displayMins, displaySecs, milliseconds
            )
            
            handler.postDelayed(this, 16) // ~60fps
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartStopwatch.setOnClickListener {
            if (isRunning) {
                pauseStopwatch()
            } else {
                startStopwatch()
            }
        }

        binding.btnResetStopwatch.setOnClickListener {
            resetStopwatch()
        }
    }

    private fun startStopwatch() {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(updateTimerThread, 0)
        isRunning = true
        binding.btnStartStopwatch.text = "Pause"
        binding.btnResetStopwatch.isEnabled = false
    }

    private fun pauseStopwatch() {
        timeSwapBuff += timeInMilliseconds
        handler.removeCallbacks(updateTimerThread)
        isRunning = false
        binding.btnStartStopwatch.text = "Start"
        binding.btnResetStopwatch.isEnabled = true
    }

    private fun resetStopwatch() {
        startTime = 0L
        timeInMilliseconds = 0L
        timeSwapBuff = 0L
        updateTime = 0L
        binding.tvStopwatchDisplay.text = "00:00:00"
        binding.btnStartStopwatch.text = "Start"
    }

    override fun onResume() {
        super.onResume()
        if (isRunning) {
            startTime = SystemClock.uptimeMillis()
            handler.postDelayed(updateTimerThread, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isRunning) {
            handler.removeCallbacks(updateTimerThread)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimerThread)
        _binding = null
    }
}