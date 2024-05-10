package com.flutter_code_generator.flutter_code_generator.helper

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

//custom save location
@State(name = "BlocTaoData", storages = [Storage(value = "BlocTaoData.xml")])
class BlocTaoData : PersistentStateComponent<BlocTaoData> {
    // 0:default  1:high   2:extended
    var defaultMode: Int = BlocConfig.defaultMode


    //Logical layer name
    var blocName: String? = BlocConfig.blocName
    var cubitName: String? = BlocConfig.cubitName

    //view layer name
    var viewName: String? = BlocConfig.viewName
    var viewFileName: String? = BlocConfig.viewFileName

    //event layer name
    var eventName: String? = BlocConfig.eventName

    override fun getState(): BlocTaoData {
        return this
    }

    override fun loadState(state: BlocTaoData) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: BlocTaoData
            get() = ServiceManager.getService(BlocTaoData::class.java)
    }
}

