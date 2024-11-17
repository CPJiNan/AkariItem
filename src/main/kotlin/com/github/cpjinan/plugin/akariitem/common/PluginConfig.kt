package com.github.cpjinan.plugin.akariitem.common

import com.github.cpjinan.plugin.akariitem.AkariItem.plugin
import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akariitem.utils.FileUtil
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.io.File

object PluginConfig {
    var settingsFile = File(FileUtil.dataFolder, "settings.yml")
    var settings: YamlConfiguration = YamlConfiguration()

    init {
        reloadConfig()
    }

    fun reloadConfig() {
        settings = YamlConfiguration.loadConfiguration(settingsFile)
        ItemAPI.reloadItem()
    }

    fun getConfigVersion() = settings.getInt("Options.Config-Version")
    fun isEnabledCheckUpdate() = settings.getBoolean("Options.Check-Update")
    fun isEnabledSendMetrics() = settings.getBoolean("Options.Send-Metrics")
    fun isEnabledOPNotify() = settings.getBoolean("Options.OP-Notify")
    fun isEnabledDebug() = settings.getBoolean("Options.Debug")

    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        plugin.saveDefaultResource(
            "settings.yml"
        )
        plugin.saveDefaultResource(
            "item/Example.yml"
        )
        reloadConfig()
    }
}