package com.dicoding.capstone_diy.ui.login

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(requireContext(), "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        firebaseUser?.getIdToken(true)
                            ?.addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val idToken = tokenTask.result?.token
                                    if (idToken != null) {
                                        loginViewModel.saveToken(requireContext(), idToken)
                                        // Menggunakan Log.d untuk menampilkan Firebase Token
                                        Log.d("LoginFragment", "Token berhasil disimpan: $idToken")
                                        loginViewModel.loginWithBackendAPI(email, password)
                                    } else {
                                        // Log jika tidak bisa mengambil token
                                        Log.d("LoginFragment", "Tidak dapat mengambil token")
                                    }
                                } else {
                                    // Log jika gagal mendapatkan token
                                    Log.d("LoginFragment", "Error mendapatkan token: ${tokenTask.exception?.message}")
                                }
                            }
                    } else {
                        // Menjaga Toast untuk Login gagal tetap ada
                        Toast.makeText(requireContext(), "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.etPassword.setOnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        observeLoginResult()

        checkToken()
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is LoginViewModel.Result.Loading -> {
                    Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.Result.Success -> {
                    saveLoginStatus()
                    Toast.makeText(requireContext(), "Login berhasil!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
                }
                is LoginViewModel.Result.Error -> {
                    Toast.makeText(requireContext(), "Login gagal: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun checkToken() {
        val token = loginViewModel.getToken(requireContext())
        if (token != null) {
            // Menggunakan Log untuk menampilkan token
            android.util.Log.d("LoginFragment", "Token tersedia: $token")
        } else {
            android.util.Log.d("LoginFragment", "Token belum tersedia")
        }
    }


    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_close),
                null
            )
        } else {
            binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open),
                null
            )
        }
        isPasswordVisible = !isPasswordVisible
        binding.etPassword.setSelection(binding.etPassword.text.length)
    }

    private fun saveLoginStatus() {
        val sharedPref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putBoolean("is_logged_in", true)
            .apply()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
