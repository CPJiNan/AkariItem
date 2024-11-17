package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "Enchant", aliases = ["ench"])
object EnchantCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()

        dynamic("enchant") { suggest { enchantList } }.int("level") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.castSafely<Player>()?.itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                item.itemMeta = buildItem(item) {
                    Enchantment.getByName(context["enchant"].getEnchantName())?.let {
                        enchants[it] = context.int("level")
                    }
                }.itemMeta

                sender.sendLang("Enchant-Set", context["enchant"], context.int("level"))
            }
        }.dynamic("options") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                val args = CommandUtil.parseOptions(content.split(" "))
                var silent = false

                for ((k, _) in args) {
                    when (k.lowercase()) {
                        "silent" -> silent = true
                    }
                }

                val item = sender.castSafely<Player>()?.itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                item.itemMeta = buildItem(item) {
                    Enchantment.getByName(context["enchant"].getEnchantName())?.let {
                        enchants[it] = context.int("level")
                    }
                }.itemMeta

                if (!silent) sender.sendLang("Enchant-Set", context["enchant"], context.int("level"))
            }
        }
    }

    val enchantList = listOf(
        "水下速掘",
        "节肢杀手",
        "绑定诅咒",
        "爆炸保护",
        "引雷",
        "深海探索者",
        "效率",
        "摔落保护",
        "火焰附加",
        "火焰保护",
        "火矢",
        "时运",
        "冰霜行者",
        "穿刺",
        "无限",
        "击退",
        "抢夺",
        "忠诚",
        "海之眷顾",
        "饵钓",
        "经验修补",
        "多重射击",
        "穿透",
        "力量",
        "弹射物保护",
        "保护",
        "冲击",
        "快速装填",
        "水下呼吸",
        "激流",
        "锋利",
        "精准采集",
        "亡灵杀手",
        "灵魂疾行",
        "横扫之刃",
        "迅捷潜行",
        "荆棘",
        "耐久",
        "消失诅咒",
        "AQUA_AFFINITY",
        "BANE_OF_ARTHROPODS",
        "BINDING_CURSE",
        "BLAST_PROTECTION",
        "BREACH",
        "CHANNELING",
        "DENSITY",
        "DEPTH_STRIDER",
        "EFFICIENCY",
        "FEATHER_FALLING",
        "FIRE_ASPECT",
        "FIRE_PROTECTION",
        "FLAME",
        "FORTUNE",
        "FROST_WALKER",
        "IMPALING",
        "INFINITY",
        "KNOCKBACK",
        "LOOTING",
        "LOYALTY",
        "LUCK_OF_THE_SEA",
        "LURE",
        "MENDING",
        "MULTISHOT",
        "PIERCING",
        "POWER",
        "PROJECTILE_PROTECTION",
        "PROTECTION",
        "PUNCH",
        "QUICK_CHARGE",
        "RESPIRATION",
        "RIPTIDE",
        "SHARPNESS",
        "SILK_TOUCH",
        "SMITE",
        "SOUL_SPEED",
        "SWEEPING_EDGE",
        "SWIFT_SNEAK",
        "THORNS",
        "UNBREAKING",
        "VANISHING_CURSE",
        "WIND_BURST"
    )

    fun String.getEnchantName(): String {
        return when (this) {
            "水下速掘" -> "AQUA_AFFINITY"
            "节肢杀手" -> "BANE_OF_ARTHROPODS"
            "绑定诅咒" -> "BINDING_CURSE"
            "爆炸保护" -> "BLAST_PROTECTION"
//            "Reduces armor effectiveness against maces" -> "BREACH"
            "引雷" -> "CHANNELING"
//            "Increases fall damage of maces" -> "DENSITY"
            "深海探索者" -> "DEPTH_STRIDER"
            "效率" -> "EFFICIENCY"
            "摔落保护" -> "FEATHER_FALLING"
            "火焰附加" -> "FIRE_ASPECT"
            "火焰保护" -> "FIRE_PROTECTION"
            "火矢" -> "FLAME"
            "时运" -> "FORTUNE"
            "冰霜行者" -> "FROST_WALKER"
            "穿刺" -> "IMPALING"
            "无限" -> "INFINITY"
            "击退" -> "KNOCKBACK"
            "抢夺" -> "LOOTING"
            "忠诚" -> "LOYALTY"
            "海之眷顾" -> "LUCK_OF_THE_SEA"
            "饵钓" -> "LURE"
            "经验修补" -> "MENDING"
            "多重射击" -> "MULTISHOT"
            "穿透" -> "PIERCING"
            "力量" -> "POWER"
            "弹射物保护" -> "PROJECTILE_PROTECTION"
            "保护" -> "PROTECTION"
            "冲击" -> "PUNCH"
            "快速装填" -> "QUICK_CHARGE"
            "水下呼吸" -> "RESPIRATION"
            "激流" -> "RIPTIDE"
            "锋利" -> "SHARPNESS"
            "精准采集" -> "SILK_TOUCH"
            "亡灵杀手" -> "SMITE"
            "灵魂疾行" -> "SOUL_SPEED"
            "横扫之刃" -> "SWEEPING_EDGE"
            "迅捷潜行" -> "SWIFT_SNEAK"
            "荆棘" -> "THORNS"
            "耐久" -> "UNBREAKING"
            "消失诅咒" -> "VANISHING_CURSE"
//            "Emits wind burst upon hitting enemy" -> "WIND_BURST"
            else -> this
        }
    }
}