package ru.mobileinjeneer.data.api.model

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