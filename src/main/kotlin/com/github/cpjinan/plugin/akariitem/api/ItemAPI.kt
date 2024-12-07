package com.github.cpjinan.plugin.akariitem.api

import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil
import com.github.cpjinan.plugin.akariitem.utils.ConfigUtil.getConfigSections
import com.github.cpjinan.plugin.akariitem.utils.FileUtil
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil.getItemFromConfig
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil.getItemFromPlugin
import com.github.cpjinan.plugin.akariitem.utils.ItemUtil.saveItemToConfig
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File

object ItemAPI {
    private var itemFiles: ArrayList<File> = arrayListOf()
    private var itemSections: HashMap<String, ConfigurationSection> = hashMapOf()
    private var itemNames: ArrayList<String> = arrayListOf()
    private var itemConfig: YamlConfiguration = YamlConfiguration()

    init {
        reloadItem()
    }

    /**
     * 重载物品配置文件
     * @author CPJiNan
     */
    fun reloadItem() {
        itemFiles = FileUtil.getFile(File(FileUtil.dataFolder, "item"), true)
            .filter { it.name.endsWith(".yml") }.toCollection(ArrayList())
        itemSections = itemFiles.getConfigSections()
        itemNames = itemSections.map { it.key }.toCollection(ArrayList())
        itemConfig = ConfigUtil.getMergedConfig(itemSections)
    }

    /**
     * 获取所有物品的配置文件
     * @return 物品配置文件列表
     * @author CPJiNan
     */
    fun getItemFiles(): ArrayList<File> = itemFiles

    /**
     * 获取所有物品的配置节点
     * @return 物品配置节点列表 (由 物品ID 及其 配置节点 组成)
     * @author CPJiNan
     */
    fun getItemSections(): HashMap<String, ConfigurationSection> = itemSections

    /**
     * 获取所有物品的名称
     * @return 物品名称列表
     * @author CPJiNan
     */
    fun getItemNames(): ArrayList<String> =
        itemNames.filter { itemSections[it]?.getBoolean("Hide") != true }.toCollection(ArrayList())

    /**
     * 获取所有物品配置合并后的新配置
     * @return 物品配置
     * @author CPJiNan
     */
    fun getItemConfig(): YamlConfiguration = itemConfig

    /**
     * 保存物品至配置文件
     * @param item 物品
     * @param file 配置文件
     * @param id 物品 ID
     * @author CPJiNan
     */
    fun saveItem(item: ItemStack, file: File, id: String) {
        val config = YamlConfiguration.loadConfiguration(file)
        config.set(id, null)
        saveItemToConfig(item, config, id)
        config.save(file)
    }

    /**
     * 保存物品至配置文件
     * @param item 物品
     * @param file 配置文件路径 (插件目录为根目录)
     * @param id 物品 ID
     * @author CPJiNan
     */
    fun saveItem(item: ItemStack, file: String, id: String) {
        val itemFile = FileUtil.getFileOrCreate(file)
        val config = YamlConfiguration.loadConfiguration(itemFile)
        config.set(id, null)
        saveItemToConfig(item, config, id)
        config.save(itemFile)
    }

    /**
     * 从配置文件获取物品
     * @param id 物品 ID
     * @return 指定物品的 ItemStack
     * @author CPJiNan
     */
    fun getItem(id: String): ItemStack? {
        return getItemFromConfig(itemConfig, id)
    }

    /**
     * 从配置文件获取物品
     * @param file 配置文件
     * @param id 物品 ID
     * @return 指定物品的 ItemStack
     * @author CPJiNan
     */
    fun getItem(file: File, id: String): ItemStack? {
        val config = YamlConfiguration.loadConfiguration(file)
        return getItemFromConfig(config, id)
    }

    /**
     * 从配置文件获取物品
     * @param file 配置文件路径 (插件目录为根目录)
     * @param id 物品 ID
     * @return 指定物品的 ItemStack
     * @author CPJiNan
     */
    fun getItem(file: String, id: String): ItemStack? {
        val config = YamlConfiguration.loadConfiguration(File(FileUtil.dataFolder, file))
        return getItemFromConfig(config, id)
    }

    /**
     * 从配置文件获取物品
     * @param config 配置文件
     * @param id 物品 ID
     * @return 指定物品的 ItemStack
     * @author CPJiNan
     */
    fun getItem(config: YamlConfiguration, id: String): ItemStack? {
        return getItemFromConfig(config, id)
    }

    /**
     * 从配置文件获取物品
     * @param config 配置文件节点
     * @return 指定物品的 ItemStack
     * @author CPJiNan
     */
    fun getItem(config: ConfigurationSection): ItemStack? {
        return getItemFromConfig(config)
    }

    /**
     * 从其他插件获取物品
     * @param plugin 插件名称
     * @param id 物品 ID
     * @param player 执行玩家 (默认为 null)
     * @return 指定物品的 ItemStack
     * @author CPJiNan
     */
    fun getItem(plugin: String, id: String, player: Player? = null): ItemStack? {
        return getItemFromPlugin(plugin, id, player)
    }
}