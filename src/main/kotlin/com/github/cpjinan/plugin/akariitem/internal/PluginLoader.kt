package com.github.cpjinan.plugin.akariitem.internal

import com.github.cpjinan.plugin.akariitem.AkariItem.plugin
import com.github.cpjinan.plugin.akariitem.api.UIAPI.openUI
import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil.saveDefaultResource
import com.github.cpjinan.plugin.akariitem.utils.FileUtil
import com.github.cpjinan.plugin.akariitem.utils.LoggerUtil
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.simpleCommand
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import java.io.File

object PluginLoader {
    @Awake(LifeCycle.LOAD)
    fun onLoad() {
        console().sendLang("Plugin-Loading", plugin.description.version)
    }

    @Awake(LifeCycle.ENABLE)
    fun onEnable() {
        LoggerUtil.message(
            "",
            "&o     _    _              _ ___ _                  ".colored(),
            "&o    / \\  | | ____ _ _ __(_)_ _| |_ ___ _ __ ___   ".colored(),
            "&o   / _ \\ | |/ / _` | '__| || || __/ _ \\ '_ ` _ \\  ".colored(),
            "&o  / ___ \\|   < (_| | |  | || || ||  __/ | | | | | ".colored(),
            "&o /_/   \\_\\_|\\_\\__,_|_|  |_|___|\\__\\___|_| |_| |_| ".colored(),
            ""
        )
        console().sendLang("Plugin-Enabled")
        plugin.saveDefaultResource("ui/Example.yml")
        simpleCommand("test") { sender, _ ->
            sender.castSafely<Player>()
                ?.openUI(YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, "ui/Example.yml")))
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun onDisable() {
        console().sendLang("Plugin-Disable")
    }

}