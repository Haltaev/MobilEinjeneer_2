package ru.mobilengineer.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.request.MovingAcceptRequest
import ru.mobilengineer.data.api.model.response.MovingAcceptResponse
import java.net.UnknownHostException
import javax.inject.Inject

class TransferFromEngineerViewModel @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var dataLiveData: MutableLiveData<MovingAcceptResponse> = MutableLiveData()
    var errorLiveData: MutableLiveData<Int?> = MutableLiveData()


    fun putItemsToMyWarehouse(actionWithItem: ArrayList<Pair<Int, MovingAcceptRequest>>) {
        viewModelScope.launch {

            try {
//                val response =
                if (actionWithItem.size == 1) {
                    val response = apiService.movingAccept1(
                        actionWithItem[0].first.toString(),
                        actionWithItem[0].second.quantity.toString(),
                        arrayListOf(actionWithItem[0].second)
                    )
                    if (response.isSuccessful) {
                        dataLiveData.postValue(response.body())
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
                    dataLiveData.postValue(MovingAcceptResponse())
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