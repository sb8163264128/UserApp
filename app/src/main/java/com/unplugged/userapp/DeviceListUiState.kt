package com.unplugged.userapp

import com.unplugged.data.DeviceListItem

sealed class DeviceListUiState {
    data object Loading : DeviceListUiState()
    data object Initial : DeviceListUiState()
    data class Success(val devices: List<DeviceListItem>) : DeviceListUiState()
    data class Error(val message: String) : DeviceListUiState()
}