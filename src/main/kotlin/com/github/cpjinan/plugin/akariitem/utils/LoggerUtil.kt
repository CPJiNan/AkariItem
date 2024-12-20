package com.github.cpjinan.plugin.akariitem.utils

import taboolib.platform.BukkitPlugin
import taboolib.platform.util.*

/**
 * logger utils
 * @author CPJiNan
 * @since 2024/01/15
 */
object LoggerUtil {
    /**
     * send message
     * @param [message] message
     */
    @JvmStatic
    fun message(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendMessage(i)
        }
    }

    /**
     * send info
     * @param [message] info
     */
    @JvmStatic
    fun info(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendInfo(i)
        }
    }

    /**
     * send warn
     * @param [message] warn
     */
    @JvmStatic
    fun warn(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendWarn(i)
        }
    }

    /**
     * send error
     * @param [message] error
     */
    @JvmStatic
    fun error(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendError(i)
        }
    }

    /**
     * send info message
     * @param [message] info message
     */
    @JvmStatic
    fun infoMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendInfoMessage(i)
        }
    }

    /**
     * send warn message
     * @param [message] warn message
     */
    @JvmStatic
    fun warnMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendWarnMessage(i)
        }
    }

    /**
     * send error
     * @param [message] error message
     */
    @JvmStatic
    fun errorMessage(vararg message: String) {
        for (i in message) {
            BukkitPlugin.getInstance().server.consoleSender.sendErrorMessage(i)
        }
    }
}