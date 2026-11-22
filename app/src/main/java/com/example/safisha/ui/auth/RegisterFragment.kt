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
import com.example.safisha.databinding.FragmentRegisterBinding
import com.example.safisha.domain.model.UserRole
import com.example.safisha.ui.viewmodel.SafishaViewModel
import com.example.safisha.ui.viewmodel.UiEvent
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SafishaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.btnToLogin.setOnClickListener { findNavController().popBackStack() }

        binding.etName.doOnTextChanged { _, _, _, _ -> binding.tilName.error = null }
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }
        binding.etConfirm.doOnTextChanged { _, _, _, _ -> binding.tilConfirm.error = null }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.Message -> {
                            setLoading(false)
                            Toast.makeText(requireContext(), event.text, Toast.LENGTH_LONG).show()
                        }
                        // Account created: go back to the Login screen
                        is UiEvent.Registered -> findNavController().popBackStack()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun attemptRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirm.text.toString()

        val nameError = when {
            name.isEmpty() -> getString(R.string.error_name_required)
            name.length < 2 -> getString(R.string.error_name_short)
            else -> null
        }
        val emailError = when {
            email.isEmpty() -> getString(R.string.error_email_required)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.error_email_invalid)
            else -> null
        }
        val passwordError = when {
            password.isEmpty() -> getString(R.string.error_password_required)
            password.length < 6 -> getString(R.string.error_password_short)
            else -> null
        }
        val confirmError = if (confirm != password) getString(R.string.error_confirm_mismatch) else null

        binding.tilName.error = nameError
        binding.tilEmail.error = emailError
        binding.tilPassword.error = passwordError
        binding.tilConfirm.error = confirmError

        if (listOf(nameError, emailError, passwordError, confirmError).any { it != null }) return

        val role = if (binding.rbAdmin.isChecked) UserRole.ADMIN else UserRole.RESIDENT

        setLoading(true)
        viewModel.register(name, email, password, role)
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}