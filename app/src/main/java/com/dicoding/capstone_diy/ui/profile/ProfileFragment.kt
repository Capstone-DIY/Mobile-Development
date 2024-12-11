package com.dicoding.capstone_diy.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.data.DiaryDatabase
import com.dicoding.capstone_diy.data.DiaryRepository
import com.dicoding.capstone_diy.databinding.FragmentProfileBinding
import com.dicoding.capstone_diy.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isSwitchInitialized = false
    private val profileViewModel: ProfileViewModel by viewModels {
        val diaryDao = DiaryDatabase.getDatabase(requireContext()).diaryDao()
        val repository = DiaryRepository(diaryDao)
        ProfileViewModelFactory(repository) // Tidak perlu passing context
    }


    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        val token = profileViewModel.getToken(requireContext())

        if (token != null) {
            // Pass token to ViewModel for API call
            profileViewModel.fetchUserProfile(token)
            Log.d("ProfileFragment", "Token berhasil didapat: $token")
        } else {
            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "Tidak dapat mengambil token")
        }

        // Observasi data profil pengguna
        profileViewModel.userProfile.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                binding.profileName.text = user.name ?: "-"
                binding.profileUsername.text = user.username ?: "-"
                binding.profileDob.text = user.dob ?: "-"
                binding.profilePhone.text = user.contact_number ?: "-"
                binding.profileGender.text = user.gender ?: "-"
                Log.d("ProfileFragment", "Profile Data Updated: $user")
            }
        })

        // Observasi error
        profileViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (it.contains("Token expired") || it.contains("Token invalid")) {
                    profileViewModel.deleteAllDiaries()

                    // Clear token from SharedPreferences
                    val sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                    sharedPreferences.edit().remove("firebase_id_token").apply()

                    // Notify user and navigate to login screen
                    Toast.makeText(context, "Token expired or invalid. Please login again.", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
                } else {
                    // Handle other error messages
                    Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }
        })


        // Ambil status tema dari SharedPreferences
        val sharedPreferences =
            requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Atur Switch tanpa memicu listener
        isSwitchInitialized = false
        binding.themeSwitch.isChecked = isDarkMode
        isSwitchInitialized = true

        // Listener untuk Switch Tema
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isSwitchInitialized) {
                ThemeManager.saveThemePreference(requireContext(), isChecked)

                // Simpan fragment aktif sebelum Activity di-recreate
                val navController = findNavController()
                val sharedPref = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
                sharedPref.edit().putInt("lastFragment", navController.currentDestination?.id ?: R.id.navigation_home).apply()

                requireActivity().recreate() // Memulai ulang Activity
            }
        }

        // Navigasi ke Edit Profile
        binding.editProfileContainer.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_editProfileFragment)
        }

        // Navigasi ke Favorite History
        binding.favoriteContainer.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_favoriteHistoryFragment)
        }

        // Logout dan hapus status login
        binding.logoutContainer.setOnClickListener {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_logout, null)

            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
                profileViewModel.deleteAllDiaries()
                // Logout dari Firebase
                auth.signOut()

                // Hapus status login di SharedPreferences
                val sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply()

                // Tutup dialog dan navigasi kembali ke halaman login
                alertDialog.dismiss()
                findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
            }

            dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}