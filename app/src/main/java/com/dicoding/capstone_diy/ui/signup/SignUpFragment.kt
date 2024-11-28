package com.dicoding.capstone_diy.ui.signup

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.databinding.FragmentSignUpBinding

import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val contact = binding.etContact.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validateInput(name, email, contact, password, confirmPassword)) {
                registerUser(email, password)
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etPassword.setOnTouchListener { _, event ->
            handlePasswordToggle(event, isPasswordField = true)
        }

        binding.etConfirmPassword.setOnTouchListener { _, event ->
            handlePasswordToggle(event, isPasswordField = false)
        }
    }

    private fun registerUser(email: String, password: String) {
        binding.btnSubmit.isEnabled = false // Disable tombol saat proses berjalan
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.btnSubmit.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Sign-Up Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun validateInput(
        name: String,
        email: String,
        contact: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Invalid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (contact.isEmpty() || contact.length < 10) {
            Toast.makeText(requireContext(), "Invalid contact number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun handlePasswordToggle(event: MotionEvent, isPasswordField: Boolean): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = 2
            val editText = if (isPasswordField) binding.etPassword else binding.etConfirmPassword
            if (event.rawX >= (editText.right - editText.compoundDrawables[drawableEnd].bounds.width())) {
                togglePasswordVisibility(isPasswordField)
                return true
            }
        }
        return false
    }

    private fun togglePasswordVisibility(isPasswordField: Boolean) {
        val editText = if (isPasswordField) binding.etPassword else binding.etConfirmPassword
        val isVisible = if (isPasswordField) isPasswordVisible else isConfirmPasswordVisible

        if (isVisible) {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_close),
                null
            )
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open),
                null
            )
        }

        if (isPasswordField) {
            isPasswordVisible = !isPasswordVisible
        } else {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
        }
        editText.setSelection(editText.text.length)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

