package com.github.cpjinan.plugin.akariitem.utils

import github.saukiya.sxattribute.SXAttribute
import ink.ptms.um.Mythic
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import pers.neige.neigeitems.manager.ItemManager
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.*
import taboolib.platform.util.buildItem

@Suppress("DEPRECATION")
object ItemUtil {
    @JvmStatic
    fun getItemFromConfig(config: YamlConfiguration, path: String): ItemStack? =
        config.getConfigurationSection(path)?.let { getItemFromConfig(it) }

    @JvmStatic
    fun getItemFromConfig(config: ConfigurationSection): ItemStack? {
        val type = config.getString("Type")?.let { XMaterial.valueOf(it) } ?: return null
        val item = buildItem(type) {
            // 基本属性
            config.getInt("Data", 0).let { damage = it }
            config.getString("Display")?.let { name = it }
            config.getStringList("Lore").let { lore.addAll(it) }

            // 附魔属性
            config.getConfigurationSection("Enchantments")?.let { enchantments ->
                enchantments.getKeys(false).forEach { enchantKey ->
                    Enchantment.getByName(enchantKey)?.let { ench ->
                        enchants[ench] = enchantments.getInt(enchantKey)
                    }
                }
            }

            // 常规物品设置
            flags.addAll(config.getStringList("Options.HideFlags").mapNotNull { ItemFlag.valueOf(it) })
            if (config.getBoolean("Options.Glow", false)) shiny()
            isUnbreakable = config.getBoolean("Options.Unbreakable", false)

            // 特殊物品设置
            when (originMeta) {
                is BannerMeta -> {
                    config.getStringList("Options.BannerPatterns").forEach { pattern ->
                        pattern.split("-", limit = 2).let { (colorName, patternName) ->
                            val color = DyeColor.valueOf(colorName)
                            val patternType = PatternType.getByIdentifier(patternName) ?: return@forEach
                            patterns.add(Pattern(color, patternType))
                        }
                    }
                }

                is LeatherArmorMeta -> {
                    color = config.getInt("Options.Color").let(Color::fromRGB)
                }

                is PotionMeta -> {
                    config.getString("Options.BasePotionData.Type")?.let { potionTypeName ->
                        potionData = PotionData(
                            PotionType.valueOf(potionTypeName),
                            config.getBoolean("Options.BasePotionData.Extended"),
                            config.getBoolean("Options.BasePotionData.Upgraded")
                        )
                    }
                    config.getStringList("Options.PotionEffects").forEach { effect ->
                        effect.split("-", limit = 3).let { (typeName, amplifier, duration) ->
                            PotionEffectType.getByName(typeName)?.let {
                                potions.add(PotionEffect(it, duration.toInt(), amplifier.toInt()))
                            }
                        }
                    }
                }

                is SkullMeta -> {
                    skullOwner = config.getString("Options.SkullOwner")
                }
            }

            // 名称 Lore 上色
            colored()
        }

        config.getConfigurationSection("NBT")?.let { nbtSection ->
            item.itemTagReader {
                nbtSection.getKeys(true).forEach { key ->
                    set(key, nbtSection.get(key))
                }
                write(item)
            }
        }

        return item
    }

    @JvmStatic
    fun saveItemToConfig(item: ItemStack, config: YamlConfiguration, path: String) {
        buildItem(item) {
            // 基本属性
            config.set("$path.Type", XMaterial.matchXMaterial(material).name)
            damage.takeIf { it != 0 }?.let { config.set("$path.Data", it) }
            config.set("$path.Display", item.getName())
            lore.takeIf { it.isNotEmpty() }?.let { config.set("$path.Lore", it) }

            // 附魔属性
            if (enchants.isNotEmpty()) {
                enchants.forEach { (enchant, level) ->
                    config.set("$path.Enchantments.${enchant.name}", level)
                }
                config.set("$path.Options.Glow", true)
            }

            // 常规物品设置
            flags.takeIf { it.isNotEmpty() }?.map { it.name }?.let { config.set("$path.Options.HideFlags", it) }
            isUnbreakable.takeIf { it }?.let { config.set("$path.Options.Unbreakable", it) }

            // 特殊物品设置
            when (originMeta) {
                is BannerMeta -> config.set(
                    "$path.Options.BannerPatterns",
                    patterns.map { "${it.color.name}-${it.pattern.identifier}" }
                )

                is LeatherArmorMeta -> config.set("$path.Options.Color", color?.asRGB() ?: Color.WHITE.asRGB())

                is PotionMeta -> {
                    config.set("$path.Options.BasePotionData.Type", potionData?.type?.name)
                    config.set("$path.Options.BasePotionData.Extended", potionData?.isExtended)
                    config.set("$path.Options.BasePotionData.Upgraded", potionData?.isUpgraded)
                    config.set(
                        "$path.Options.PotionEffects",
                        potions.map { "${it.type.name}-${it.amplifier}-${it.duration}" }
                    )
                }

                is SkullMeta -> skullOwner?.let { config.set("$path.Options.SkullOwner", it) }
            }

            // NBT
            item.getItemTag().entries
                .filter {
                    it.key !in listOf(
                        "display", "Damage", "ench", "Enchantments",
                        "Unbreakable", "HideFlags",
                        "BlockEntityTag", "Potion", "SkullOwner"
                    )
                }
                .forEach { tag ->
                    config.set("$path.NBT.${tag.key}", tag.value.getValue())
                }
        }
    }

    @JvmStatic
    fun getItemFromPlugin(plugin: String, id: String, player: Player? = null): ItemStack? {
        val item: ItemStack? = when (plugin) {
            "MythicMobs" -> Mythic.API.getItemStack(id)

            "SX-Attribute" -> SXAttribute.getApi().getItem(id, player)

            "NeigeItems" -> ItemManager.getItemStack(id)

            else -> null
        }
        return item
    }

    @JvmStatic
    fun getNBTInfo(item: ItemStack): List<String> {
        fun getItemTagValue(tag: ItemTag, indent: String = ""): List<String> {
            val result = mutableListOf<String>()
            tag.forEach { (key, value) ->
                when (value) {
                    is ItemTag -> {
                        result.add("$indent§7$key§8:")
                        result.addAll(getItemTagValue(value, "$indent  "))
                    }

                    is ItemTagList -> {
                        result.add("§7$indent$key§8:")
                        value.forEach { v ->
                            when (v.type) {
                                ItemTagType.COMPOUND -> result.addAll(getItemTagValue(v.asCompound(), "$indent  "))
                                else -> result.add("$indent  §f- §f$v")
                            }
                        }
                    }

                    else -> result.add("$indent§7$key§8: §f$value")
                }
            }
            return result
        }
        return getItemTagValue(item.getItemTag(), "  ")
    }

    @JvmStatic
    fun getEnchantInfo(item: ItemStack): List<String> {
        val result = mutableListOf<String>()
        buildItem(item) {
            enchants.forEach { (enchant, level) ->
                result.add("  §7${enchant.name}§8: §f$level")
            }
        }
        return result
    }

    private fun ItemTagData.getValue(): Any = when (val data = unsafeData()) {
        is ItemTag -> data.entries.associate { it.key to it.value.getValue() }
        is ItemTagList -> data.map { it.getValue() }
        else -> data
    }
}