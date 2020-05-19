package ru.mobilengineer.common

interface MediaListener {
    fun showLoading()
    fun hideLoading()
    fun success(photoPath: String)
}