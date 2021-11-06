package com.zsqw123.inject.plugin.transform.process

import com.zsqw123.inject.plugin.InjectImpl

class ProcessingData {
    // 所有被 @CatInject 注解的接口
    internal val injectInterfaceInternalNames = HashSet<String>()

    // 所有被实现了 injectInterfaceInternalNames 中接口的类
    internal val injectImpls = ArrayList<InjectImpl>()
}