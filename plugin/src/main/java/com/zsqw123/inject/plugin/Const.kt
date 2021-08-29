package com.zsqw123.inject.plugin

import com.zsqw123.inject.CatInject
import org.objectweb.asm.Type

object Const {
    // Lcom/zsqw123/inject/CatInject;
    val injectAnnotationDescriptor: String = Type.getType(CatInject::class.java).descriptor
}