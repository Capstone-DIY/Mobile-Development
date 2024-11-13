package com.dicoding.capstone_diy.ui.addDiary

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.capstone_diy.R
import java.util.Calendar

class AddDiaryFragment : Fragment() {

    private val viewModel: AddDiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil dateField dari layout
        val dateField = view.findViewById<EditText>(R.id.dateField)

        // Buat listener untuk dateField
        dateField.setOnClickListener {
            showDatePickerDialog(dateField)
        }
    }

    // Fungsi untuk menampilkan DatePickerDialog
    private fun showDatePickerDialog(dateField: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Membuat DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format tanggal yang dipilih dan set ke dateField
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateField.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}
