package com.github.cpjinan.plugin.akariitem.utils

object CooldownUtil {
    private val cooldownData = HashMap<String, Long>()

    /**
     * 设置冷却时间
     * @param id 标识符
     * @param time 冷却时间 (毫秒)
     * 1 s = 20 tick, 1 s = 1000 ms, 1 tick = 50 ms
     */
    @JvmStatic
    fun setCooldown(id: String, time: Long) {
        cooldownData[id] = System.currentTimeMillis() + time
    }

    /**
     * 增加冷却时间
     * @param id 标识符
     * @param time 增加的冷却时间 (毫秒)
     * 1 s = 20 tick, 1 s = 1000 ms, 1 tick = 50 ms
     */
    @JvmStatic
    fun addCooldown(id: String, time: Long) {
        val currentCooldown = cooldownData[id]
        val newEndTime = if (currentCooldown != null) {
            currentCooldown + time
        } else {
            System.currentTimeMillis() + time
        }
        cooldownData[id] = newEndTime
    }

    /**
     * 减少冷却时间
     * @param id 标识符
     * @param time 减少的冷却时间 (毫秒)
     * 1 s = 20 tick, 1 s = 1000 ms, 1 tick = 50 ms
     */
    @JvmStatic
    fun removeCooldown(id: String, time: Long) {
        val currentCooldown = cooldownData[id]
        if (currentCooldown != null) {
            val newEndTime = currentCooldown - time
            cooldownData[id] = maxOf(newEndTime, System.currentTimeMillis())
        }
    }

    /**
     * 重置冷却时间
     * @param id 标识符
     * 1 s = 20 tick, 1 s = 1000 ms, 1 tick = 50 ms
     */
    @JvmStatic
    fun resetCooldown(id: String) {
        cooldownData.remove(id)
    }

    /**
     * 获取是否冷却结束
     * @param id 标识符
     * @return 冷却是否冷却结束
     * 1 s = 20 tick, 1 s = 1000 ms, 1 tick = 50 ms
     */
    @JvmStatic
    fun isCooldownFinish(id: String): Boolean {
        val endTime = cooldownData[id] ?: return true
        return System.currentTimeMillis() > endTime
    }

    /**
     * 获取冷却剩余时间
     * @param id 标识符
     * @return 剩余冷却时间 (毫秒)
     * 1 s = 20 tick, 1 s = 1000 ms, 1 tick = 50 ms
     */
    @JvmStatic
    fun getCooldown(id: String): Long {
        val endTime = cooldownData[id] ?: return 0
        val cooldown = endTime - System.currentTimeMillis()
        return if (cooldown > 0) cooldown else 0
    }
}