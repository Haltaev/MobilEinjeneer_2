package ru.mobilengineer.data.api.model



data class Resp(
    var status: String,
    var data: Any
)

data class ModuleData(
    var id: String,
    var employee_name: String,
    var employee_salary: String,
    var employee_age: String,
    var profile_image: String
)

data class ItemParams(
    var title: String,
    var quantity: String,
    var vendorCode: String?
)

data class Warehouse(
    var warehouseId: Int? = null,
    var quantity: Int? = null,
    var skuId: Int? = null,
    var name: String? = null,
    var vendorCode: String? = null,
    var isEnabled: Boolean = false,
    var inStock: Boolean,
    var serialNumbers: ArrayList<SerialNumber> = arrayListOf(),
    var isPicked: Boolean = false

)

data class Product(
    var id: String,
    var title: String,
    var articul: String,
    var count: String,
    var inStock: Boolean,
    var serialNumbers: ArrayList<SerialNumber> = arrayListOf(),
    var isEnabled: Boolean = false,
    var isPicked: Boolean = false
)

data class SerialNumber(
    var title: String,
    var isEnabled: Boolean = false,
    var isPicked: Boolean = false
)