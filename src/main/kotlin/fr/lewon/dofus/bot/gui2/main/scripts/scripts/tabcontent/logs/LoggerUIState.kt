package fr.lewon.dofus.bot.gui2.main.scripts.scripts.tabcontent.logs

import androidx.compose.foundation.ScrollState
import fr.lewon.dofus.bot.core.logs.LogItem

data class LoggerUIState(
    val loggerType: LoggerUIType,
    val autoScroll: Boolean = true,
    val pauseLogs: Boolean = false,
    val logItems: List<LogItem> = emptyList(),
    val expandedLogItem: LogItem? = null,
    val scrollState: ScrollState = ScrollState(0),
)