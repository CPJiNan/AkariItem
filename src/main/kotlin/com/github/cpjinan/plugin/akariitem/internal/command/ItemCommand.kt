package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.api.ItemAPI
import com.github.cpjinan.plugin.akariitem.common.PluginConfig
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.Command
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.CommandParameter
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.createHelper
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.nms.getName
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "AkariItem")
object ItemCommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            sender.createHelper(
                mainCommand = Command(
                    name = "akariitem",
                    parameters = listOf(
                        CommandParameter("...", optional = true)
                    )
                ),
                subCommand = listOf(
                    Command(
                        name = "get",
                        parameters = listOf(
                            CommandParameter("id"),
                            CommandParameter("amount", optional = true),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = listOf("&fsilent&8: &7不输出命令提示")
                            )
                        ),
                        info = "获取物品"
                    ),
                    Command(
                        name = "give",
                        parameters = listOf(
                            CommandParameter("player"),
                            CommandParameter("id"),
                            CommandParameter("amount", optional = true),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = listOf("&fsilent&8: &7不输出命令提示")
                            )
                        ),
                        info = "给予玩家物品"
                    ),
                    Command(
                        name = "save",
                        parameters = listOf(
                            CommandParameter("id"),
                            CommandParameter("path", optional = true),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = listOf("&fsilent&8: &7不输出命令提示")
                            )
                        ),
                        info = "存储手中物品"
                    ),
                    Command(
                        name = "list",
                        info = "物品列表"
                    ),
                    Command(
                        name = "reload",
                        info = "重载插件"
                    )
                )
            )
        }

        literal("get").dynamic("id") {
            suggest { ItemAPI.getItemNames() }
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = ItemAPI.getItem(ItemAPI.getItemConfig(), context["id"])
                if (item != null) {
                    sender.cast<Player>().giveItem(item)
                    sender.sendLang("Item-Get", context["id"], 1)
                } else sender.sendLang("Item-Not-Found")
            }
        }.int("amount", optional = true) {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = ItemAPI.getItem(ItemAPI.getItemConfig(), context["id"])
                if (item != null) {
                    sender.cast<Player>().giveItem(item, context.int("amount"))
                    sender.sendLang("Item-Get", context["id"], context.int("amount"))
                } else sender.sendLang("Item-Not-Found")
            }
        }.dynamic("options", optional = true) {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                val args = CommandUtil.parseOptions(content.split(" "))
                var silent = false

                for ((k, _) in args) {
                    when (k.lowercase()) {
                        "silent" -> silent = true
                    }
                }

                val item = ItemAPI.getItem(ItemAPI.getItemConfig(), context["id"])
                if (item != null) {
                    sender.cast<Player>().giveItem(item, context.int("amount"))
                    if (!silent) sender.sendLang("Item-Get", context["id"], context.int("amount"))
                } else if (!silent) sender.sendLang("Item-Not-Found")
            }
        }

        literal("give").player("player").dynamic("id") {
            suggest { ItemAPI.getItemNames() }
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = ItemAPI.getItem(ItemAPI.getItemConfig(), context["id"])
                if (item != null) {
                    val player = context.player("player").cast<Player>()
                    player.giveItem(item)
                    sender.sendLang("Item-Give", context["id"], 1, context["player"])
                } else sender.sendLang("Item-Not-Found")
            }
        }.int("amount", optional = true) {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = ItemAPI.getItem(ItemAPI.getItemConfig(), context["id"])
                if (item != null) {
                    val player = context.player("player").cast<Player>()
                    player.giveItem(item, context.int("amount"))
                    sender.sendLang("Item-Give", context["id"], context.int("amount"), context["player"])
                } else sender.sendLang("Item-Not-Found")
            }
        }.dynamic("options", optional = true) {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                val args = CommandUtil.parseOptions(content.split(" "))
                var silent = false

                for ((k, _) in args) {
                    when (k.lowercase()) {
                        "silent" -> silent = true
                    }
                }

                val item = ItemAPI.getItem(ItemAPI.getItemConfig(), context["id"])
                if (item != null) {
                    val player = context.player("player").cast<Player>()
                    player.giveItem(item, context.int("amount"))
                    if (!silent) sender.sendLang("Item-Give", context["id"], context.int("amount"), context["player"])
                } else if (!silent) sender.sendLang("Item-Not-Found")
            }
        }

        literal("save") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.cast<Player>().itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                ItemAPI.saveItem(
                    item,
                    "module/item/SaveItems.yml",
                    item.getName(sender.cast())
                )
                ItemAPI.reloadItem()
                sender.sendLang("Item-Save", "SaveItems.yml", item.getName(sender.cast()))
            }
        }.dynamic("id") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.cast<Player>().itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                ItemAPI.saveItem(
                    item,
                    "item/SaveItems.yml",
                    context["id"]
                )
                ItemAPI.reloadItem()
                sender.sendLang("Item-Save", "SaveItems.yml", context["id"])
            }
        }.dynamic("path") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.cast<Player>().itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                ItemAPI.saveItem(
                    item,
                    "item/${context["path"].takeIf { it.endsWith(".yml") } ?: "${context["path"]}.yml"}",
                    context["id"]
                )
                ItemAPI.reloadItem()
                sender.sendLang(
                    "Item-Save",
                    context["path"].takeIf { it.endsWith(".yml") } ?: "${context["path"]}.yml",
                    context["id"]
                )
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

                val item = sender.cast<Player>().itemInHand
                if (item.isAir()) {
                    if (!silent) sender.sendLang("Air-In-Hand")
                    return@execute
                }

                ItemAPI.saveItem(
                    item,
                    "item/${context["path"].takeIf { it.endsWith(".yml") } ?: "${context["path"]}.yml"}",
                    context["id"]
                )
                ItemAPI.reloadItem()
                if (!silent) sender.sendLang(
                    "Item-Save",
                    context["path"].takeIf { it.endsWith(".yml") } ?: "${context["path"]}.yml",
                    context["id"]
                )
            }
        }

        literal("list") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                sender.sendLang("Item-List")
                sender.sendMessage(
                    ItemAPI.getItemNames().joinToString(separator = "&7, ".colored()) { "&f${it}".colored() })
            }
        }
    }

    @CommandBody
    val reload = subCommand {
        execute { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            PluginConfig.reloadConfig()
            sender.sendLang("Plugin-Reloaded")
        }
    }
}