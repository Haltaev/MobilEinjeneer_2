package ru.mobilengineer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.Resp
import ru.mobilengineer.data.api.model.response.SelfInfoResponse
import java.net.UnknownHostException
import javax.inject.Inject

class MyProfileViewModel @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var selfInfoLiveData: MutableLiveData<SelfInfoResponse> = MutableLiveData()
    var myWarehouseLiveData: MutableLiveData<Int> = MutableLiveData()
    var availableFromUsersLiveData: MutableLiveData<Int> = MutableLiveData()
    var availableFromWarehousesLiveData: MutableLiveData<Int> = MutableLiveData()

    var errorLiveData: MutableLiveData<String?> = MutableLiveData()
    val emptyResp: Resp = Resp("", "")


    fun getSelfInfo() {
        viewModelScope.launch {

            try {
                val response = apiService.getSelfInfo()

                if (response.isSuccessful || response.body() != null) {
                    selfInfoLiveData.postValue(response.body())
                } else {
                    errorLiveData.postValue(response.body())
                }

            } catch (e: UnknownHostException) {
//                dataLiveData.postValue(Pair(emptyResp, "UnknownHostException"))
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
//                dataLiveData.postValue(Pair(emptyResp, "Exception"))
                preferencesManager.status = "Exception"

            }
        }
    }

    fun getMyStock() {
        viewModelScope.launch {

            try {
                val response = apiService.getMyStock("n")

                Log.e("response", response.message())
                Log.e("response", response.code().toString())

                var quantity = 0
                response.body()?.forEach {item ->
                    item.quantity?.let {
                        quantity += it
                    }
                }

                if (response.isSuccessful || response.body() != null) {
                    myWarehouseLiveData.postValue(quantity)
                } else {
                    myWarehouseLiveData.postValue(response.code())
                }

            } catch (e: UnknownHostException) {
                myWarehouseLiveData.postValue(-100)
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
                myWarehouseLiveData.postValue(-200)
                preferencesManager.status = "Exception"
            }
        }
    }

    fun getAvailableFromUsers() {
        viewModelScope.launch {

            try {
                val response
                        = apiService.getIncommingSkuFromAll("n", "users")

                Log.e("response", response.message())
                Log.e("response", response.code().toString())

                var quantity = 0
                response.body()?.forEach {item ->
                    item.quantity?.let {
                        quantity += it
                    }
                }

                if (response.isSuccessful || response.body() != null) {
                    availableFromUsersLiveData.postValue(quantity)
                }else {
                    availableFromUsersLiveData.postValue(response.code())
                }
            } catch (e: UnknownHostException) {
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
                preferencesManager.status = "Exception"
            }
        }
    }

    fun getAvailableFromWarehouses() {
        viewModelScope.launch {

            try {
                val response
                        = apiService.getIncommingSkuFromAll("n", "warehouses")

                Log.e("response", response.message())
                Log.e("response", response.code().toString())

                var quantity = 0
                response.body()?.forEach {item ->
                    item.quantity?.let {
                        quantity += it
                    }
                }

                if (response.isSuccessful || response.body() != null) {
                    availableFromWarehousesLiveData.postValue(quantity)
                }else {
                    availableFromWarehousesLiveData.postValue(response.code())
                }
            } catch (e: UnknownHostException) {
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
                preferencesManager.status = "Exception"
            }
        }
    }
}
