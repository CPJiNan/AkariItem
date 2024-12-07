package com.github.cpjinan.plugin.akariitem.common.script.kether

import com.github.cpjinan.plugin.akariitem.api.UIAPI.closeUI
import org.bukkit.entity.Player
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

object UIScript {
    @KetherParser(["closeui"], namespace = "akariitem", shared = true)
    fun parser() = scriptParser {
        CloseUIScriptAction()
    }

    class CloseUIScriptAction : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val sender = frame.script().sender
            sender?.castSafely<Player>()?.closeUI()
            return CompletableFuture.completedFuture(null)
        }
    }
}