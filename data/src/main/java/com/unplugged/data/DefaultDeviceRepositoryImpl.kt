package com.unplugged.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.unplugged.data.ipc.AppResultReceiver
import com.unplugged.data.ipc.InterAppContracts
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultDeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
)  : DeviceRepository {

    private val TAG = "UserAppDeviceRepo"

    override fun fetchDeviceList(searchQuery: String?, callback: DeviceListCallback) {
        Log.d(TAG, "Attempting to fetch device list. Query: '$searchQuery'")

        val appResultReceiver = AppResultReceiver.create { resultCode, resultDataBundle ->
            Log.d(TAG, "fetchDeviceList - AppResultReceiver invoked. ResultCode: $resultCode")

            if (resultCode == InterAppContracts.RESULT_CODE_SUCCESS) {
                val deviceList = AppResultReceiver.parseReceivedDeviceList(resultDataBundle, gson)

                if (deviceList != null) {
                    Log.i(TAG, "Successfully fetched and parsed device list. Count: ${deviceList.size}")
                    callback(Result.success(deviceList))
                } else {
                    val errorMsg = "Received success code but failed to parse device list or list is null."
                    Log.e(TAG, errorMsg)
                    callback(Result.failure(Exception(errorMsg)))
                }
            } else {
                val errorMessage = AppResultReceiver.parseErrorMessage(resultDataBundle)
                    ?: "Unknown error fetching device list (ResultCode: $resultCode)"
                Log.e(TAG, "Failed to fetch device list: $errorMessage")
                callback(Result.failure(Exception(errorMessage)))
            }
        }

        val requestIntent = Intent(InterAppContracts.ACTION_REQUEST_DEVICE_LIST).apply {
            setPackage(InterAppContracts.DATA_APP_PACKAGE_NAME)
            putExtra(InterAppContracts.EXTRA_RESULT_RECEIVER, appResultReceiver)
            if (!searchQuery.isNullOrBlank()) {
                putExtra(InterAppContracts.EXTRA_SEARCH_QUERY, searchQuery)
            }
        }

        Log.d(TAG, "Sending broadcast for device list. Intent Action: ${requestIntent.action}, Target Package: ${requestIntent.getPackage()}, Extras: ${requestIntent.extras}")
        try {
            context.sendBroadcast(requestIntent)
            Log.d(TAG, "Broadcast for device list sent.")
        } catch (e: SecurityException) {
            val errorMsg = "SecurityException sending broadcast for device list. " +
                    "Does UserApp have the '${InterAppContracts.DATA_APP_PERMISSION}' permission in its manifest? " +
                    "Is DataApp installed and its receiver permission set correctly? " +
                    "Are signatures matching (if signature protection is enabled)? Error: ${e.message}"
            Log.e(TAG, errorMsg, e)
            callback(Result.failure(Exception(errorMsg)))
        } catch (e: Exception) {
            val errorMsg = "Failed to send broadcast for device list: ${e.message}"
            Log.e(TAG, errorMsg, e)
            callback(Result.failure(Exception(errorMsg)))
        }
    }

    override fun fetchDeviceDetails(deviceId: String, callback: DeviceDetailsCallback) {
        TODO("Not yet implemented")
    }


}