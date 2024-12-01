package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.Command
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.CommandParameter
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.createHelper
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.module.lang.sendLang
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "NBTEdit", aliases = ["nbt"])
object NBTCommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            sender.createHelper(
                mainCommand = Command(
                    name = "nbtedit",
                    parameters = listOf(
                        CommandParameter("...", optional = true)
                    ),
                    description = "&f命令别名&8: &7nbt"
                ),
                subCommands = arrayOf(
                    Command(
                        name = "info",
                        info = "查看手中物品 NBT",
                        suggest = "/nbtedit info"
                    ),
                    Command(
                        name = "set",
                        parameters = listOf(
                            CommandParameter(
                                name = "key",
                                description = "NBT 节点名 \\(支持多级节点\\)"
                            ),
                            CommandParameter(
                                name = "value",
                                description = "NBT 值 \\(类型: String\\)"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        ),
                        info = "修改手中物品指定 NBT"
                    ),
                    Command(
                        name = "remove",
                        parameters = listOf(
                            CommandParameter(
                                name = "key",
                                description = "NBT 节点名 \\(支持多级节点\\)"
                            )
                        ),
                        info = "删除手中物品指定 NBT"
                    )
                )
            )
        }

        literal("info") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                sender.castSafely<Player>().let { it ->
                    val item = it?.itemInHand
                    if (item.isAir()) {
                        sender.sendLang("Air-In-Hand")
                        return@execute
                    }

                    sender.sendLang("NBT-Info")
                    ItemUtil.getNBTInfo(item).forEach { message ->
                        sender.sendMessage(message)
                    }
                }
            }
        }

        literal("set").dynamic("key").dynamic("value") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.castSafely<Player>()?.itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                item.itemTagReader {
                    set(context["key"], context["value"])
                    write(item)
                }

                sender.sendLang("NBT-Set", context["key"], context["value"])
            }
        }.dynamic("options") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                val value = context["value"] + CommandUtil.parseContentAfterSpace(content.split(" "))
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

                item.itemTagReader {
                    set(context["key"], value)
                    write(item)
                }

                if (!silent) sender.sendLang("NBT-Set", context["key"], value)
            }
        }

        literal("remove").dynamic("key") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.castSafely<Player>()?.itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                item.itemTagReader {
                    set(context["key"], null)
                    write(item)
                }

                sender.sendLang("NBT-Remove", context["key"])
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

                item.itemTagReader {
                    set(context["key"], null)
                    write(item)
                }

                if (!silent) sender.sendLang("NBT-Remove", context["key"])
            }
        }
    }
}