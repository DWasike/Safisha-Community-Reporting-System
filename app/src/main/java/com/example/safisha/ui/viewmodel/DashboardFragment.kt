package com.example.safisha.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.safisha.R
import com.example.safisha.databinding.FragmentDashboardBinding
import com.example.safisha.ui.viewmodel.SafishaViewModel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SafishaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // If the app process was killed, the in-memory user is gone: back to login
        if (viewModel.currentUser.value == null) {
            goToLogin()
            return
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            goToLogin()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentUser.collect { user ->
                    if (user != null) {
                        binding.tvWelcome.text = getString(R.string.dashboard_welcome, user.fullName)
                        binding.tvRole.text = getString(R.string.dashboard_role, user.role)
                    }
                }
            }
        }
    }

    private fun goToLogin() {
        findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}