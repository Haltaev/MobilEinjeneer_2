package ru.mobilengineer

import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse

interface OnNameEngineerItemClickListener {
    fun onNameEngineerItemClicked(anotherEngineers: AnotherEngineersResponse)
}