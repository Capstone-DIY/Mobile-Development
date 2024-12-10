package com.dicoding.capstone_diy.ui.addDiary

import android.os.Bundle
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
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository

class AddDiaryFragment : Fragment() {

    private lateinit var repository: DiaryRepository

    private val addDiaryViewModel: AddDiaryViewModel by viewModels {
        AddDiaryViewModelFactory(requireContext()) // Pass the context here
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val currentTimestamp = System.currentTimeMillis()
        dateTextView.text = formatTimestamp(currentTimestamp)

        saveButton.setOnClickListener {
            val title = titleField.text.toString()
            val story = storyField.text.toString()

            if (title.isBlank() || story.isBlank()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addDiaryViewModel.insertDiary(
                title = title,
                story = story,
                onSuccess = {
                    Toast.makeText(context, "Diary added successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                },
                onError = { errorMessage ->
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            )
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