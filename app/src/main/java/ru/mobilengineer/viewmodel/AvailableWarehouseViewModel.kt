package ru.mobilengineer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.request.MovingAcceptRequest
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.data.api.model.response.MovingAcceptResponse
import java.net.UnknownHostException
import javax.inject.Inject

class AvailableWarehouseViewModel @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var dataLiveData: MutableLiveData<ArrayList<IncommingSkuFromAnyResponse>> = MutableLiveData()
    var movingAcceptLiveData: MutableLiveData<MovingAcceptResponse> = MutableLiveData()
    var cancelTransferLiveData: MutableLiveData<Any> = MutableLiveData()
    var errorLiveData: MutableLiveData<Int?> = MutableLiveData()


    fun getIncommingSkuFromAll(filterMode: String, fromWho: String) {
        viewModelScope.launch {

            try {
                val response = apiService.getIncommingSkuFromAll(filterMode, fromWho)

                Log.e("response", response.message())
                Log.e("response", response.code().toString())

                if (response.isSuccessful || response.body() != null) {
                    dataLiveData.postValue(response.body())
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

    fun cancelTransfer(listOfMovingId: ArrayList<String>) {
        viewModelScope.launch {

            try {
                for(movingId in listOfMovingId) {
                   val response =  apiService.movingCancel(movingId)
                    if (!response.isSuccessful) {
                        errorLiveData.postValue(response.code())
                    }
                }
                cancelTransferLiveData.postValue("ok")

            } catch (e: UnknownHostException) {
                errorLiveData.postValue(-100)
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
                errorLiveData.postValue(-200)
                preferencesManager.status = "Exception"
            }
        }
    }

    fun putItemsToMyWarehouse(actionWithItem: ArrayList<Pair<Int, MovingAcceptRequest>>) {
        viewModelScope.launch {

            try {
                if (actionWithItem.size == 1) {
                    val response = apiService.movingAccept2(
                        actionWithItem[0].first.toString(),
                        arrayListOf(actionWithItem[0].second)
                    )
                    if (response.isSuccessful) {
                        movingAcceptLiveData.postValue(response.body())
                    } else {
                        errorLiveData.postValue(response.code())
                    }
                } else {
                    for (item in actionWithItem) {
                        val response = apiService.movingAccept2(
                            item.first.toString(),
                            arrayListOf(item.second)
                        )
                        if (!response.isSuccessful) {
                            errorLiveData.postValue(response.code())
                        }
                    }
                    movingAcceptLiveData.postValue(MovingAcceptResponse())
                }
            } catch (e: UnknownHostException) {
//                errorLiveData.postValue(-100)
                preferencesManager.status = "UnknownHostException"
            } catch (e: Exception) {
//                errorLiveData.postValue(-200)
                preferencesManager.status = "Exception"
            }
        }
    }
}