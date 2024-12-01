package com.github.cpjinan.plugin.akariitem.internal.command

import com.github.cpjinan.plugin.akariitem.utils.CommandUtil
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.Command
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.CommandParameter
import com.github.cpjinan.plugin.akariitem.utils.CommandUtil.createHelper
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common5.util.replace
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import taboolib.platform.util.modifyLore

@Suppress("DEPRECATION")
@CommandHeader(name = "LoreEdit", aliases = ["lore"])
object LoreCommand {
    private val clipboard: HashMap<Player, String> = hashMapOf()

    @CommandBody
    val lore = mainCommand {
        execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
            sender.createHelper(
                mainCommand = Command(
                    name = "loreedit",
                    parameters = listOf(
                        CommandParameter("...", optional = true)
                    ),
                    description = "&f命令别名&8: &7lore"
                ),
                subCommands = arrayOf(
                    Command(
                        name = "info",
                        info = "查看手中物品 Lore",
                        suggest = "/loreedit info"
                    ),
                    Command(
                        name = "add",
                        info = "为手中物品添加 Lore",
                        parameters = listOf(
                            CommandParameter(
                                name = "lore",
                                description = "Lore 内容"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "remove",
                        info = "为手中物品删除 Lore",
                        parameters = listOf(
                            CommandParameter(
                                name = "line",
                                description = "要删除的 Lore 行数 \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "set",
                        info = "为手中物品设置 Lore",
                        parameters = listOf(
                            CommandParameter(
                                name = "line",
                                description = "要修改的 Lore 行数 \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "lore",
                                description = "Lore 内容"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "insert",
                        info = "在指定行后面插入新的 Lore",
                        parameters = listOf(
                            CommandParameter(
                                name = "line",
                                description = "在哪一行后面插入新的 Lore \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "lore",
                                description = "Lore 内容"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "replace",
                        info = "为手中物品替换 Lore 中的指定内容 (默认全部替换,参数可指定行)",
                        parameters = listOf(
                            CommandParameter(
                                name = "oldChar",
                                description = "旧的内容"
                            ),
                            CommandParameter(
                                name = "newChar",
                                description = "新的内容"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示 &8\\; &f--line\\=Num&8: &7指定在某行中替换"
                            )
                        )
                    ),
                    Command(
                        name = "clear",
                        info = "清空手中物品的全部 Lore (谨慎使用)",
                        parameters = listOf(
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "clone",
                        info = "将手中物品某行 Lore 的内容复制到另一行",
                        parameters = listOf(
                            CommandParameter(
                                name = "lineA",
                                description = "从哪一行复制 Lore \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "line",
                                description = "将 Lore 粘贴至哪一行 \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "copy",
                        info = "将手中物品的某行 Lore 的内容复制到剪贴板",
                        parameters = listOf(
                            CommandParameter(
                                name = "line",
                                description = "从哪一行复制 Lore \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "paste",
                        info = "将剪贴板的 Lore 内容粘贴至某一行",
                        parameters = listOf(
                            CommandParameter(
                                name = "line",
                                description = "将 Lore 粘贴至哪一行 \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "cut",
                        info = "将手中物品的某行 Lore 内容剪切至剪贴板 (复制并删除)",
                        parameters = listOf(
                            CommandParameter(
                                name = "line",
                                description = "从哪一行剪切 Lore \\(从 1 开始\\)"
                            ),
                            CommandParameter(
                                name = "options",
                                optional = true,
                                description = "&f--silent&8: &7不输出命令提示"
                            )
                        )
                    ),
                    Command(
                        name = "clipboard",
                        info = "查看/修改/清空剪贴板内容",
                        parameters = listOf(
                            CommandParameter(
                                name = "info/set/clear",
                                description = "&finfo &7- &f查看剪贴板内容 &8\\; &fset <内容> [参数] &7- &f设置剪贴板内容 &8\\; &fclear [参数] &7- &f清空剪贴板内容"
                            )
                        )
                    )
                )
            )
        }

        literal("info") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                try {
                    sender.castSafely<Player>().let {
                        val item = it?.itemInHand
                        if (item.isNotAir()) {
                            sender.sendLang("Lore-Info")
                            item.itemMeta?.lore?.forEachIndexed { index, content ->
                                sender.sendMessage("&7${index + 1} &8| &r$content".colored())
                            }
                        } else sender.sendLang("Air-In-Hand")
                    }
                } catch (_: IndexOutOfBoundsException) {
                    sender.sendLang("Index-Out-Of-Bounds")
                }
            }
        }

        literal("add") {
            dynamic("lore") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    add(context["lore"].colored())
                                }
                                sender.sendLang("Lore-Add", context["lore"].colored())
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val lore = context["lore"] + CommandUtil.parseContentAfterSpace(content.split(" "))
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    add(lore.colored())
                                }
                                if (!silent) sender.sendLang("Lore-Add", lore.colored())
                            } else if (!silent) sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("remove") {
            int("line") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    removeAt(context.int("line") - 1)
                                }
                                sender.sendLang("Lore-Remove", context.int("line"))
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    removeAt(context.int("line") - 1)
                                }
                                if (!silent) sender.sendLang("Lore-Remove", context.int("line"))
                            } else if (!silent) sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("set") {
            int("line").dynamic("lore") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    set(context.int("line") - 1, context["lore"].colored())
                                }
                                sender.sendLang("Lore-Set", context.int("line"), context["lore"].colored())
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val lore = context["lore"] + CommandUtil.parseContentAfterSpace(content.split(" "))
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    set(context.int("line") - 1, lore.colored())
                                }
                                if (!silent) sender.sendLang("Lore-Set", context.int("line"), lore.colored())
                            } else if (!silent) sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("insert") {
            int("line").dynamic("lore") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    add(context.int("line"), context["lore"].colored())
                                }
                                sender.sendLang(
                                    "Lore-Insert",
                                    context.int("line"),
                                    context.int("line") + 1,
                                    context["lore"].colored()
                                )
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val lore = context["lore"] + CommandUtil.parseContentAfterSpace(content.split(" "))
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    add(context.int("line"), lore.colored())
                                }
                                if (!silent) sender.sendLang(
                                    "Lore-Insert",
                                    context.int("line"),
                                    context.int("line") + 1,
                                    lore.colored()
                                )
                            } else if (!silent) sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("replace") {
            dynamic("oldChar").dynamic("newChar") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isAir()) {
                                sender.sendLang("Air-In-Hand")
                                return@execute
                            }

                            val meta = item.itemMeta
                            meta?.lore =
                                item.itemMeta?.lore?.replace(Pair(context["oldChar"], context["newChar"]))

                            item.itemMeta = meta

                            sender.cast<Player>().updateInventory()
                            sender.sendLang(
                                "Lore-Replace",
                                context["oldChar"],
                                context["newChar"],
                                "All"
                            )
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val args = CommandUtil.parseOptions(content.split(" "))
                            var silent = false
                            var line: Int? = null

                            for ((k, v) in args) {
                                when (k.lowercase()) {
                                    "silent" -> silent = true
                                    "line" -> line = v?.toInt()
                                }
                            }

                            val item = it?.itemInHand
                            if (item.isAir()) {
                                sender.sendLang("Air-In-Hand")
                                return@execute
                            }

                            val meta = item.itemMeta

                            if (line == null) {
                                meta?.lore =
                                    item.itemMeta?.lore?.replace(Pair(context["oldChar"], context["newChar"]))
                            } else {
                                val lore = meta?.lore
                                lore?.set(
                                    line - 1,
                                    lore[line - 1].replace(Pair(context["oldChar"], context["newChar"]))
                                )
                                meta?.lore = lore
                            }

                            item.itemMeta = meta
                            sender.cast<Player>().updateInventory()

                            if (!silent) sender.sendLang(
                                "Lore-Replace",
                                context["oldChar"],
                                context["newChar"],
                                line ?: "All"
                            )
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("clear") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                try {
                    sender.castSafely<Player>().let {
                        val item = it?.itemInHand
                        if (item.isNotAir()) {
                            item.modifyLore {
                                clear()
                            }
                            sender.sendLang("Lore-Clear")
                        } else sender.sendLang("Air-In-Hand")
                    }
                } catch (_: IndexOutOfBoundsException) {
                    sender.sendLang("Index-Out-Of-Bounds")
                }
            }
        }.dynamic("options") {
            execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, content: String ->
                try {
                    val args = CommandUtil.parseOptions(content.split(" "))
                    var silent = false

                    for ((k, _) in args) {
                        when (k.lowercase()) {
                            "silent" -> silent = true
                        }
                    }

                    sender.castSafely<Player>().let {
                        val item = it?.itemInHand
                        if (item.isNotAir()) {
                            item.modifyLore {
                                clear()
                            }
                            if (!silent) sender.sendLang("Lore-Clear")
                        } else if (!silent) sender.sendLang("Air-In-Hand")
                    }
                } catch (_: IndexOutOfBoundsException) {
                    sender.sendLang("Index-Out-Of-Bounds")
                }
            }
        }

        literal("clone") {
            int("lineA").int("lineB") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    set(context.int("lineB") - 1, get(context.int("lineA") - 1))
                                }
                                sender.sendLang("Lore-Clone", context.int("lineA"), context.int("lineB"))
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    set(context.int("lineB") - 1, get(context.int("lineA") - 1))
                                }
                                if (!silent) sender.sendLang("Lore-Clone", context.int("lineA"), context.int("lineB"))
                            } else if (!silent) sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("copy") {
            int("line") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    val value = get(context.int("line") - 1)
                                    clipboard[it] = value
                                    sender.sendLang("Lore-Copy", context.int("line"), value)
                                }
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    val value = get(context.int("line") - 1)
                                    clipboard[it] = value
                                    if (!silent) sender.sendLang("Lore-Copy", context.int("line"), value)
                                }
                            } else if (!silent) sender.sendLang("Air-In-Hand") else return@execute
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("paste") {
            int("line") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    sender.castSafely<Player>().let {
                        try {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    clipboard[it]?.colored()?.let { value ->
                                        if (clipboard.isNotEmpty()) {
                                            set(context.int("line") - 1, value)
                                            sender.sendLang("Lore-Paste", context.int("line"), value)
                                        } else sender.sendLang("Lore-Clipboard-Empty")
                                    }
                                }
                            } else sender.sendLang("Air-In-Hand")
                        } catch (_: IndexOutOfBoundsException) {
                            sender.sendLang("Index-Out-Of-Bounds")
                        }
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    clipboard[it]?.colored()?.let { value ->
                                        if (clipboard.isNotEmpty()) {
                                            set(context.int("line"), value)
                                            if (!silent) sender.sendLang("Lore-Paste", context.int("line"), value)
                                        } else if (!silent) sender.sendLang("Lore-Clipboard-Empty")
                                    }
                                }
                            } else if (!silent) sender.sendLang("Air-In-Hand") else return@execute
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("cut") {
            int("line") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                    try {
                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    val value = get(context.int("line") - 1)
                                    clipboard[it] = value
                                    removeAt(context.int("line") - 1)
                                    sender.sendLang("Lore-Cut", context.int("line"), value)
                                }
                            } else sender.sendLang("Air-In-Hand")
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, content: String ->
                    try {
                        val args = CommandUtil.parseOptions(content.split(" "))
                        var silent = false

                        for ((k, _) in args) {
                            when (k.lowercase()) {
                                "silent" -> silent = true
                            }
                        }

                        sender.castSafely<Player>().let {
                            val item = it?.itemInHand
                            if (item.isNotAir()) {
                                item.modifyLore {
                                    val value = get(context.int("line") - 1)
                                    clipboard[it] = value
                                    removeAt(context.int("line") - 1)
                                    if (!silent) sender.sendLang("Lore-Cut", context.int("line"), value)
                                }
                            } else if (!silent) sender.sendLang("Air-In-Hand") else return@execute
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        sender.sendLang("Index-Out-Of-Bounds")
                    }
                }
            }
        }

        literal("clipboard") {

            literal("info") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                    sender.castSafely<Player>().let {
                        if (clipboard[it]?.isNotEmpty() == true) {
                            sender.sendLang(
                                "Lore-Clipboard-Info",
                                clipboard[it]!!
                            )
                        } else sender.sendLang("Lore-Clipboard-Empty")
                    }
                }
            }

            literal("set") {
                dynamic("value") {
                    execute<ProxyCommandSender> { sender: ProxyCommandSender, context: CommandContext<ProxyCommandSender>, _: String ->
                        sender.castSafely<Player>().let {
                            clipboard[it!!] = context["value"]
                        }
                        sender.sendLang("Lore-Clipboard-Set", context["value"])
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

                        sender.castSafely<Player>().let {
                            clipboard[it!!] = value
                        }

                        if (!silent) sender.sendLang("Lore-Clipboard-Set", value)
                    }
                }
            }

            literal("clear") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, _: String ->
                    sender.castSafely<Player>().let {
                        clipboard[it!!] = ""
                        sender.sendLang("Lore-Clipboard-Clear")
                    }
                }
            }.dynamic("options") {
                execute<ProxyCommandSender> { sender: ProxyCommandSender, _: CommandContext<ProxyCommandSender>, content: String ->
                    val args = CommandUtil.parseOptions(content.split(" "))
                    var silent = false

                    for ((k, _) in args) {
                        when (k.lowercase()) {
                            "silent" -> silent = true
                        }
                    }

                    sender.castSafely<Player>().let {
                        clipboard[it!!] = ""
                    }

                    if (!silent) sender.sendLang("Lore-Clipboard-Clear")
                }
            }

        }
    }
}