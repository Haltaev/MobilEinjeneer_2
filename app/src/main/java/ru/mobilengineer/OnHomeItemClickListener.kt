package ru.mobilengineer



interface OnHomeItemClickListener {
    fun onHomeItemClicked(count: Int?)
    fun onHomeItemLongClicked(isAnyItemPicked: Boolean)
}