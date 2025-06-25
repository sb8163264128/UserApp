package com.unplugged.data.ipc

object InterAppContracts {

    const val DATA_APP_PACKAGE_NAME = "com.unplugged.dataapp"

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