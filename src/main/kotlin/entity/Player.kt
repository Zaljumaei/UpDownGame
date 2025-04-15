package entity

/**
 * Entity class for player, which have name, handCards and drawStack cards.
 * At the start player have 5 cards.
 * When player have min. 8 cards in the hand and the drawStack is not empty,
 * he can shuffle the drawStack and after that draw 5 cards to the hand.
 *When player has max. 9 cards, he can draw one card from drawStack, so player can have max. 10 cards.
 * @param name name of the player, who will play.
 * @param handCards cards that player can put them on the playStackOne or playStackTwo.
 * @param drawStack player have 0-20 [Card] in the draw stack. when he has 8 on the hand and mix them wit the draw,
 * the drawStack can be changed.
 */
class Player(
    val name: String,
    val handCards: ArrayDeque<Card> = ArrayDeque(),
    val drawStack: ArrayDeque<Card> = ArrayDeque(),
)
