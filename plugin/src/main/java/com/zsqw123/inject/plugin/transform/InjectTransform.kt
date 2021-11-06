package com.zsqw123.inject.plugin.transform

import com.zsqw123.inject.plugin.transform.process.*

class InjectTransform : BaseTransform(), FileProcess by InjectProcess.convertToFileProcess()
class InjectImplTransform : BaseTransform(), FileProcess by InjectImplProcess.convertToFileProcess()
class InjectsTransform : BaseTransform(), FileProcess by InjectsProcess.convertToFileProcess()