package com.zsqw123.inject.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.zsqw123.inject.plugin.transform.InjectImplTransform
import com.zsqw123.inject.plugin.transform.InjectTransform
import com.zsqw123.inject.plugin.transform.InjectsTransform
import com.zsqw123.inject.plugin.transform.process.ProcessingData
import org.gradle.api.Plugin
import org.gradle.api.Project

typealias IP = InjectPlugin

class InjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val androidAppExtension = target.extensions.findByType(AppExtension::class.java)
        val androidLibExtension = target.extensions.findByType(LibraryExtension::class.java)
        if (androidAppExtension == null && androidLibExtension == null) return
        data = ProcessingData()
        val injectTransform = arrayOf(InjectTransform(), InjectImplTransform(), InjectsTransform())
        injectTransform.forEach {
            androidAppExtension?.registerTransform(it)
            androidLibExtension?.registerTransform(it)
        }
        println("CatInject Plugin Loaded!")
    }

    companion object {
        var data = ProcessingData()
            private set
    }
}