package com.dicoding.capstone_diy.ui.addDiary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.Diary
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository
import java.util.Calendar

class AddDiaryFragment : Fragment() {

    private lateinit var repository: DiaryRepository

    // Inisialisasi ViewModel menggunakan factory yang dibuat secara manual
    private val addDiaryViewModel: AddDiaryViewModel by viewModels {
        AddDiaryViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Database dan Repository
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        repository = DiaryRepository(diaryDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleField = view.findViewById<EditText>(R.id.titleField)
        val storyField = view.findViewById<EditText>(R.id.storyField)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val backButton = view.findViewById<View>(R.id.back_icon)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)

        // Format timestamp sekarang ke hari dan tanggal
        val currentTimestamp = System.currentTimeMillis()
        dateTextView.text = formatTimestamp(currentTimestamp)

        saveButton.setOnClickListener {
            val title = titleField.text.toString()
            val story = storyField.text.toString()

            if (title.isBlank() || story.isBlank()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Membuat objek Diary
            val diary = Diary(
                id = 0, // Biarkan Room mengatur ID otomatis
                date = currentTimestamp,
                title = title,
                description = story
            )

            // Simpan ke database melalui ViewModel
            addDiaryViewModel.insertDiary(diary)

            Toast.makeText(context, "Diary added!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault())
        val date = java.util.Date(timestamp)
        return dateFormat.format(date)
    }
}
