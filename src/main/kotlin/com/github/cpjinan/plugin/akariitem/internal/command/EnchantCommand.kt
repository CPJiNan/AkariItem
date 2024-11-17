package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "Enchant", aliases = ["ench"])
object EnchantCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()

        literal("info") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                sender.castSafely<Player>().let {
                    val item = it?.itemInHand
                    if (item.isAir()) {
                        sender.sendLang("Air-In-Hand")
                        return@execute
                    }

                    sender.sendLang("Enchant-Info")
                    ItemUtil.getEnchantInfo(item).forEach { message ->
                        sender.sendMessage(message)
                    }
                }
            }
        }

        literal("set") {
            dynamic("enchant").int("level") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    val item = sender.castSafely<Player>()?.itemInHand
                    if (item.isAir()) {
                        sender.sendLang("Air-In-Hand")
                        return@execute
                    }

                    Enchantment.getByName(context["enchant"])?.let {
                        item.addUnsafeEnchantment(it, context.int("level"))
                    }

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

                    Enchantment.getByName(context["enchant"])?.let {
                        item.addUnsafeEnchantment(it, context.int("level"))
                    }

                    if (!silent) sender.sendLang("Enchant-Set", context["enchant"], context.int("level"))
                }
            }
        }
    }
}