package com.unplugged.userapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.unplugged.data.DeviceListItem
import com.unplugged.data.DeviceDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gson: Gson,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceListUiState>(DeviceListUiState.Initial)
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    private val TAG = "MainViewModel"

    fun setLoadingState() {
        Log.d(TAG, "Setting UI state to Loading.")
        _uiState.value = DeviceListUiState.Loading
    }

    fun processDeviceListJson(deviceListJson: String) {
        Log.d(TAG, "Processing device list JSON. Current state: ${_uiState.value}")
        if (_uiState.value !is DeviceListUiState.Loading) {
            _uiState.value = DeviceListUiState.Loading
        }

        viewModelScope.launch {
            try {
                // Perform JSON parsing on a background thread
                val devices = withContext(Dispatchers.Default) {
                    Log.d(TAG, "Parsing JSON on Dispatchers.Default")
                    val listType = object : TypeToken<List<DeviceListItem>>() {}.type
                    gson.fromJson<List<DeviceListItem>>(deviceListJson, listType)
                }
                Log.i(TAG, "Successfully parsed ${devices.size} devices. Updating UI to Success.")
                _uiState.value = DeviceListUiState.Success(devices)
            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "JSON Syntax Error parsing device list: ${e.message}", e)
                _uiState.value = DeviceListUiState.Error("Invalid data format from DataApp.")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing device list JSON: ${e.message}", e)
                _uiState.value = DeviceListUiState.Error("Failed to process device data: ${e.message}")
            }
        }
    }

    fun processDeviceDetailsJson(deviceDetailsJson: String) {
        Log.d(TAG, "Processing device details JSON.")

        viewModelScope.launch {
            try {
                val details = withContext(Dispatchers.Default) {
                    Log.d(TAG, "Parsing device details JSON on Dispatchers.Default")
                    gson.fromJson(deviceDetailsJson, DeviceDetails::class.java)
                }

                if (details?.id != null) {
                    Log.i(TAG, "Successfully parsed details for device ID: ${details.id}. Details: $details")

                    val currentUiState = _uiState.value
                    if (currentUiState is DeviceListUiState.Success) {
                        val updatedList = currentUiState.devices.map { listItem ->
                            if (listItem.id == details.id) {
                                listItem.copy(itemData = details.data)
                            } else {
                                listItem
                            }
                        }
                        _uiState.value = DeviceListUiState.Success(updatedList, details.id)
                        Log.d(TAG, "Updated _uiState with details for device ID: ${details.id}")
                    } else {
                        Log.w(TAG, "Received device details, but current UI state is not Success. State: $currentUiState")
                    }
                } else {
                    Log.w(TAG, "Parsed device details but ID was null or details object was null. JSON: $deviceDetailsJson")
                }

            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "JSON Syntax Error parsing device details: ${e.message}. JSON: $deviceDetailsJson", e)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing device details JSON: ${e.message}. JSON: $deviceDetailsJson", e)
            }
        }
    }

    fun processDeviceListError(errorMessage: String) {
        Log.e(TAG, "Processing error: $errorMessage. Current state: ${_uiState.value}")
        _uiState.value = DeviceListUiState.Error(errorMessage)
    }


}