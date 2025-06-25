package com.unplugged.data.ipc // Or your UserApp's package for this file

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import com.google.gson.Gson
import com.unplugged.data.DeviceDetails
import com.unplugged.data.DeviceListItem

class AppResultReceiver(
    handler: Handler?,
    private val onResult: (resultCode: Int, resultData: Bundle?) -> Unit
) : ResultReceiver(handler) {

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        super.onReceiveResult(resultCode, resultData)
        Log.d("AppResultReceiver", "onReceiveResult: resultCode=$resultCode, data=$resultData")
        onResult(resultCode, resultData)
    }

    companion object {
        fun create(onResultCallback: (resultCode: Int, resultData: Bundle?) -> Unit): AppResultReceiver {
            return AppResultReceiver(Handler(Looper.getMainLooper()), onResultCallback)
        }

        fun parseReceivedDeviceList(bundle: Bundle?, gson: Gson): List<DeviceListItem>? {
            val jsonString = bundle?.getString(InterAppContracts.KEY_RESULT_DEVICE_LIST)
            return if (jsonString != null) {
                try {
                    gson.fromJson(jsonString, Array<DeviceListItem>::class.java)?.toList()
                } catch (e: Exception) {
                    Log.e("AppResultReceiver", "Companion: Error parsing device list JSON", e)
                    null
                }
            } else {
                null
            }
        }

        fun parseReceivedDeviceDetails(bundle: Bundle?, gson: Gson): DeviceDetails? {
            val jsonString = bundle?.getString(InterAppContracts.KEY_RESULT_DEVICE_DETAILS)
            return if (jsonString != null) {
                try {
                    gson.fromJson(jsonString, DeviceDetails::class.java)
                } catch (e: Exception) {
                    Log.e("AppResultReceiver", "Companion: Error parsing device details JSON", e)
                    null
                }
            } else {
                null
            }
        }

        fun parseErrorMessage(bundle: Bundle?): String? {
            return bundle?.getString(InterAppContracts.KEY_RESULT_ERROR_MESSAGE)
        }
    }
}