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

data class Product(
    var id: String,
    var title: String,
    var articul: String,
    var count: String,
    var inStock: Boolean,
    var isEnabled: Boolean = false,
    var isPicked: Boolean = false
)