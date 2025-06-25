package com.unplugged.userapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.unplugged.data.ipc.InterAppContracts
import com.unplugged.userapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var deviceAdapter: DeviceAdapter
    private val TAG = "MainActivity"

    private val deviceListLauncher: ActivityResultLauncher<Intent> = getActivityResultLauncher()
    private val deviceDetailsLauncher: ActivityResultLauncher<Intent> = getDeviceDetailsLauncher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        collectUiState()
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter { device ->
            Log.d(TAG, "Device clicked: ${device.name} (ID: ${device.id})")
            launchDeviceProviderForDetails(device.id)
        }
        binding.recyclerViewDevices.apply {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupSearch() {
        binding.buttonSearch.setOnClickListener {
            launchDeviceProviderForList()
            hideKeyboard()
        }
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                launchDeviceProviderForList()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }


    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Default visibilities
                    binding.recyclerViewDevices.isVisible = false
                    binding.progressBar.isVisible = false
                    binding.textViewError.isVisible = false
                    binding.textViewEmptyState.isVisible = false

                    when (state) {
                        is DeviceListUiState.Initial -> {
                            Log.d("MainActivity", "UI State: Initial")
                            binding.textViewEmptyState.isVisible = true
                            binding.textViewEmptyState.text =
                                "Use the search bar above to find devices."
                        }

                        is DeviceListUiState.Loading -> {
                            Log.d("MainActivity", "UI State: Loading")
                            binding.progressBar.isVisible = true
                        }

                        is DeviceListUiState.Success -> {
                            if (state.devices.isEmpty()) {
                                Log.d("MainActivity", "UI State: Success - no devices")
                                binding.textViewEmptyState.isVisible = true
                                binding.textViewEmptyState.text = "No devices found for your query."
                            } else {
                                Log.d(
                                    "MainActivity",
                                    "UI State: Success - ${state.devices.size} devices"
                                )
                                binding.recyclerViewDevices.isVisible = true
                                deviceAdapter.submitList(state.devices)
                            }
                        }

                        is DeviceListUiState.Error -> {
                            Log.d("MainActivity", "UI State: Error - ${state.message}")
                            binding.textViewError.isVisible = true
                            binding.textViewError.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun getActivityResultLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "Received result from DataApp. ResultCode: ${result.resultCode}")

            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val deviceListJson =
                    data?.getStringExtra(InterAppContracts.RESULT_EXTRA_DEVICE_LIST_JSON)
                if (deviceListJson != null) {
                    viewModel.processDeviceListJson(deviceListJson)
                } else {
                    val errorMsg = if (data == null) "DataApp returned OK, but intent data is null."
                    else "DataApp returned OK, but no device list JSON found."
                    Log.w(TAG, errorMsg)
                    viewModel.processDeviceListError(errorMsg)
                }
            } else {
                val errorMessage =
                    result.data?.getStringExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE)
                        ?: "Request to DataApp failed or was canceled."
                Log.w(TAG, "DataApp request failed/canceled. Message: $errorMessage")
                viewModel.processDeviceListError(errorMessage)
            }
        }

    private fun getDeviceDetailsLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "DeviceDetailsLauncher: Received result. ResultCode: ${result.resultCode}")

            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val deviceDetailsJson =
                    data?.getStringExtra(InterAppContracts.RESULT_EXTRA_DEVICE_DETAILS_JSON)

                if (deviceDetailsJson != null) {
                    Log.i(TAG, "DeviceDetailsLauncher: Received device details JSON.")
                    viewModel.processDeviceDetailsJson(deviceDetailsJson)
                } else {
                    val errorMsg =
                        "DataApp returned OK for details, but device details JSON was missing."
                    Log.w(TAG, errorMsg)
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            } else {
                val deviceIdOnError = result.data?.getStringExtra(InterAppContracts.EXTRA_DEVICE_ID)
                val errorMessage =
                    result.data?.getStringExtra(InterAppContracts.RESULT_EXTRA_ERROR_MESSAGE)
                        ?: "Request for device details failed or was canceled."
                Log.w(
                    TAG,
                    "DeviceDetailsLauncher: Request failed for device ID ${deviceIdOnError ?: "Unknown"}. Message: $errorMessage"
                )
                Toast.makeText(
                    this,
                    "Failed to get details for device ${deviceIdOnError ?: "Unknown"}: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

    private fun launchDeviceProviderForList() {
        Log.d(TAG, "Preparing to launch DeviceProviderActivity in DataApp for device list.")
        viewModel.setLoadingState()

        val query = binding.editTextSearch.text.toString().trim()

        try {
            val intent = Intent(InterAppContracts.DEVICE_PROVIDER_ACTIVITY_ACTION).apply {
                setPackage(InterAppContracts.DATA_APP_PACKAGE_NAME)
                putExtra(
                    InterAppContracts.REQUEST_TYPE_EXTRA,
                    InterAppContracts.REQUEST_DEVICE_LIST
                )
                if (query.isNotEmpty()) {
                    putExtra(InterAppContracts.EXTRA_SEARCH_QUERY, query)
                }
            }
            deviceListLauncher.launch(intent)
            Log.i(TAG, "Intent launched to DataApp.")
        } catch (e: SecurityException) {
            val errorMsg = "SecurityException: Cannot launch DataApp."
            Log.e(TAG, "$errorMsg Check permissions and app installation. ${e.message}", e)
            Toast.makeText(this, "Error: Could not launch DataApp.", Toast.LENGTH_LONG)
                .show()
            viewModel.processDeviceListError(errorMsg)
        } catch (e: Exception) {
            val errorMsg = "Exception: Failed to launch DataApp."
            Log.e(TAG, "$errorMsg ${e.message}", e)
            Toast.makeText(this, "Error: Failed to launch DataApp.", Toast.LENGTH_LONG)
                .show()
            viewModel.processDeviceListError(errorMsg)
        }
    }

    private fun launchDeviceProviderForDetails(deviceId: String) {
        Log.d(TAG, "Preparing to launch DataApp for device DETAILS of ID: $deviceId")

        try {
            val intent = Intent(InterAppContracts.DEVICE_PROVIDER_ACTIVITY_ACTION).apply {
                setPackage(InterAppContracts.DATA_APP_PACKAGE_NAME)
                putExtra(
                    InterAppContracts.REQUEST_TYPE_EXTRA,
                    InterAppContracts.REQUEST_DEVICE_DETAILS
                )
                putExtra(InterAppContracts.EXTRA_DEVICE_ID, deviceId)
            }
            deviceDetailsLauncher.launch(intent)
            Log.i(TAG, "Intent for device DETAILS (ID: $deviceId) launched to DataApp.")

        } catch (e: SecurityException) {
            val errorMsg =
                "SecurityException launching DataApp for details (ID: $deviceId)."
            Log.e(TAG, "$errorMsg Check permissions. ${e.message}", e)
            Toast.makeText(
                this,
                "Error: Could not request details. App permission issue.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            val errorMsg = "Exception launching DataApp for details (ID: $deviceId)."
            Log.e(TAG, "$errorMsg ${e.message}", e)
            Toast.makeText(
                this,
                "Error: Could not request device details at this time.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}