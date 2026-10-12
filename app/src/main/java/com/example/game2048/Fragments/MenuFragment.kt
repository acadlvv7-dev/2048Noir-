package com.asadbek.noir2048.Fragments
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.asadbek.noir2048.R
import com.asadbek.noir2048.databinding.FragmentMenuBinding
import com.asadbek.noir2048.AppRepository

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShare.setOnClickListener {
            val packageName = requireActivity().packageName
            val playStoreLink = "https://play.google.com/store/apps/details?id=$packageName"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "2048 Noir - Play and set new records!\n\nDownload: $playStoreLink")
            }
            requireActivity().startActivity(Intent.createChooser(shareIntent, "Share app via"))
        }

        binding.infoApp.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_info, null)
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<View>(R.id.btn_ok).setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
            
            // Dialog o'lchamini barcha ekranlar uchun to'g'rilash (85% kenglik)
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            alertDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        var isMusicOn = AppRepository.getMusicState(requireContext())
        updateMusicIcon(isMusicOn)

        binding.btnMusic.setOnClickListener {
            isMusicOn = !isMusicOn
            AppRepository.setMusicState(requireContext(), isMusicOn)
            updateMusicIcon(isMusicOn)
        }

        binding.recordsBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_records, null)
            val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val records = AppRepository.getTop3Records(requireContext())
            val r1 = dialogView.findViewById<android.widget.TextView>(R.id.record1)
            val r2 = dialogView.findViewById<android.widget.TextView>(R.id.record2)
            val r3 = dialogView.findViewById<android.widget.TextView>(R.id.record3)
            val noR = dialogView.findViewById<android.widget.TextView>(R.id.noRecords)

            if (records.isEmpty()) {
                r1.visibility = View.GONE
                r2.visibility = View.GONE
                r3.visibility = View.GONE
                noR.visibility = View.VISIBLE
            } else {
                noR.visibility = View.GONE
                if (records.size >= 1) r1.text = "1. ${records[0]}" else r1.visibility = View.GONE
                if (records.size >= 2) r2.text = "2. ${records[1]}" else r2.visibility = View.GONE
                if (records.size >= 3) r3.text = "3. ${records[2]}" else r3.visibility = View.GONE
            }

            dialogView.findViewById<View>(R.id.btn_ok).setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()

            // Dialog o'lchamini barcha ekranlar uchun to'g'rilash (85% kenglik)
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            alertDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        binding.startBtn.setOnClickListener {
            AppRepository.restartGame(requireContext())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GameScreenFragment())
                .addToBackStack(null)
                .commit()
        }

        val hasSavedGame = AppRepository.hasSavedGame(requireContext())
        binding.continiuGameBtn.alpha = if (hasSavedGame) 1.0f else 0.4f
        binding.continiuGameBtn.isEnabled = hasSavedGame

        binding.continiuGameBtn.setOnClickListener {
            AppRepository.loadGameState(requireContext())
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GameScreenFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateMusicIcon(isOn: Boolean) {
        if (isOn) {
            binding.ivMusic.setImageResource(R.drawable.ic_music_on)
        } else {
            binding.ivMusic.setImageResource(R.drawable.ic_music_off)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}