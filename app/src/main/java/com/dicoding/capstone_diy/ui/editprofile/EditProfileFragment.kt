package com.dicoding.capstone_diy.ui.editprofile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dicoding.capstone_diy.R
import java.util.Calendar

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var profileImage: ImageView
    private lateinit var editImageButton: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var updateButton: Button
    private lateinit var cancelButton: Button

    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Views
        profileImage = view.findViewById(R.id.profileImage)
        editImageButton = view.findViewById(R.id.btnEditImage)
        nameEditText = view.findViewById(R.id.etName)
        dateEditText = view.findViewById(R.id.etDate)
        genderSpinner = view.findViewById(R.id.spinnerGender)
        updateButton = view.findViewById(R.id.btnUpdate)
        cancelButton = view.findViewById(R.id.btnCancel)

        // Listener untuk tombol back
        val btnBack: ImageView = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            // Kembali ke ProfileFragment
            findNavController().popBackStack()
        }

        // Listener untuk tombol cancel
        cancelButton.setOnClickListener {
            // Kembali ke ProfileFragment
            findNavController().popBackStack()
        }

        // Listener untuk mengganti gambar
        editImageButton.setOnClickListener {
            openImagePicker()
        }

        // DatePicker untuk tanggal
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Tombol Update
        updateButton.setOnClickListener {
            updateProfile()
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                selectedImageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .into(profileImage)
                }
            }
        }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
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
        val date = dateEditText.text.toString().trim()
        val gender = genderSpinner.selectedItem.toString()

        if (name.isEmpty()) {
            nameEditText.error = "Name cannot be empty"
            return
        }

        if (date.isEmpty()) {
            dateEditText.error = "Date cannot be empty"
            return
        }

        println("Profile updated: Name=$name, Date=$date, Gender=$gender, ImageUri=$selectedImageUri")

        findNavController().popBackStack()
    }
}
