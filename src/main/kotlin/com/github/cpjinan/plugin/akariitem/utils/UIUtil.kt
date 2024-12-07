package com.github.cpjinan.plugin.akariitem.utils

import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import com.github.cpjinan.plugin.akariitem.api.ui.UIClickType
import com.github.cpjinan.plugin.akariitem.common.script.kether.Kether.evalKether
import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil.getConfigSections
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
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
        val inventory = buildUIFromConfig(config, onFinish) ?: return
        this.openInventory(inventory)
    }

    @JvmStatic
    fun buildUIFromConfig(
        config: YamlConfiguration,
        onFinish: (ui: Chest, icons: MutableList<Icon>) -> Unit = { _: Chest, _: MutableList<Icon> -> }
    ): Inventory? {
        val settings = getUISettingsFromConfig(config) ?: return null
        val icons = mutableListOf<Icon>()

        settings.icons?.forEach { (index, section) ->
            val actions = hashMapOf<String, List<String>>()
            section.getConfigSections().forEach { action ->
                actions[action.key] = section.getStringList(action.key)
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

        return buildMenu<Chest>(settings.title) {
            map(*settings.layout.toTypedArray())

            icons.forEach { icon ->
                set(icon.symbol[0], icon.item) {
                    if (settings.freeSlots != null && clickEvent().slot in settings.freeSlots) isCancelled = true
                    icon.actions?.forEach { (index, action) ->
                        if (index == "ALL") action.evalKether(clicker)
                        else if (UIClickType.equals(clickEvent(), UIClickType.valueOf(index.uppercase()))) {
                            action.evalKether(clicker)
                        }
                    }
                }
            }

            onBuild(async = true) { player, _ ->
                var matchCondition = true
                settings.openCondition?.forEach {
                    if (!(it.evalKether(player) as Boolean)) matchCondition = false
                }
                if (matchCondition) {
                    settings.openActions?.evalKether(player)
                } else settings.openDeny?.evalKether(player)
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
            icons = config.getConfigurationSection("Icons")?.getConfigSections()
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
        val icons: HashMap<String, ConfigurationSection>? = null
    )

    data class Icon(
        val symbol: String,
        val item: ItemStack,
        val actions: HashMap<String, List<String>>? = null
    )
}