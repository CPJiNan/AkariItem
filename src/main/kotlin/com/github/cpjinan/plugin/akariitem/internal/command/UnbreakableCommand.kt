package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.isAir

@Suppress("DEPRECATION")
@CommandHeader(name = "Unbreakable")
object UnbreakableCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()

        bool("isUnbreakable") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                val item = sender.castSafely<Player>()?.itemInHand
                if (item.isAir()) {
                    sender.sendLang("Air-In-Hand")
                    return@execute
                }

                val meta = item.itemMeta
                meta?.isUnbreakable = context.bool("isUnbreakable")
                item.itemMeta = meta

                when (context.bool("isUnbreakable")) {
                    true -> sender.sendLang("Unbreakable-True")
                    false -> sender.sendLang("Unbreakable-False")
                }
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

                val meta = item.itemMeta
                meta?.isUnbreakable = context.bool("isUnbreakable")
                item.itemMeta = meta

                if (!silent) {
                    when (context.bool("isUnbreakable")) {
                        true -> sender.sendLang("Unbreakable-True")
                        false -> sender.sendLang("Unbreakable-False")
                    }
                }
            }
        }
    }
}