package com.github.cpjinan.plugin.akariitem.api.ui

import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

enum class UIClickType(
    val button: String,
    val modifier: String
) {
    ALL("ALL", ""),
    LEFT("LEFT", ""),
    RIGHT("RIGHT", ""),
    MIDDLE("MIDDLE", ""),
    SHIFT_LEFT("LEFT", "SHIFT"),
    SHIFT_RIGHT("RIGHT", "SHIFT"),
    OFFHAND("OFFHAND", ""),
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
    LEFT_MOUSE_DRAG_ADD("LEFT", "DRAG"),
    RIGHT_MOUSE_DRAG_ADD("RIGHT", "DRAG"),
    MIDDLE_MOUSE_DRAG_ADD("MIDDLE", "DRAG");

    companion object {
        fun isLeftClick(type: UIClickType): Boolean {
            return type == LEFT || type == SHIFT_LEFT
        }

        fun isRightClick(type: UIClickType): Boolean {
            return type == RIGHT || type == SHIFT_RIGHT
        }

        fun isShiftClick(type: UIClickType): Boolean {
            return type == SHIFT_LEFT || type == SHIFT_RIGHT
        }

        fun isNumberKeyClick(type: UIClickType): Boolean {
            return type.name.startsWith("NUMBER_KEY")
        }

        fun isItemMoveable(type: UIClickType): Boolean {
            return isShiftClick(type) || isNumberKeyClick(type) || type == LEFT_MOUSE_DRAG_ADD || type == RIGHT_MOUSE_DRAG_ADD
        }

        fun toBukkitType(type: UIClickType): ClickType {
            return when (type) {
                LEFT -> ClickType.LEFT
                RIGHT -> ClickType.RIGHT
                MIDDLE -> ClickType.MIDDLE
                else -> ClickType.UNKNOWN
            }
        }

        fun fromClickType(clickType: ClickType): UIClickType {
            return when (clickType) {
                ClickType.LEFT -> LEFT
                ClickType.RIGHT -> RIGHT
                ClickType.MIDDLE -> MIDDLE
                else -> ALL
            }
        }

        fun fromClickEvent(event: InventoryClickEvent): UIClickType {
            val buttonType = when (event.click) {
                ClickType.LEFT -> "LEFT"
                ClickType.RIGHT -> "RIGHT"
                ClickType.MIDDLE -> "MIDDLE"
                else -> "ALL"
            }

            val modifier = when {
                event.isShiftClick -> "SHIFT"
                event.cursor?.type != Material.AIR -> "OFFHAND"
                else -> ""
            }

            if (event.slotType == InventoryType.SlotType.QUICKBAR) {
                return UIClickType.valueOf("NUMBER_KEY_${event.hotbarButton + 1}")
            }

            if (event.click == ClickType.LEFT && event.action == InventoryAction.PICKUP_ALL) {
                return LEFT_MOUSE_DRAG_ADD
            }
            if (event.click == ClickType.RIGHT && event.action == InventoryAction.PICKUP_ALL) {
                return RIGHT_MOUSE_DRAG_ADD
            }
            if (event.click == ClickType.MIDDLE && event.action == InventoryAction.PICKUP_ALL) {
                return MIDDLE_MOUSE_DRAG_ADD
            }

            return UIClickType.valueOf("$buttonType$modifier".takeIf { it.isNotEmpty() } ?: "ALL")
        }

        fun equals(type1: UIClickType, type2: UIClickType): Boolean {
            return type1.button == type2.button && type1.modifier == type2.modifier
        }

        fun equals(event: InventoryClickEvent, type: UIClickType): Boolean {
            return equals(fromClickEvent(event), type)
        }
    }
}
