package com.example.safisha.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.safisha.R
import com.example.safisha.databinding.FragmentDashboardBinding
import com.example.safisha.domain.model.UserRole
import com.example.safisha.ui.viewmodel.ReportFilter
import com.example.safisha.ui.viewmodel.SafishaViewModel
import com.example.safisha.ui.viewmodel.WeatherUiState
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
        // The logged-in user lives in memory; if the process was killed, go back to login
        val user = viewModel.currentUser.value
        if (user == null) {
            goToLogin()
            return
        }

        val isAdmin = user.role == UserRole.ADMIN

        binding.tvWelcome.text = getString(R.string.dashboard_welcome, user.fullName)
        binding.tvRole.text = getString(
            R.string.dashboard_role,
            getString(if (isAdmin) R.string.role_admin else R.string.role_resident)
        )

        // Admins manage reports; residents file them
        binding.cardReport.visibility = if (isAdmin) View.GONE else View.VISIBLE
        binding.tvReportsTitle.setText(if (isAdmin) R.string.card_all_reports else R.string.card_my_reports)
        binding.tvReportsSub.setText(if (isAdmin) R.string.card_all_reports_sub else R.string.card_my_reports_sub)

        binding.cardReport.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_reportIssueFragment)
        }

        binding.cardReports.setOnClickListener {
            // Residents see only their own reports; admins see everything
            viewModel.setFilter(if (isAdmin) ReportFilter.All else ReportFilter.ByUser(user.id))
            findNavController().navigate(
                R.id.action_dashboardFragment_to_reportsPlaceholderFragment,
                bundleOf("showAll" to isAdmin)
            )
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            goToLogin()
        }

        // Weather card: fetch now, and again whenever the user taps Retry
        binding.btnRetryWeather.setOnClickListener { viewModel.loadWeather() }
        viewModel.loadWeather()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherState.collect { renderWeather(it) }
            }
        }
    }

    private fun renderWeather(state: WeatherUiState) {
        val loading = state is WeatherUiState.Loading
        val success = state is WeatherUiState.Success
        val error = state is WeatherUiState.Error

        binding.progressWeather.visibility = if (loading) View.VISIBLE else View.GONE
        binding.tvWeatherMain.visibility = if (success) View.VISIBLE else View.GONE
        binding.tvWeatherDetail.visibility = if (success) View.VISIBLE else View.GONE
        binding.tvWeatherAdvice.visibility = if (success) View.VISIBLE else View.GONE
        binding.tvWeatherError.visibility = if (error) View.VISIBLE else View.GONE
        binding.btnRetryWeather.visibility = if (error) View.VISIBLE else View.GONE

        when (state) {
            is WeatherUiState.Success -> {
                val w = state.weather
                binding.tvWeatherMain.text =
                    getString(R.string.weather_temp_desc, w.temperatureC, w.description)
                binding.tvWeatherDetail.text =
                    getString(R.string.weather_details, w.precipitationMm, w.windKmh)
                binding.tvWeatherAdvice.text = w.advice
            }
            is WeatherUiState.Error -> binding.tvWeatherError.text = state.message
            WeatherUiState.Loading -> Unit
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