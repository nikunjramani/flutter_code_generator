package com.flutter_code_generator.flutter_code_generator.setting

import com.flutter_code_generator.flutter_code_generator.helper.BlocTaoData
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent



class SettingsConfigurable : Configurable {
    private val data: BlocTaoData = BlocTaoData.instance
    private var mSetting: SettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Flutter Bloc Setting"
    }

    override fun createComponent(): JComponent {
        mSetting = SettingsComponent()
        return mSetting!!.mainPanel
    }

    override fun isModified(): Boolean {
        val modified = (!mSetting!!.blocName.text.equals(data.blocName)
                || !mSetting!!.cubitName.text.equals(data.cubitName)
                || !mSetting!!.eventName.text.equals(data.eventName)
                || !mSetting!!.viewName.text.equals(data.viewName)
                || !mSetting!!.viewFileName.text.equals(data.viewFileName))
        return modified
    }

    override fun apply() {
        data.blocName = mSetting!!.blocName.text
        data.cubitName = mSetting!!.cubitName.text
        data.eventName = mSetting!!.eventName.text
        data.viewName = mSetting!!.viewName.text
        data.viewFileName = mSetting!!.viewFileName.text
    }

    override fun reset() {
        mSetting!!.blocName.text = data.blocName
        mSetting!!.cubitName.text = data.cubitName
        mSetting!!.eventName.text = data.eventName
        mSetting!!.viewName.text = data.viewName
        mSetting!!.viewFileName.text = data.viewFileName
    }

    override fun disposeUIResources() {
        mSetting = null
    }
}