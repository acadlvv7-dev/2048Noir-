package com.asadbek.noir2048.Fragments

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.asadbek.noir2048.databinding.FragmentGameScreenBinding
import com.asadbek.noir2048.AppRepository
import com.asadbek.noir2048.BackgroundUtil
import com.asadbek.noir2048.MyTouchListener
import com.asadbek.noir2048.R
import com.asadbek.noir2048.SideEnum


class GameScreenFragment : Fragment() {
    private var _binding: FragmentGameScreenBinding? = null
    private val binding get() = _binding!!
    private val list = ArrayList<TextView>()
    
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViews()
        initSoundPool()

        binding.restart.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_restart, null)
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<View>(R.id.btn_no).setOnClickListener {
                alertDialog.dismiss()
            }

            dialogView.findViewById<View>(R.id.btn_yes).setOnClickListener {
                alertDialog.dismiss()
                AppRepository.restartGame(requireContext())
                showMatrix()
            }

            alertDialog.show()

            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            alertDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        binding.backBtn.setOnClickListener {
            AppRepository.undoMove(requireContext())
            showMatrix()
        }

        binding.homeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val myTouchListener = MyTouchListener(requireContext())
        myTouchListener.setMoveSideListener { side ->
            when (side) {
                SideEnum.UP -> AppRepository.moveToUp()
                SideEnum.LEFT -> AppRepository.moveToLeft()
                SideEnum.RIGHT -> AppRepository.moveToRight()
                SideEnum.DOWN -> AppRepository.moveToDown()
            }
            AppRepository.saveGameState(requireContext())
            playClickSound()
            showMatrix()
            if (isGameOver()) {
                AppRepository.finishGame(requireContext())
                val record = AppRepository.getRecord(requireContext())
                val bundle = bundleOf("score" to AppRepository.score, "record" to record)
                val resultFragment = ResultFragment()
                resultFragment.arguments = bundle
                resultFragment.isCancelable = false
                resultFragment.show(requireActivity().supportFragmentManager, "result")
            }
        }
        binding.container.setOnTouchListener(myTouchListener)
        showMatrix()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        
        clickSoundId = soundPool?.load(requireContext(), R.raw.tab_click_for_game, 1) ?: 0
    }

    private fun loadViews() {
        list.clear()
        for (i in 0 until 4) list.add(binding.line1.getChildAt(i) as TextView)
        for (i in 0 until 4) list.add(binding.line2.getChildAt(i) as TextView)
        for (i in 0 until 4) list.add(binding.line3.getChildAt(i) as TextView)
        for (i in 0 until 4) list.add(binding.line4.getChildAt(i) as TextView)
    }

    private fun showMatrix() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val value = AppRepository.matrix[i][j]
                list[i * 4 + j].text = if (value == 0) "" else value.toString()
                list[i * 4 + j].background = BackgroundUtil.getItemBackground(requireContext(), value)
            }
        }
        binding.scoreValue.text = AppRepository.score.toString()
        binding.recordValue.text = AppRepository.getRecord(requireContext()).toString()
    }

    private fun isGameOver(): Boolean {
        val m = AppRepository.matrix
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (m[i][j] == 0) return false
                if (i < 3 && m[i][j] == m[i + 1][j]) return false
                if (j < 3 && m[i][j] == m[i][j + 1]) return false
            }
        }
        return true
    }

    private fun playClickSound() {
        if (AppRepository.getMusicState(requireContext()) && clickSoundId != 0) {
            soundPool?.play(clickSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool?.release()
        soundPool = null
        _binding = null
    }
}