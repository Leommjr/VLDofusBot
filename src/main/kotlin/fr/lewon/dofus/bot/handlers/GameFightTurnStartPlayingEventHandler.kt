package fr.lewon.dofus.bot.handlers

import fr.lewon.dofus.bot.sniffer.DofusConnection
import fr.lewon.dofus.bot.sniffer.model.messages.fight.GameFightTurnStartPlayingMessage
import fr.lewon.dofus.bot.sniffer.store.EventHandler
import fr.lewon.dofus.bot.util.network.GameSnifferUtil

object GameFightTurnStartPlayingEventHandler : EventHandler<GameFightTurnStartPlayingMessage> {

    override fun onEventReceived(socketResult: GameFightTurnStartPlayingMessage, connection: DofusConnection) {
        val gameInfo = GameSnifferUtil.getGameInfoByConnection(connection)
        gameInfo.logger.debug("Turn starting")
    }
}