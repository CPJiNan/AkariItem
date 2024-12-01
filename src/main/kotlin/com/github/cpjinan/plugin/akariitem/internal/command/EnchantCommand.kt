package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.Command
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.CommandParameter
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.createHelper
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.lang.sendLang
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "Enchant", aliases = ["ench"])
object EnchantCommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            sender.createHelper(
                mainCommand = Command(
                    name = "enchant",
                    parameters = listOf(
                        CommandParameter("...", optional = true)
                    ),
                    description = "&f命令别名&8: &7ench"
                ),
                subCommands = arrayOf(
                    Command(
                        name = "info",
                        info = "查看手中物品附魔"
                    ),
                    Command(
                        name = "set",
                        parameters = listOf(
                            CommandParameter(
                                name = "enchant",
                                description = "英文附魔名称 \\(可以使用 /enchant info 查看已附魔物品的附魔名称\\)"

                            ),
                            CommandParameter(
                                name = "level",
                                description = "附魔等级"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&fsilent&8: &7不输出命令提示"
                            )
                        ),
                        info = "设置手中物品附魔"
                    )
                )
            )
        }

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