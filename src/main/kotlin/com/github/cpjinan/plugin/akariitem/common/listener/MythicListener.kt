package com.github.cpjinan.plugin.akariitem.common.listener

import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import ink.ptms.um.event.MobDropLoadEvent
import taboolib.common.platform.event.SubscribeEvent

object MythicListener {
    @SubscribeEvent
    fun onMobDrop(event: MobDropLoadEvent) {
        event.dropName.split("@", limit = 2).takeIf { it.size == 2 }?.let {
            if (it[0].equals("akariitem", ignoreCase = true)) {
                ItemAPI.getItem(it[1])?.let { item -> event.registerItem { item } }
            }
        }
    }
}