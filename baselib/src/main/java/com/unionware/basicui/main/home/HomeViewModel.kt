package com.unionware.basicui.main.home

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.UserApi
import unionware.base.app.viewmodel.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userApi: UserApi,
) :
    BaseViewModel() {

    private val HOME_PAGE_INDEX = "home_page_index"
    val mLiveData = MutableLiveData(0)

    fun getSelected(): LiveData<Int> {
        //从缓存中读取，防止Activity因内存不知等原因被回收重建后，Fragment重叠问题
//        if (mLiveData.value == null) {
//            val index = savedStateHandle[HOME_PAGE_INDEX] ?: 0
//            mLiveData.postValue(index)
//        }
        return mLiveData
    }

    //保存每一次的下标选中
    fun saveSelect(selectIndex: Int) {
//        savedStateHandle[HOME_PAGE_INDEX] = selectIndex
        mLiveData.postValue(selectIndex)
    }
}