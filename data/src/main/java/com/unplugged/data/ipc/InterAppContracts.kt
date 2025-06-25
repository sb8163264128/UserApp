package com.unplugged.data.ipc

object InterAppContracts {

    // --- Actions ---
    const val ACTION_REQUEST_DEVICE_LIST = "com.unplugged.dataapp.ACTION_REQUEST_DEVICE_LIST"
    const val ACTION_REQUEST_DEVICE_DETAILS = "com.unplugged.dataapp.ACTION_REQUEST_DEVICE_DETAILS"

    // --- Extras ---
    const val EXTRA_RESULT_RECEIVER = "com.unplugged.dataapp.EXTRA_RESULT_RECEIVER"

    // --- Result Codes (for ResultReceiver) ---
    const val RESULT_CODE_SUCCESS = 0
    const val RESULT_CODE_ERROR = 1

    // --- Result Data Bundle Keys (for ResultReceiver's onReceiveResult data) ---
    const val KEY_RESULT_DEVICE_LIST = "com.unplugged.dataapp.KEY_RESULT_DEVICE_LIST"
    const val KEY_RESULT_DEVICE_DETAILS = "com.unplugged.dataapp.KEY_RESULT_DEVICE_DETAILS"
    const val KEY_RESULT_ERROR_MESSAGE = "com.unplugged.dataapp.KEY_RESULT_ERROR_MESSAGE"

    const val DATA_APP_PACKAGE_NAME = "com.unplugged.dataapp"

    const val DATA_APP_PERMISSION = "com.unplugged.dataapp.permission.SEND_DATA_REQUESTS"



    const val DEVICE_PROVIDER_ACTIVITY_ACTION = "com.unplugged.dataapp.action.PROVIDE_DEVICE_DATA"

    const val REQUEST_TYPE_EXTRA = "REQUEST_TYPE"
    const val REQUEST_DEVICE_LIST = "GET_DEVICE_LIST"
    const val REQUEST_DEVICE_DETAILS = "GET_DEVICE_DETAILS"

    const val EXTRA_SEARCH_QUERY = "SEARCH_QUERY"
    const val EXTRA_DEVICE_ID = "DEVICE_ID"

    const val RESULT_EXTRA_DEVICE_LIST_JSON = "DEVICE_LIST_JSON"
    const val RESULT_EXTRA_DEVICE_DETAILS_JSON = "DEVICE_DETAILS_JSON"
    const val RESULT_EXTRA_ERROR_MESSAGE = "ERROR_MESSAGE"

}