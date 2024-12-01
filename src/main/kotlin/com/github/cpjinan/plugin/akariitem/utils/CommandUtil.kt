package com.github.cpjinan.plugin.akariitem.utils

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.chat.colored
import taboolib.module.chat.component

object CommandUtil {
    /**
     * 解析命令行参数及其对应值
     * @return 参数及对应值
     */
    fun parseOptions(args: List<String>): HashMap<String, String?> {
        val options = hashMapOf<String, String?>()
        var i = 0
        while (i < args.size) {
            val arg = args[i]
            if (arg.startsWith("--") || arg.startsWith("-")) {
                val (key, value) = if (arg.contains("=")) {
                    val splitArg = arg.split("=", limit = 2)
                    val key = splitArg[0].removePrefix("-").removePrefix("-")
                    val value = splitArg[1]
                    key to value
                } else {
                    val key = arg.removePrefix("-").removePrefix("-")
                    val value = if (i + 1 < args.size && !args[i + 1].startsWith("-")) args[++i] else null
                    key to value
                }
                options[key] = value
            }
            i++
        }
        return options
    }

    /**
     * 解析带有空格的命令参数
     * @return 参数值
     */
    fun parseContentAfterSpace(args: List<String>): String {
        var i = 0
        var content = ""
        while (i < args.size) {
            val arg = args[i]
            if (arg.startsWith("--") || arg.startsWith("-")) break
            else content += " $arg"
            i++
        }
        return content
    }

    fun ProxyCommandSender.createHelper(
        plugin: String = pluginId,
        version: String = "v$pluginVersion",
        mainCommand: Command,
        subCommand: List<Command>? = null
    ) {
        // 插件名和版本
        sendMessage(" &f&l$plugin &f&l$version".colored())
        sendMessage(" ")

        // 发送主命令信息
        " &7命令: &f/${mainCommand.getInfo()}".component().buildColored().sendTo(this)

        sendMessage(" &7参数:".colored())

        // 发送子命令信息
        subCommand?.forEach { sub ->
            "   &8- ${sub.getInfo()}".component().buildColored().sendTo(this)
            sub.info?.let { sendMessage("     &7$it".colored()) }
        }
    }

    data class Command(
        val name: String,
        val parameters: List<CommandParameter>? = null,
        val info: String? = null,
        val description: List<String>? = null
    )

    data class CommandParameter(
        val name: String,
        val description: List<String>? = null,
        val optional: Boolean = false
    )

    // 获取命令信息
    private fun Command.getInfo(): String {
        val descriptionHover = description?.joinToString(",&f")?.let { "hover=&f$it" } ?: ""
        val parametersInfo = parameters?.joinToString(" ") { it.getInfo() } ?: ""
        return "[&f$name]($descriptionHover) $parametersInfo"
    }

    // 获取命令参数信息
    private fun CommandParameter.getInfo(): String {
        val prefix = if (optional) "\\[" else "<"
        val suffix = if (optional) "\\]" else ">"
        val descriptionHover = description?.joinToString(",")?.let { "hover=$it" } ?: ""
        return "[&8$prefix$name$suffix]($descriptionHover)"
    }
}