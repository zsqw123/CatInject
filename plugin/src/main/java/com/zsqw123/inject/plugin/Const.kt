package com.zsqw123.inject.plugin

import com.zsqw123.inject.CatInject
import com.zsqw123.inject.CatInjects
import com.zsqw123.inject.InjectsUtil
import org.objectweb.asm.Type

object Const {
    // Lcom/zsqw123/inject/CatInject;
    val injectAnnotationDescriptor: String = Type.getType(CatInject::class.java).descriptor

    val injectsInternalName: String = Type.getInternalName(CatInjects::class.java)
    val injectsUtilInternalName: String = Type.getInternalName(InjectsUtil::class.java)

    // com.zsqw123.inject.CatInjects
    val injectsQualifiedName: String = CatInjects::class.java.name

    // com.zsqw123.inject.CatInject
    val injectQualifiedName: String = CatInject::class.java.name

    // task: find @CatInject
    val taskCatInjectFind = "findCatInject"
}