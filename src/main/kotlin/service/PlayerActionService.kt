package service

import entity.Card
import entity.CardValue
import entity.Player
import kotlin.math.min

/**
 * class to control the player action like play a card, draw card and swap card.
 */
class PlayerActionService(private var rootService: RootService) : AbstractRefreshingService() {


    /**
     * Attempts to play a card from the player's hand onto the target central stack.
     * the player can only play card if he did not make another action.
     * @param card The card to be played.
     * @param targetStack The central stack where the card should be placed.
     * @throws IllegalStateException when game is not running
     */
    fun playCard(card: Card, targetStack: Int) {
        checkNotNull(rootService.currentGame){throw IllegalStateException("game is not running!.")}

        //determine which player play.
        val currentPlayer: Player? = rootService.gameService.currentPlayer()
        //check if the player already did any action.
        if (rootService.currentGame?.playerAction == false){
            if(targetStack == 1 && canPlayCard(card, rootService.currentGame?.playStackOne?.first())){
                rootService.currentGame?.playStackOne?.addFirst(card)
                currentPlayer?.handCards?.remove(card)
                //remark that player did action, so he can not do another action
                rootService.currentGame?.playerAction = true
                rootService.currentGame?.lastSkip = false
                onAllRefreshables { refreshAfterPlayCard(card, targetStack) }
            }else if (targetStack == 2 && canPlayCard(card, rootService.currentGame?.playStackTwo?.first())){
                rootService.currentGame?.playStackTwo?.addFirst(card)
                currentPlayer?.handCards?.remove(card)
                rootService.currentGame?.playerAction = true
                rootService.currentGame?.lastSkip = false
                onAllRefreshables { refreshAfterPlayCard(card, targetStack) }
            }
        }
    }

    /**
     * Allows the player to draw one card from their draw stack if conditions are met.
     * A player can draw a card if they have fewer than 10 cards in hand and the draw stack is not empty.
     * @throws IllegalStateException if the game is not running.
     */
    fun drawCard() {

        checkNotNull(rootService.currentGame){throw IllegalStateException("game is not running!.")}

        val currentPlayer : Player? = rootService.gameService.currentPlayer()
        if (canDrawCard() && rootService.currentGame?.playerAction == false) {
            /* first card will be removed and returned as input for handCards */
            currentPlayer?.handCards?.addLast(currentPlayer.drawStack.removeLast())
            rootService.currentGame?.lastSkip = false
            rootService.currentGame?.playerAction = true

            //the last card in the hand of the player is the card the drawn.
            onAllRefreshables { currentPlayer?.handCards?.let { refreshAfterDraw(it.last()) } }
        }
    }

    /**
     * Replaces the player's hand cards by shuffling the draw stack and give player 5 new cards.
     * This action is allowed if the player has at least 8 cards in hand and the draw stack is not empty.
     * @throws IllegalStateException when no game runs.
     */
    fun swapHand(){
        checkNotNull(rootService.currentGame){throw IllegalStateException("game is not running!.")}

        val currentPlayer : Player? = rootService.gameService.currentPlayer()
        if (canSwapHand() && rootService.currentGame?.playerAction == false) {
            // Merge current hand cards into the draw stack and remove them from handStack.
            currentPlayer?.drawStack?.addAll(currentPlayer.handCards)
            currentPlayer?.handCards?.clear()

            // Shuffle the draw stack.
            currentPlayer?.drawStack?.shuffle()
            // Draw 5 new cards, or as many as available if fewer than 5.
            val drawCount = currentPlayer?.drawStack?.size?.let { min(5, it) }

            //repeat instead of For-loop
            if (drawCount != null) {
                repeat(drawCount) {
                    currentPlayer.handCards.addLast(currentPlayer.drawStack.removeFirst())
                }
            }
            rootService.currentGame?.lastSkip = false
            rootService.currentGame?.playerAction = true
            onAllRefreshables { refreshAfterSwapHand() }

        }
    }

    /**
     *[Player] can skip his turn if no other action  is not possible.
     * lastSkip will be after this methode true.
     * when player make any action, it will be set to false.
     */
    fun skipTurn() {
        //if both player skipp, game end.
        if (rootService.currentGame?.lastSkip == true && rootService.currentGame?.playerAction == false && canSkip()){
            rootService.gameService.endGame()
            return
        }
        if (canSkip()){
            this.rootService.gameService.endTurn()
            rootService.currentGame?.lastSkip = true
        }
    }

    /**
     *check if no other action not possible.
     * Other action can play a card , swap hand or draw card.
     * @return true if player can skip.
     */
    fun canSkip(): Boolean{
        val currentPlayer : Player? = rootService.gameService.currentPlayer()
        return !(canSwapHand() || canDrawCard() || (currentPlayer?.handCards?.size == 0))
    }

    /**
     * Validates if the card to play can be placed on top of the current top card of the target stack.
     * Rules:
     * Standard move: card value must be one rank higher or lower.
     * If the same suit is played, a move of two ranks higher or lower is also allowed.
     * @param cardToPlay the card which will the player play.
     * @param topCard the top card in the central playStack.
     */
    private fun canPlayCard(cardToPlay: Card?, topCard: Card?): Boolean {
        if (topCard == null) return true

        val cardOrder = listOf(
            CardValue.TWO, CardValue.THREE, CardValue.FOUR, CardValue.FIVE,
            CardValue.SIX, CardValue.SEVEN, CardValue.EIGHT, CardValue.NINE,
            CardValue.TEN, CardValue.JACK, CardValue.QUEEN, CardValue.KING, CardValue.ACE
        )
        val indexTop = cardOrder.indexOf(topCard.cardValue)
        val indexPlay = cardToPlay?.let { cardOrder.indexOf(it.cardValue) }

        // Check for standard one-rank move
        if (cardToPlay != null) {
            if (cardToPlay.cardSuit != topCard.cardSuit) {
                if ((indexPlay == (indexTop + 1) % cardOrder.size) ||
                    (indexPlay == (indexTop - 1 + cardOrder.size) % cardOrder.size)) {
                    return true
                }
            } else {
                // For the same suit, two-rank moves are allowed in addition to one-rank moves.
                if ((indexPlay == (indexTop + 2) % cardOrder.size) ||
                    (indexPlay == (indexTop - 2 + cardOrder.size) % cardOrder.size)) {
                    return true
                }
                if ((indexPlay == (indexTop + 1) % cardOrder.size) ||
                    (indexPlay == (indexTop - 1 + cardOrder.size) % cardOrder.size)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * check if player can draw one card from his drawCard
     * @return true if currentPlayer can draw a [Card], false otherwise.
     */
    fun canDrawCard(): Boolean {

        val currentPlayer : Player? = rootService.gameService.currentPlayer()
        val handCardSize = currentPlayer?.handCards?.size
        if (handCardSize != null) {
            return handCardSize < 10 &&
                    currentPlayer.drawStack.isNotEmpty()
        }

        return false
    }

    /**
     *Checks whether cards in hand can be swapped (replace cards in hand)
     * this means that the cards in hand are shuffled into the drawStack and the player
     * then become 5 new cards from this pile.
     * @return true if the currentPlayer have min 8 cards and the drawStack is not empty.
     */
    fun canSwapHand(): Boolean {
        //first check which player play, then make the action.
        val currentPlayer : Player? = rootService.gameService.currentPlayer()
        val handCardSize = currentPlayer?.handCards?.size
        if (handCardSize != null) {
            return (handCardSize >= 8  &&
                    currentPlayer.drawStack.isNotEmpty())
        }

        return false
    }
}
