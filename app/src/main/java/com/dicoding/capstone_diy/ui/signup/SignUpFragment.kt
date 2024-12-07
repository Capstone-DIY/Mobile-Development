package com.dicoding.capstone_diy.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val contactNumber = binding.etContact.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validateInput(email, password, confirmPassword, contactNumber)) {
                Log.d("SignUpFragment", "Valid input, attempting to register user")
                signUpViewModel.registerUser(name, email, password, contactNumber)
            }
        }

        signUpViewModel.registerResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is SignUpViewModel.ResultState.Loading -> {
                    Log.d("SignUpFragment", "Loading registration...")
                }
                is SignUpViewModel.ResultState.Success -> {
                    Log.d("SignUpFragment", "Registration successful: ${result.data?.message}")
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                }
                is SignUpViewModel.ResultState.Error -> {
                    Log.e("SignUpFragment", "Registration failed: ${result.message}")
                    // Show the error message to user (gunakan Toast atau dialog)
                }
            }
        })
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String,
        contactNumber: String
    ): Boolean {
        if (email.isEmpty()) {
            Log.e("SignUpFragment", "Email cannot be empty")
            return false
        }
        if (contactNumber.isEmpty()) {
            Log.e("SignUpFragment", "Contact number cannot be empty")
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Log.e("SignUpFragment", "Password must be at least 6 characters")
            return false
        }
        if (password != confirmPassword) {
            Log.e("SignUpFragment", "Passwords do not match")
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
