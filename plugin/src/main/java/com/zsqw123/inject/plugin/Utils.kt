package com.zsqw123.inject.plugin

internal typealias InjectImplsMap = Map<String, List<String>>

internal fun pluginLog(msg: String) = println("CatInjectTransform---->$msg")

internal fun String.toClassName() = replace('/', '.')

internal fun printMappedInterfaceAndImpls(map: InjectImplsMap) {
    map.forEach { (k, v) -> pluginLog("$k : $v") }
}