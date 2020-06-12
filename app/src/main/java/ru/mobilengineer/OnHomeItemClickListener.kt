package ru.mobilengineer

import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.data.api.model.response.MyStockResponse


interface OnHomeItemClickListener {
    fun onHomeItemClicked(count: Int?, position: Int, isIncreased: Boolean = false)
    fun onHomeItemLongClicked(isAnyItemPicked: Boolean, position: Int)
    fun isConsiderSerialNumber(): Boolean
    fun onWarehouseItemClicked(item: IncommingSkuFromAnyResponse, position: Int)
}