package com.unplugged.data

data class DeviceListItem(
    val id: String,
    val name: String?,
    val itemData: DeviceSpecificData? = null
)

data class DeviceDetails(
    val id: String,
    val name: String?,
    val data: DeviceSpecificData?
)

data class DeviceSpecificData(
    val color: String? = null,
    val capacity: String? = null,
    val price: Double? = null,
    val generation: String? = null,
    val year: Int? = null,
    val cpuModel: String? = null,
    val hardDiskSize: String? = null,
    val strapColour: String? = null,
    val caseSize: String? = null,
    val altColor: String? = null,
    val description: String? = null,
    val screenSize: Double? = null,
    val capacityGB: String? = null,
    val altPrice: String? = null
)
