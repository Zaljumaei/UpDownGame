package service

import entity.GameUpAndDown
import gui.Refreshable

/**
 * Main class of the service layer for the War card game. Provides access
 * to all other service classes and holds the [currentGame] state for these
 * services to access.
 */
class RootService {

    var gameService = GameService(this)
    val playerActionService = PlayerActionService(this)

    /**
     * The currently active [GameUpAndDown], which can have 'null' if no Game started.
     */
    var currentGame: GameUpAndDown? = null

    /**
     * Add [newRefreshable] to all services, which connected
     * to this root service
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
    }

    /**
     * Adds each of the provided [newRefreshables] to all services
     * connected to this root service
     */
    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}
