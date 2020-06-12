package ru.mobilengineer

import ru.mobilengineer.data.api.model.response.WarehousesResponse

interface OnTargetItemClickListener {
    fun onTargetItemClicked(warehouse: WarehousesResponse)
}