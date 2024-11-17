package com.github.cpjinan.plugin.akariitem.common

import com.github.cpjinan.plugin.akariitem.AkariItem.plugin
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.module.metrics.Metrics

object PluginMetrics {
    @Awake(LifeCycle.LOAD)
    fun load() {
        if (PluginConfig.isEnabledSendMetrics()) Metrics(18992, plugin.description.version, Platform.BUKKIT)
    }
}