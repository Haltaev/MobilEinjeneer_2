package ru.mobilengineer

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.Resp
import java.net.UnknownHostException
import javax.inject.Inject

class MainViewModel @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var dataLiveData: MutableLiveData<Pair<Resp, String>> = MutableLiveData()

    val emptyResp: Resp = Resp("", "")


    fun getContacts() {
        viewModelScope.launch {

//            try {
//                val response = apiService.authorization()
//
//                preferencesManager.status = response.body()?.status
//
//                if (!response.isSuccessful || response.body() == null) {
//                    dataLiveData.postValue(null)
//                } else
//                    dataLiveData.postValue(Pair(response.body()!!, ""))
//
//            } catch (e: UnknownHostException) {
//                dataLiveData.postValue(Pair(emptyResp, "UnknownHostException"))
//                preferencesManager.status = "UnknownHostException"
//            } catch (e: Exception) {
//                dataLiveData.postValue(Pair(emptyResp, "Exception"))
//                preferencesManager.status = "Exception"
//
//            }
        }
    }
}