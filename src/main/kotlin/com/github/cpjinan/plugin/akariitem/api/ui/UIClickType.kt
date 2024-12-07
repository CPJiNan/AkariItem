package com.github.cpjinan.plugin.akariitem.api.ui

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

enum class UIClickType(
    val button: String,
    val modifier: String
) {
    // 鼠标点击
    LEFT("LEFT", ""),
    RIGHT("RIGHT", ""),
    MIDDLE("MIDDLE", ""),
    SHIFT_LEFT("LEFT", "SHIFT"),
    SHIFT_RIGHT("RIGHT", "SHIFT"),
    WINDOW_BORDER_LEFT("LEFT", "BORDER"),
    WINDOW_BORDER_RIGHT("RIGHT", "BORDER"),
    DOUBLE_CLICK("DOUBLE", ""),

    // 数字按键
    NUMBER_KEY("NUMBER", ""),
    NUMBER_KEY_1("NUMBER_1", ""),
    NUMBER_KEY_2("NUMBER_2", ""),
    NUMBER_KEY_3("NUMBER_3", ""),
    NUMBER_KEY_4("NUMBER_4", ""),
    NUMBER_KEY_5("NUMBER_5", ""),
    NUMBER_KEY_6("NUMBER_6", ""),
    NUMBER_KEY_7("NUMBER_7", ""),
    NUMBER_KEY_8("NUMBER_8", ""),
    NUMBER_KEY_9("NUMBER_9", ""),

    // 丢弃
    DROP("DROP", ""),
    CONTROL_DROP("DROP", "CONTROL"),

    // 杂项
    CREATIVE("CREATIVE", ""),
    SWAP_OFFHAND("OFFHAND", ""),
    UNKNOWN("", "");

    companion object {
        fun isLeftClick(type: UIClickType): Boolean {
            return type.button == "LEFT"
        }

        fun isRightClick(type: UIClickType): Boolean {
            return type.button == "RIGHT"
        }

        fun isShiftClick(type: UIClickType): Boolean {
            return type.modifier == "SHIFT"
        }

        fun isNumberKeyClick(type: UIClickType): Boolean {
            return type.button.startsWith("NUMBER")
        }

        fun toBukkitType(type: UIClickType): ClickType {
            return when (type) {
                LEFT -> ClickType.LEFT
                RIGHT -> ClickType.RIGHT
                MIDDLE -> ClickType.MIDDLE
                SHIFT_LEFT -> ClickType.SHIFT_LEFT
                SHIFT_RIGHT -> ClickType.SHIFT_RIGHT
                WINDOW_BORDER_LEFT -> ClickType.WINDOW_BORDER_LEFT
                WINDOW_BORDER_RIGHT -> ClickType.WINDOW_BORDER_RIGHT
                DOUBLE_CLICK -> ClickType.DOUBLE_CLICK
                NUMBER_KEY -> ClickType.NUMBER_KEY
                NUMBER_KEY_1 -> ClickType.NUMBER_KEY
                NUMBER_KEY_2 -> ClickType.NUMBER_KEY
                NUMBER_KEY_3 -> ClickType.NUMBER_KEY
                NUMBER_KEY_4 -> ClickType.NUMBER_KEY
                NUMBER_KEY_5 -> ClickType.NUMBER_KEY
                NUMBER_KEY_6 -> ClickType.NUMBER_KEY
                NUMBER_KEY_7 -> ClickType.NUMBER_KEY
                NUMBER_KEY_8 -> ClickType.NUMBER_KEY
                NUMBER_KEY_9 -> ClickType.NUMBER_KEY
                DROP -> ClickType.DROP
                CONTROL_DROP -> ClickType.DROP
                SWAP_OFFHAND -> ClickType.SWAP_OFFHAND
                CREATIVE -> ClickType.CREATIVE
                UNKNOWN -> ClickType.UNKNOWN
            }
        }

        fun fromClickType(clickType: ClickType): UIClickType {
            return when (clickType) {
                ClickType.LEFT -> LEFT
                ClickType.RIGHT -> RIGHT
                ClickType.MIDDLE -> MIDDLE
                ClickType.SHIFT_LEFT -> SHIFT_LEFT
                ClickType.SHIFT_RIGHT -> SHIFT_RIGHT
                ClickType.WINDOW_BORDER_LEFT -> WINDOW_BORDER_LEFT
                ClickType.WINDOW_BORDER_RIGHT -> WINDOW_BORDER_RIGHT
                ClickType.DOUBLE_CLICK -> DOUBLE_CLICK
                ClickType.NUMBER_KEY -> NUMBER_KEY
                ClickType.DROP -> DROP
                ClickType.CONTROL_DROP -> CONTROL_DROP
                ClickType.SWAP_OFFHAND -> SWAP_OFFHAND
                ClickType.CREATIVE -> CREATIVE
                ClickType.UNKNOWN -> UNKNOWN
                else -> UNKNOWN
            }
        }

        fun fromClickEvent(event: InventoryClickEvent): UIClickType {
            val buttonType = when (event.click) {
                ClickType.LEFT -> "LEFT"
                ClickType.RIGHT -> "RIGHT"
                ClickType.MIDDLE -> "MIDDLE"
                ClickType.SHIFT_LEFT -> "LEFT"
                ClickType.SHIFT_RIGHT -> "RIGHT"
                ClickType.WINDOW_BORDER_LEFT -> "LEFT"
                ClickType.WINDOW_BORDER_RIGHT -> "RIGHT"
                ClickType.DOUBLE_CLICK -> "DOUBLE"
                ClickType.NUMBER_KEY -> "NUMBER"
                ClickType.DROP -> "DROP"
                ClickType.CONTROL_DROP -> "DROP"
                ClickType.SWAP_OFFHAND -> "OFFHAND"
                ClickType.CREATIVE -> "CREATIVE"
                ClickType.UNKNOWN -> ""
                else -> ""
            }

            val modifier = when {
                event.isShiftClick -> "SHIFT"
                event.click == ClickType.WINDOW_BORDER_LEFT || event.click == ClickType.WINDOW_BORDER_RIGHT -> "BORDER"
                event.click == ClickType.CONTROL_DROP -> "CONTROL"
                else -> ""
            }

            if (event.hotbarButton != -1) {
                return UIClickType.valueOf("NUMBER_KEY_${event.hotbarButton + 1}")
            }

            return UIClickType.valueOf("$buttonType$modifier".takeIf { it.isNotEmpty() } ?: "UNKNOWN")
        }

        fun equals(type1: UIClickType, type2: UIClickType): Boolean {
            return type1.button == type2.button && type1.modifier == type2.modifier
        }

        fun equals(event: InventoryClickEvent, type: UIClickType): Boolean {
            return equals(fromClickEvent(event), type)
        }
    }
}
