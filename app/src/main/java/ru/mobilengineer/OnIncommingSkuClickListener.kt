package ru.mobilengineer

import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse

interface OnIncommingSkuClickListener {
    fun onHomeItemClicked(count: Int?, position: Int, isIncreased: Boolean = false)
    fun onHomeItemLongClicked(isAnyItemPicked: Boolean, position: Int)
    fun isConsiderSerialNumber(): Boolean
    fun onTakeAwayButtonClicked(item: IncommingSkuFromAnyResponse, position: Int)
}