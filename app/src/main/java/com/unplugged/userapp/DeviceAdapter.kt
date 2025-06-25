package com.unplugged.userapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unplugged.data.DeviceListItem
import com.unplugged.userapp.databinding.ItemDeviceBinding

class DeviceAdapter(
    private val onItemClicked: (DeviceListItem) -> Unit
) : ListAdapter<DeviceListItem, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device, onItemClicked)
    }

    class DeviceViewHolder(
        private val binding: ItemDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceListItem, onItemClicked: (DeviceListItem) -> Unit) {
            with(binding) {
                textViewName.text = device.name
                textViewId.text = "ID: ${device.id}"
                bindDetailsData(device)

                root.setOnClickListener {
                    onItemClicked(device)
                }
            }
        }

        private fun ItemDeviceBinding.bindDetailsData(device: DeviceListItem) {
            device.itemData?.let { details ->

                textViewDataColor.apply {
                    val colorText = details.color ?: details.altColor
                    colorText?.let {
                        text = "Color: ${it}"
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }

                }

                textViewDataCapacity.apply {
                    details.capacityGB?.let {
                        text = "Capacity: ${it} "
                        visibility = View.VISIBLE
                    } ?: details.capacity?.let {
                        text = "Capacity: ${it} GB"
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }

                }

                textViewDataPrice.apply {
                    val price = details.price?.toString() ?: details.altPrice
                    price?.let {
                        text = "Price: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataGeneration.apply {
                    details.generation?.let {
                        text = "Generation: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataYear.apply {
                    details.year?.let {
                        text = "Year: ${it}"
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataCpuModel.apply {
                    details.cpuModel?.let {
                        text = "CPU: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataHardDiskSize.apply {
                    details.hardDiskSize?.let {
                        text = "Hard Disk: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataStrapColour.apply {
                    details.strapColour?.let {
                        text = "Strap color: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataCaseSize.apply {
                    details.caseSize?.let {
                        text = "Case size: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataDescription.apply {
                    details.description?.let {
                        text = "Description: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }

                textViewDataScreenSize.apply {
                    details.screenSize?.let {
                        text = "Screen size: ${it} "
                        visibility = View.VISIBLE
                    } ?: run { visibility = View.GONE }
                }
            } ?: run {
                textViewDataColor.visibility = View.GONE
                textViewDataCapacity.visibility = View.GONE
                textViewDataPrice.visibility = View.GONE
                textViewDataGeneration.visibility = View.GONE
                textViewDataYear.visibility = View.GONE
                textViewDataCpuModel.visibility = View.GONE
                textViewDataHardDiskSize.visibility = View.GONE
                textViewDataStrapColour.visibility = View.GONE
                textViewDataCaseSize.visibility = View.GONE
                textViewDataDescription.visibility = View.GONE
                textViewDataScreenSize.visibility = View.GONE
            }
        }
    }

    class DeviceDiffCallback : DiffUtil.ItemCallback<DeviceListItem>() {
        override fun areItemsTheSame(oldItem: DeviceListItem, newItem: DeviceListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeviceListItem, newItem: DeviceListItem): Boolean {
            return oldItem == newItem
        }
    }
}