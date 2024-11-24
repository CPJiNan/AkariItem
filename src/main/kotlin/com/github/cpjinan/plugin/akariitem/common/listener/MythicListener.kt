package com.github.cpjinan.plugin.akariitem.common.listener

import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import ink.ptms.um.event.MobDropLoadEvent
import taboolib.common.platform.event.SubscribeEvent

object MythicListener {
    @SubscribeEvent
    fun onMobDrop(event: MobDropLoadEvent) {
        val (plugin, id) = event.dropName.split("@", limit = 2)

        if (plugin.equals("akariitem", ignoreCase = true)) {
            ItemAPI.getItem(id)?.let { item -> event.registerItem { item } }
        }
    }
}