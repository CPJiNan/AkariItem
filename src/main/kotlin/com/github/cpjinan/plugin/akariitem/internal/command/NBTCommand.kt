package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.module.nms.getItemTag
import taboolib.module.nms.itemTagReader
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "NBTEdit", aliases = ["nbt"])
object NBTCommand {
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

                    sender.sendLang("NBT-Info")
                    ItemUtil.getNBTInfo(item.getItemTag(), "  ").forEach {
                        sender.sendMessage(it)
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