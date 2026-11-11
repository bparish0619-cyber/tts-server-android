package com.github.jing332.tts_server_android.compose.systts.list

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jing332.common.utils.FileUtils.readAllText
import com.github.jing332.database.dbm
import com.github.jing332.database.entities.AbstractListGroup.Companion.DEFAULT_GROUP_ID
import com.github.jing332.database.entities.plugin.Plugin
import com.github.jing332.database.entities.systts.GroupWithSystemTts
import com.github.jing332.database.entities.systts.SystemTtsV2
import com.github.jing332.database.entities.systts.TtsConfigurationDTO
import com.github.jing332.database.entities.systts.source.PluginTtsSource
import com.github.jing332.tts_server_android.R
import com.github.jing332.tts_server_android.conf.SystemTtsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import java.util.Collections

class ListManagerViewModel : ViewModel() {
    companion object {
        const val TAG = "ListManagerViewModel"

        // Plugin id of the bundled ElevenLabs plugin, as declared in the
        // "id" field of its PluginJS block in
        // assets/defaultData/plugin-elevenlabs.js. The seeded plugin and
        // the seeded configuration below are tied together by this id.
        const val ELEVENLABS_PLUGIN_ID = "io.elevenlabs.api"
    }

    private val _list =
        MutableStateFlow<List<GroupWithSystemTts>>(
            emptyList()
        )
    val list: StateFlow<List<GroupWithSystemTts>> get() = _list

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.systemTtsV2.updateAllOrder()
            dbm.systemTtsV2.flowAllGroupWithTts().conflate().collect {
                Log.d(TAG, "update list: ${it.size}")
                _list.tryEmit(it)
            }
        }
    }

    fun updateTtsEnabled(
        item: SystemTtsV2,
        enabled: Boolean,
    ) {
        if (!SystemTtsConfig.isVoiceMultipleEnabled.value && enabled) {
            val itemConfig = (item.config as? TtsConfigurationDTO)
            if (itemConfig != null)
                dbm.systemTtsV2.allEnabled.forEach { systts ->
                    if (systts.config is TtsConfigurationDTO) {
                        val config = systts.config as TtsConfigurationDTO
                        if (config.speechRule.target == itemConfig.speechRule.target) {
                            if (config.speechRule.tagRuleId == itemConfig.speechRule.tagRuleId
                                && config.speechRule.tag == itemConfig.speechRule.tag
                                && config.speechRule.tagName == itemConfig.speechRule.tagName
                                && config.speechRule.isStandby == itemConfig.speechRule.isStandby
                            )
                                dbm.systemTtsV2.update(systts.copy(isEnabled = false))
                        }
                    }
                }
        }

        dbm.systemTtsV2.update(item.copy(isEnabled = enabled))
    }

    fun updateGroupEnable(
        item: GroupWithSystemTts,
        enabled: Boolean,
    ) {
        if (!SystemTtsConfig.isGroupMultipleEnabled.value && enabled) {
            list.value.forEach {
                it.list.forEach { systts ->
                    if (systts.isEnabled)
                        dbm.systemTtsV2.update(systts.copy(isEnabled = false))
                }
            }
        }

        dbm.systemTtsV2.update(
            *item.list.filter { it.isEnabled != enabled }.map { it.copy(isEnabled = enabled) }
                .toTypedArray()
        )
    }

    fun reorder(from: ItemPosition, to: ItemPosition) {
        if (from.key is String && to.key is String) {
            val fromKey = from.key as String
            val toKey = to.key as String

            if (fromKey.startsWith("g") && toKey.startsWith("g")) {
                val mList = list.value.map { it.group }.toMutableList()

                val fromId = fromKey.substring(2).toLong()
                val fromIndex = mList.indexOfFirst { it.id == fromId }

                val toId = toKey.substring(2).toLong()
                val toIndex = mList.indexOfFirst { it.id == toId }

                try {
                    Collections.swap(mList, fromIndex, toIndex)
                } catch (_: IndexOutOfBoundsException) {
                    return
                }
                mList.forEachIndexed { index, systemTtsGroup ->
                    if (systemTtsGroup.order != index)
                        dbm.systemTtsV2.updateGroup(systemTtsGroup.copy(order = index))
                }
            } else if (!fromKey.startsWith("g") && !toKey.startsWith("g")) {
                val (fromGId, fromId) = fromKey.split("_").map { it.toLong() }
                val (toGId, toId) = toKey.split("_").map { it.toLong() }
                if (fromGId != toGId) return

                val listInGroup = findListInGroup(fromGId).toMutableList()
                val fromIndex = listInGroup.indexOfFirst { it.id == fromId }
                val toIndex = listInGroup.indexOfFirst { it.id == toId }
                Log.d(TAG, "fromIndex: $fromIndex, toIndex: $toIndex")

                try {
                    Collections.swap(listInGroup, fromIndex, toIndex)
                } catch (_: IndexOutOfBoundsException) {
                    return
                }

                listInGroup.forEachIndexed { index, systts ->
                    Log.d(TAG, "$index ${systts.displayName}")
                    if (systts.order != index)
                        dbm.systemTtsV2.update(systts.copy(order = index))
                }
            }

        }
    }

    private fun findListInGroup(groupId: Long): List<SystemTtsV2> {
        return list.value.find { it.group.id == groupId }?.list?.sortedBy { it.order }
            ?: emptyList()
    }

    fun checkListData(context: Context) {
        dbm.systemTtsV2.getGroup(DEFAULT_GROUP_ID) ?: kotlin.run {
            dbm.systemTtsV2.insertGroup(
                com.github.jing332.database.entities.systts.SystemTtsGroup(
                    DEFAULT_GROUP_ID,
                    context.getString(R.string.default_group),
                    dbm.systemTtsV2.groupCount
                )
            )
        }

        if (dbm.systemTtsV2.count == 0)
            importDefaultListData(context)
    }

    private fun importDefaultListData(context: Context) {
        // First-run defaults for this fork. This replaces upstream's old
        // list.json preset import (which seeded Chinese demo voices): a
        // fresh install now gets the bundled ElevenLabs plugin plus one
        // ready-to-use ElevenLabs configuration, so the only setup left
        // is entering your own ElevenLabs API key in the plugin variables.
        //
        // checkListData() calls this only when the configuration list is
        // completely empty: fresh install, or after every configuration
        // was deleted. Installs that already have configurations are
        // never touched, and no existing data is migrated or removed.
        viewModelScope.launch(Dispatchers.IO) {
            seedElevenLabsPlugin(context)
            seedElevenLabsConfig()
        }
    }

    // CHANGE-ME (bundled plugin): the plugin code is the asset
    // defaultData/plugin-elevenlabs.js, taken unchanged from the upstream
    // project's plugin collection. To bundle a different plugin instead,
    // drop its .js file into assets/defaultData and adjust the asset name,
    // ELEVENLABS_PLUGIN_ID, and the metadata below to match its PluginJS
    // block.
    private fun seedElevenLabsPlugin(context: Context) {
        if (dbm.pluginDao.getByPluginId(ELEVENLABS_PLUGIN_ID) != null) return

        val code =
            context.assets.open("defaultData/plugin-elevenlabs.js").readAllText()
        dbm.pluginDao.insert(
            Plugin(
                // Must stay enabled: the speech engine only resolves
                // plugins that are enabled (SpeechServiceFactory).
                isEnabled = true,
                version = 1,
                name = "ElevenLabs",
                pluginId = ELEVENLABS_PLUGIN_ID,
                author = "Generated AI",
                code = code,
                // Declared variables, mirrored from the "vars" block in
                // the plugin code. apiKey is present but EMPTY on
                // purpose: the user enters their own key under the
                // plugin's Variables. Never hardcode a real API key here.
                defVars = mapOf("apiKey" to mapOf("label" to "ElevenLabs API Key")),
                userVars = mapOf("apiKey" to ""),
            )
        )
    }

    private fun seedElevenLabsConfig() {
        // Re-check inside the coroutine so a second call can never
        // duplicate the seeded configuration.
        if (dbm.systemTtsV2.count != 0) return

        dbm.systemTtsV2.insert(
            SystemTtsV2(
                displayName = "ElevenLabs",
                groupId = DEFAULT_GROUP_ID,
                isEnabled = true,
                order = 0,
                config = TtsConfigurationDTO(
                    source = PluginTtsSource(
                        // CHANGE-ME (seeded voice): default locale and
                        // voice for the seeded configuration. The voice
                        // is Zoe (id XdflFrQO8wbGpWMNZHFr); swap in any
                        // voice id from your ElevenLabs account to change
                        // it. The plugin itself ignores the locale when
                        // synthesizing; en-US also makes Android expose
                        // this configuration as a US English voice.
                        locale = "en-US",
                        voice = "XdflFrQO8wbGpWMNZHFr", // Zoe
                        pluginId = ELEVENLABS_PLUGIN_ID,
                    )
                ),
            )
        )
    }

}