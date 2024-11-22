package com.dicoding.capstone_diy.ui.detailsDiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.databinding.FragmentDetailsDiaryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsDiaryFragment : Fragment() {

    private var _binding: FragmentDetailsDiaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsDiaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Ambil data Diary dari Bundle menggunakan BundleCompat
        val diary = arguments?.let {
            BundleCompat.getParcelable<Diary>(it, "diary", Diary::class.java)
        }

        // Set data Diary ke UI
        diary?.let {
            binding.dateText.text = formatTimestamp(it.date) // Format timestamp ke tanggal
            binding.titleText.text = it.title
            binding.descriptionText.text = it.description
        }

        // Set OnClickListener untuk tombol back
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        return root
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
