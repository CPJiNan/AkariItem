package com.github.cpjinan.plugin.akariitem.api

import com.github.cpjinan.plugin.akariitem.utils.UIUtil
import com.github.cpjinan.plugin.akariitem.utils.UIUtil.Icon
import com.github.cpjinan.plugin.akariitem.utils.UIUtil.openUIFromConfig
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import taboolib.module.ui.type.Chest

object UIAPI {
    /**
     * 从配置文件构建 UI 界面
     * @param config 用于构建 UI 界面的配置文件
     * @author CPJiNan
     */
    @JvmStatic
    fun buildUI(
        config: YamlConfiguration,
        onFinish: (ui: Chest, icons: MutableList<Icon>) -> Unit = { _: Chest, _: MutableList<Icon> -> }
    ): Inventory? = UIUtil.buildUIFromConfig(config, onFinish)

    /**
     * 为玩家打开指定 UI 界面
     * @param config 用于构建 UI 界面的配置文件
     * @author CPJiNan
     */
    @JvmStatic
    fun Player.openUI(
        config: YamlConfiguration,
        onFinish: (ui: Chest, icons: MutableList<Icon>) -> Unit = { _: Chest, _: MutableList<Icon> -> }
    ) {
        this.openUIFromConfig(config, onFinish)
    }
}