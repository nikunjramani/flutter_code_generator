package com.flutter_code_generator.flutter_code_generator.live_templates

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider


class BlocTemplateProvider : DefaultLiveTemplatesProvider {
    override fun getDefaultLiveTemplateFiles(): Array<String> {
        return arrayOf("liveTemplates/provider")
    }

    override fun getHiddenLiveTemplateFiles(): Array<String>? {
        return null
    }
}
