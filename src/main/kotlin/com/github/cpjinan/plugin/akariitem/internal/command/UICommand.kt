package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.Command
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.CommandParameter
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.createHelper
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*

@CommandHeader(name = "AkariUI")
object UICommand {
    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            sender.createHelper(
                mainCommand = Command(
                    name = "akariui",
                    parameters = listOf(CommandParameter("...", optional = true))
                ),
                subCommands = arrayOf(
                    Command(
                        name = "close",
                        info = "关闭玩家当前 UI 界面"
                    )
                )
            )
        }

        literal("close") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                sender.castSafely<Player>()?.closeInventory() ?: return@execute
            }
        }.player("player", optional = true) {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                context.player("player").castSafely<Player>()?.closeInventory() ?: return@execute
            }
        }
    }
}