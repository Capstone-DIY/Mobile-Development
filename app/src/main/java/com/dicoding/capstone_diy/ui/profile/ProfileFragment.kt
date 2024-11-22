package com.dicoding.capstone_diy.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dicoding.capstone_diy.R
import com.dicoding.capstone_diy.databinding.FragmentProfileBinding
import com.dicoding.capstone_diy.utils.ThemeManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isSwitchInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                requireActivity().recreate()
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
        binding.logoutContainer.setOnClickListener {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_logout, null)

            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
                alertDialog.dismiss()
                requireActivity().finish()
            }

            dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
