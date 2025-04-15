package gui

import entity.Card

/**
 * Interface that allow the service layer to communicate with the UI classes, when something changed
 */
interface Refreshable {

    /**
     * refresh the scene after game is started
     */
    fun refreshAfterStartNewGame() {}

    /**
     *refresh after start turn
     */
    fun refreshAfterStartTurn() {}

    /**
     *refresh after the player ends his turn.
     */
    fun refreshAfterEndTurn() {}

    /**
     * refresh after the endgame and show the winner.
     * @param gameWinner the player name who wins the game
     */
    fun refreshAfterEndGame(gameWinner: String) {}

    /**
     * refresh after the player play card, so the card move from the hand of the player to the playStack.
     * @param card that player want to play.
     * @param stackNumber that the card move to there.
     */
    fun refreshAfterPlayCard(card: Card, stackNumber: Int) {}

    /**
     *refresh after the player draw card(take card) from his drawStack
     * @param card that player draw it from the drawStack
     */
    fun refreshAfterDraw(card: Card) {}

    /**
     * refresh after the player swap his handCard with the drawStack
     */
    fun refreshAfterSwapHand() {}
}
