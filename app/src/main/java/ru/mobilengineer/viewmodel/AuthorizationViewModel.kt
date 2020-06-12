package ru.mobilengineer.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.Resp
import ru.mobilengineer.data.api.model.request.AuthorizationRequest
import ru.mobilengineer.data.api.model.response.AuthorizationResponse
import java.net.UnknownHostException
import javax.inject.Inject

class AuthorizationViewModel @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var dataLiveData: MutableLiveData<AuthorizationResponse> = MutableLiveData()
    var errorLiveData: MutableLiveData<Int?> = MutableLiveData()

    val emptyResp: Resp = Resp("", "")


    fun authorization(login: String, password: String) {
        viewModelScope.launch {

            try {
                val request = AuthorizationRequest()
                request.login = login
                request.password = password
                val response = apiService.authorization(request)

                if (response.isSuccessful || response.body() != null) {
                    dataLiveData.postValue(response.body())
//                    preferencesManager.token = response.body()?.token
                } else {
                    errorLiveData.postValue(response.code())
                }

            } catch (e: UnknownHostException) {
                errorLiveData.postValue(-100)
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
                errorLiveData.postValue(-200)
                preferencesManager.status = "Exception"
            }
        }
    }
}