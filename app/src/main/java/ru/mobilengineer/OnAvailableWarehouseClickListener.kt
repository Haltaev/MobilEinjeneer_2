package ru.mobilengineer

import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse

interface OnAvailableWarehouseClickListener {
    fun onHomeItemClicked(count: Int?, position: Int, isIncreased: Boolean = false)
    fun onHomeItemLongClicked(isAnyItemPicked: Boolean, position: Int)
    fun isConsiderSerialNumber(): Boolean
    fun onItemRefuseClicked(item: IncommingSkuFromAnyResponse, position: Int)
    fun onItemReceiveClicked(item: IncommingSkuFromAnyResponse, position: Int)
}