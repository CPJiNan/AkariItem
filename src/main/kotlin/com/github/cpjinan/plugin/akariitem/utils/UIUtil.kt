package com.github.cpjinan.plugin.akariitem.utils

import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import com.github.cpjinan.plugin.akariitem.api.ui.UIClickType
import com.github.cpjinan.plugin.akariitem.common.PluginConfig
import com.github.cpjinan.plugin.akariitem.common.script.kether.Kether.evalKether
import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil.getConfigSections
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Chest

object UIUtil {
    @JvmStatic
    fun Player.openUIFromConfig(
        config: YamlConfiguration,
        onFinish: (ui: Chest, icons: MutableList<Icon>) -> Unit = { _: Chest, _: MutableList<Icon> -> }
    ) {
        val inventory = buildUIFromConfig(this, config, onFinish) ?: return
        this.openInventory(inventory)
    }

    @JvmStatic
    fun buildUIFromConfig(
        player: Player,
        config: YamlConfiguration,
        onFinish: (ui: Chest, icons: MutableList<Icon>) -> Unit = { _: Chest, _: MutableList<Icon> -> }
    ): Inventory? {
        val settings = getUISettingsFromConfig(config) ?: return null
        val icons = mutableListOf<Icon>()

        var matchCondition = true
        settings.openCondition?.forEach {
            if (!(it.evalKether(player) as Boolean)) matchCondition = false
        }
        if (!matchCondition) {
            settings.openDeny?.evalKether(player)
            return null
        }

        settings.icons?.forEach { (index, section) ->
            val actions = hashMapOf<String, List<String>>()
            section.getConfigurationSection("Actions")!!.getKeys(false).forEach { type ->
                actions[type] = section.getStringList("Actions.$type")
            }
            section.getConfigurationSection("Display")?.let { itemSection ->
                ItemAPI.getItem(itemSection)?.let { item ->
                    Icon(
                        symbol = index,
                        item = item,
                        actions = actions
                    )
                }?.let { icon ->
                    icons.add(icon)
                }
            }
        }

        val freeSlots = settings.freeSlots ?: mutableListOf()

        return buildMenu<Chest>(settings.title) {
            map(*settings.layout.toTypedArray())

            handLocked(false)

            icons.forEach { icon ->
                set(icon.symbol[0], icon.item) {
                    icon.actions?.forEach { (index, action) ->
                        if (clickEvent().slot in freeSlots) isCancelled = false
                        if (index.uppercase() == "ALL") action.evalKether(clicker)
                        else if (UIClickType.equals(clickEvent(), UIClickType.valueOf(index.uppercase()))) {
                            action.evalKether(clicker)
                        }
                    }
                }
            }

            onClick { event ->
                when {
                    event.clickEventOrNull() != null -> {
                        if (event.clickEvent().click == ClickType.DOUBLE_CLICK) event.isCancelled = true
                        event.clickEvent().slot.let {
                            if (it !in freeSlots) event.isCancelled = true
                            if (PluginConfig.isEnabledDebug()) event.clicker.sendMessage("Type: clickEvent, Slot: $it")
                        }
                    }

                    event.dragEventOrNull() != null -> {
                        event.dragEvent().inventorySlots.let {
                            if (!freeSlots.containsAll(it)) event.isCancelled = true
                            if (PluginConfig.isEnabledDebug()) event.clicker.sendMessage("Type: dragEvent, Slot: $it")
                        }
                    }

                    event.virtualEventOrNull() != null -> {
                        event.virtualEvent().clickSlot.let {
                            if (it !in freeSlots) event.isCancelled = true
                            if (PluginConfig.isEnabledDebug()) event.clicker.sendMessage("Type: virtualEvent, Slot: $it")
                        }
                    }
                }
            }

            onBuild(async = false) { player, _ ->
                settings.openActions?.evalKether(player)
            }

            onClose(once = true) { event ->
                settings.closeActions?.forEach {
                    it.evalKether(event.player)
                }
            }

            onFinish(this, icons)
        }
    }

    fun getUISettingsFromConfig(config: YamlConfiguration): UISettings? {
        val freeSlots = mutableListOf<Int>()

        config.getStringList("Options.Free-Slots").forEach {
            if (it.contains("~")) {
                val (start, end) = it.split("~", limit = 2)
                start.toIntOrNull()?.let { startInt ->
                    end.toIntOrNull()?.let { endInt ->
                        for (i in startInt..endInt) {
                            freeSlots.add(i)
                        }
                    }
                }
                freeSlots.sort()
            } else it.toIntOrNull()?.let { freeSlots.add(it) }
        }

        return UISettings(
            title = config.getString("Title") ?: return null,
            layout = config.getStringList("Layout"),
            freeSlots = freeSlots,
            openCondition = config.getStringList("Events.Open.Condition"),
            openActions = config.getStringList("Events.Open.Actions"),
            openDeny = config.getStringList("Events.Open.Deny"),
            closeActions = config.getStringList("Bindings.Close"),
            icons = config.getConfigurationSection("Icons")?.getConfigSections(),
            config = config
        )
    }

    data class UISettings(
        val title: String,
        val layout: List<String>,
        val freeSlots: List<Int>? = null,
        val openCondition: List<String>? = null,
        val openActions: List<String>? = null,
        val openDeny: List<String>? = null,
        val closeActions: List<String>? = null,
        val icons: HashMap<String, ConfigurationSection>? = null,
        val config: YamlConfiguration
    )

    data class Icon(
        val symbol: String,
        val item: ItemStack,
        val actions: HashMap<String, List<String>>? = null
    )
}