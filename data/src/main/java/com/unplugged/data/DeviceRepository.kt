package com.unplugged.data

// Using Kotlin Result for asynchronous operations
typealias DeviceListCallback = (Result<List<DeviceListItem>>) -> Unit
typealias DeviceDetailsCallback = (Result<DeviceDetails>) -> Unit

interface DeviceRepository {
    fun fetchDeviceList(searchQuery: String?, callback: DeviceListCallback)
    fun fetchDeviceDetails(deviceId: String, callback: DeviceDetailsCallback)
    // Potentially a method to clear or cancel ongoing requests if needed
}