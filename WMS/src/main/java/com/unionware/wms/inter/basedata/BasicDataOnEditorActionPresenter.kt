package com.unionware.wms.inter.basedata

import com.unionware.wms.api.PackingApi
import unionware.base.app.view.base.mvp.BasePresenter
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.BinCodeInfoBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

/**
 * @Author : pangming
 * @Time : On 2024/8/30 11:02
 * @Description : BasicDataOnEditorActionPresenter
 */

class BasicDataOnEditorActionPresenter @Inject constructor(val api: PackingApi) :
    BasePresenter<BasicDataOnEditorActionContract.View>(), BasicDataOnEditorActionContract.Presenter {
    override fun queryBasicData(scene: String?, name: String?, filters: FiltersReq?, position:Int) {
        NetHelper.request(
            api.getBaseInfoList(scene, name, filters),
            mView,
            object : ICallback<CommonListDataResp<BaseInfoBean>> {
                override fun onSuccess(data: CommonListDataResp<BaseInfoBean>?) {
                    //基础数据
                    mView.showBasicDataList(data?.data,position)
                }

                override fun onFailure(e: ApiException?) {
                    mView.showFailedView(e?.errorMsg)
                }
            })
    }

    override fun queryBinCodeData(scene: String?, filters: FiltersReq?, position: Int,pKey: String) {
        NetHelper.request(
            api.getBinCodeInfoList(scene, "UNW_WMS_BINCODE", filters),
            mView,
            object : ICallback<CommonListDataResp<BinCodeInfoBean>> {
                override fun onSuccess(data: CommonListDataResp<BinCodeInfoBean>?) {
                    var list: List<BinCodeInfoBean>? = data?.data
                    var baseList: MutableList<BaseInfoBean>? = mutableListOf()
                    list?.let {
                        it[0].stock?.let {
                            var item:BaseInfoBean = BaseInfoBean()
                            item.id =  it.id
                            item.code =  it.code
                            item.name = it.name
                            item.fStockFlexItem = it.fStockFlexItem
                            it.isLocManaged = it.isLocManaged
                            baseList?.add(item)
                        }
                        it[0].stockLoc?.let {
                            for(index in 0 until it.size){
                                var item:BaseInfoBean = BaseInfoBean()
                                for ( keyName:String in it[index].keys){
                                    //区分调入调出仓位
                                    //keyName 返回的只会包含"FStockLocId.FF"
                                    if(keyName.contains(pKey)){
                                        item.key = keyName
                                    }else{
                                        if(pKey == "FStockLocId.FF"){
                                            item.key = "FStockLocId.FF"+keyName.replace("FInStockLocId.FF","")
                                        }else{
                                            item.key = "FInStockLocId.FF"+keyName.replace("FStockLocId.FF","")
                                        }
                                    }
                                    //赋值其它参数
                                    it[index].get(keyName)?.let {
                                        for (name:String in it.keys){
                                            if("id" == name){
                                                item.id = it.get(name).toString()
                                            }else if("code" == name){
                                                item.code  = it.get(name) as String?
                                            }else if("name" == name){
                                                item.name  = it.get(name) as String?
                                            }
                                        }
                                    }
                                    baseList?.add(item)
                                }
                            }
                        }
                    }

                    //基础数据
                    mView.showBasicDataList(baseList,position)
                }

                override fun onFailure(e: ApiException?) {
                    mView.showFailedView(e?.errorMsg)
                }
            })
    }

}