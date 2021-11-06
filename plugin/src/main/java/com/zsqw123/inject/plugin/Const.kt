package com.zsqw123.inject.plugin

import com.zsqw123.inject.CatInject
import com.zsqw123.inject.CatInjects
import org.objectweb.asm.Type

object Const {
    // Lcom/zsqw123/inject/CatInject;
    val injectAnnotationDescriptor: String = Type.getType(CatInject::class.java).descriptor

    // com.zsqw123.inject.CatInjects
    val injectsQualifiedName: String = CatInjects::class.java.name

    // com.zsqw123.inject.CatInject
    val injectQualifiedName: String = CatInject::class.java.name

    // task: find @CatInject
    val taskCatInjectFind = "findCatInject"
}