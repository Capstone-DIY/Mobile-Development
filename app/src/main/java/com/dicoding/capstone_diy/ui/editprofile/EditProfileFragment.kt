package com.dicoding.capstone_diy.ui.editprofile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val editProfileViewModel: EditProfileViewModel by viewModels()

    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var updateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var backButton: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Views
        nameEditText = view.findViewById(R.id.etName)
        usernameEditText = view.findViewById(R.id.etUsername)
        dateEditText = view.findViewById(R.id.etDate)
        contactEditText = view.findViewById(R.id.etContact)
        genderSpinner = view.findViewById(R.id.spinnerGender)
        updateButton = view.findViewById(R.id.btnUpdate)
        cancelButton = view.findViewById(R.id.btnCancel)
        backButton = view.findViewById(R.id.btnBack)

        requireActivity().findViewById<View>(R.id.nav_view).visibility = View.GONE

        // Listener tombol cancel
        cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Listener tombol cancel
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Listener untuk DatePicker
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Listener tombol update
        updateButton.setOnClickListener {
            updateProfile()
        }

        // Observasi hasil update
        editProfileViewModel.profileUpdateResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        })

        // Observasi error
        editProfileViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        // Parse tanggal saat ini di EditText jika ada
        val currentDob = dateEditText.text.toString()
        if (currentDob.isNotEmpty()) {
            val dateParts = currentDob.split("-")
            if (dateParts.size == 3) {
                calendar.set(
                    dateParts[0].toInt(), // Year
                    dateParts[1].toInt() - 1, // Month (0-based)
                    dateParts[2].toInt() // Day
                )
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                dateEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateProfile() {
        val name = nameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val dob = dateEditText.text.toString().trim().split("T").firstOrNull() ?: dateEditText.text.toString()
        val contact = contactEditText.text.toString().trim()
        val gender = genderSpinner.selectedItem.toString()

        if (name.isEmpty() || username.isEmpty() || dob.isEmpty() || contact.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Kirim data ke ViewModel untuk update
        editProfileViewModel.updateUserProfile(requireContext(), name, username, dob, contact, gender)
    }
}
