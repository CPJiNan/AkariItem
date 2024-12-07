package com.github.cpjinan.plugin.akariitem.api

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

object UIAPI {
    /**
     * 为玩家关闭当前 UI 界面
     * @param type 要关闭的 UI 界面种类 (默认为 null)
     * @author CPJiNan
     */
    fun Player.closeUI(type: InventoryType? = null) {
        player?.openInventory?.run {
            if (type == null || this.type == type) player.closeInventory()
        }
    }
}