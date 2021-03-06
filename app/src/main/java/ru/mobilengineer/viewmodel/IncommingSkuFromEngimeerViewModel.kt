package ru.mobilengineer.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.ApiService
import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.data.api.model.response.WarehousesResponse
import java.net.UnknownHostException
import javax.inject.Inject

class IncommingSkuFromEngimeerViewModel  @Inject constructor(var context: Context) : ViewModel() {
    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var dataLiveData: MutableLiveData<ArrayList<IncommingSkuFromAnyResponse>> = MutableLiveData()
    var errorLiveData: MutableLiveData<Int?> = MutableLiveData()


    fun getIncommingSkuFromWarehouse(engineer: AnotherEngineersResponse) {
        viewModelScope.launch {

            try {
                val response = apiService.getIncommingSkuFromEngineers("users")

                Log.e("response", response.message())
                Log.e("response", response.code().toString())


                if (response.isSuccessful || response.body() != null) {
//                    dataLiveData.postValue(response.body())
                    val arrayList = arrayListOf<IncommingSkuFromAnyResponse>()
                    response.body()?.let {
                        for(i in it){
                            if(i.warehouseId == engineer.warehouseId)
                                arrayList.add(i)
                        }
                        dataLiveData.postValue(arrayList)
                    }
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