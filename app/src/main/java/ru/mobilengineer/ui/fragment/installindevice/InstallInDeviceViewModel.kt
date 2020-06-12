package ru.mobilengineer.ui.fragment.installindevice

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.response.DeviceResponse
import ru.mobilengineer.data.api.model.response.InstallCartridgeResponse
import java.net.UnknownHostException
import javax.inject.Inject

class InstallInDeviceViewModel @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var dataLiveData: MutableLiveData<ArrayList<DeviceResponse>> = MutableLiveData()
    var errorLiveData: MutableLiveData<Int?> = MutableLiveData()


    fun getDeviceInfo(serialNumber: String) {
        viewModelScope.launch {

            try {
                val response = apiService.getDeviceInfo(serialNumber)

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
}