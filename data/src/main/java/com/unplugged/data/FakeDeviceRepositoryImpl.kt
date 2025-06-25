package com.unplugged.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unplugged.data.ipc.AppResultReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeDeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : DeviceRepository {

    private val json = """
    [
        {
            "color": "Midnight Blue",
            "id": "api-123",
            "name": "Super Phone X (Fake)",
            "data": {
                "Screen size": "6.1 inches",
                "storage_capacity": "256GB",
                "ram": "8GB"
            }
        }
    ]
    """

/*
    override fun getDevicesStream(query: String): Flow<List<DeviceListItem>> {
        return flow {
            Log.d("FakeDeviceRepository", "Fetching fake devices with query: '$query'")
            // Simulate network/processing delay
            delay(500) // 0.5 seconds delay

            try {
                val listType = object : TypeToken<List<DeviceListItem>>() {}.type
                val allDevices: List<DeviceListItem> = gson.fromJson(json, listType)
                emit(allDevices)
            } catch (e: Exception) {
                Log.e("FakeDeviceRepository", "Error parsing fake JSON", e)
                emit(emptyList())
            }
        }

    }
*/

    override fun fetchDeviceList(searchQuery: String?, callback: DeviceListCallback) {
        val list = gson.fromJson(json, Array<DeviceListItem>::class.java)?.toList() ?: emptyList()
        callback.invoke(Result.success(list))
    }

    override fun fetchDeviceDetails(deviceId: String, callback: DeviceDetailsCallback) {
        TODO("Not yet implemented")
    }
}