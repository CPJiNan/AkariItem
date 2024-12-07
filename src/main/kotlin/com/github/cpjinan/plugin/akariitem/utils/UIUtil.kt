package com.github.cpjinan.plugin.akariitem.utils

import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import com.github.cpjinan.plugin.akariitem.api.ui.UIClickType
import com.github.cpjinan.plugin.akariitem.common.script.kether.Kether.evalKether
import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil.getConfigSections
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Chest

object UIUtil {
    fun getUIFromSettings(config: YamlConfiguration): UI? {
        val settings = getUISettingsFromConfig(config) ?: return null
        val pages = mutableListOf<Inventory>()

        settings.layout.forEach { layoutList ->
            val layout = (layoutList as? List<*>)?.map { it.toString() }?.toTypedArray() ?: return@forEach
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
                    }?.let { icon -> icons.add(icon) }
                }
            }

            pages.add(
                buildMenu<Chest>(settings.title) {
                    map(*layout)
                    icons.forEach { icon ->
                        set(icon.symbol[0], icon.item) {
                            icon.actions?.forEach { (index, action) ->
                                if (UIClickType.equals(clickEvent(), UIClickType.valueOf(index.uppercase()))) {
                                    action.evalKether(clicker)
                                }
                            }
                        }
                    }
                }
            )
        }

        return UI(
            title = settings.title,
            pages = pages,
            freeSlots = settings.freeSlots,
            defaultLayout = settings.defaultLayout,
            hidePlayerInventory = settings.hidePlayerInventory,
            minClickDelay = settings.minClickDelay,
            bindingCommands = settings.bindingCommands,
            bindingItems = settings.bindingItems,
            openCondition = settings.openCondition,
            openActions = settings.openActions,
            openDeny = settings.openDeny,
            closeActions = settings.closeActions
        )
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

        val bindingItems = mutableListOf<ItemStack>()
        config.getStringList("Bindings.Items").forEach {
            if (it.contains("@")) {
                val (plugin, id) = it.split("@", limit = 2)
                when (plugin.lowercase()) {
                    "akariitem" -> {
                        ItemAPI.getItem(id)?.let { item ->
                            bindingItems.add(item)
                        }
                    }
                }
            }
        }

        return UISettings(
            title = config.getString("Title") ?: return null,
            layout = config.getList("Layout") ?: return null,
            freeSlots = freeSlots,
            defaultLayout = config.getInt("Options.Default-Layout"),
            hidePlayerInventory = config.getBoolean("Options.Hide-Player-Inventory"),
            minClickDelay = config.getInt("Options.Min-Click-Delay"),
            bindingCommands = config.getStringList("Bindings.Commands"),
            bindingItems = bindingItems,
            openCondition = config.getStringList("Events.Open.Condition"),
            openActions = config.getStringList("Events.Open.Actions"),
            openDeny = config.getStringList("Events.Open.Deny"),
            closeActions = config.getStringList("Bindings.Close"),
            icons = config.getConfigurationSection("Icons")?.getConfigSections()
        )
    }

    data class UI(
        val title: String,
        val pages: List<Inventory>,
        val freeSlots: List<Int>? = null,
        val defaultLayout: Int = 0,
        val hidePlayerInventory: Boolean = false,
        val minClickDelay: Int = -1,
        val bindingCommands: List<String>? = null,
        val bindingItems: List<ItemStack>? = null,
        val openCondition: List<String>? = null,
        val openActions: List<String>? = null,
        val openDeny: List<String>? = null,
        val closeActions: List<String>? = null
    )

    data class UISettings(
        val title: String,
        val layout: List<*>,
        val freeSlots: List<Int>? = null,
        val defaultLayout: Int = 0,
        val hidePlayerInventory: Boolean = false,
        val minClickDelay: Int = -1,
        val bindingCommands: List<String>? = null,
        val bindingItems: List<ItemStack>? = null,
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