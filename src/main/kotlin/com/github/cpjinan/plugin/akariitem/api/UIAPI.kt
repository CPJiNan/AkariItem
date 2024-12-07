package com.github.cpjinan.plugin.akariitem.api

import com.github.cpjinan.plugin.akariitem.utils.UIUtil
import com.github.cpjinan.plugin.akariitem.utils.UIUtil.openUIFromConfig
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

object UIAPI {
    /**
     * 从配置文件构建 UI 界面
     * @param config 用于构建 UI 界面的配置文件
     * @author CPJiNan
     */
    @JvmStatic
    fun buildUI(config: YamlConfiguration): Inventory? = UIUtil.buildUIFromConfig(config)

    /**
     * 为玩家打开指定 UI 界面
     * @param config 用于构建 UI 界面的配置文件
     * @author CPJiNan
     */
    @JvmStatic
    fun Player.openUI(config: YamlConfiguration) {
        this.openUIFromConfig(config)
    }

    /**
     * 为玩家关闭当前 UI 界面
     * @param type 要关闭的 UI 界面种类 (默认为 null)
     * @author CPJiNan
     */
    @JvmStatic
    fun Player.closeUI(type: InventoryType? = null) {
        player?.openInventory?.run {
            if (type == null || this.type == type) player.closeInventory()
        }
    }
}