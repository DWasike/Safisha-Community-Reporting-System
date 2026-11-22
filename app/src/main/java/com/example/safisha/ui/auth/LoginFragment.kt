package com.example.safisha.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.safisha.R
import com.example.safisha.databinding.FragmentLoginBinding
import com.example.safisha.ui.viewmodel.SafishaViewModel
import com.example.safisha.ui.viewmodel.UiEvent
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Shared with every screen so currentUser survives navigation
    private val viewModel: SafishaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Clear an inline error as soon as the user starts fixing the field
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }

        // Listen for one-off events from the ViewModel (login success, messages)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.LoggedIn -> findNavController()
                            .navigate(R.id.action_loginFragment_to_dashboardFragment)
                        is UiEvent.Message -> {
                            setLoading(false)
                            Toast.makeText(requireContext(), event.text, Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val emailError = when {
            email.isEmpty() -> getString(R.string.error_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.error_email_invalid)
            else -> null
        }
        val passwordError = if (password.isEmpty()) getString(R.string.error_password_required) else null

        binding.tilEmail.error = emailError
        binding.tilPassword.error = passwordError
        if (emailError != null || passwordError != null) return   // block the submit

        setLoading(true)
        viewModel.login(email, password)
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}