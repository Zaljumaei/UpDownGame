package entity

/**
 * Entity class, that manage which player can play and hold the lastSkip.
 * This class can be accessed from RootService.
 *
 * @param playerOne represent the first player.
 * @param playerTwo represent the second player.
 * @param currentPlayer because there is only two player in the game,(true or false).
 * @param lastSkip remark the last skip from one player, when the both player make two skip, the game is ended.
 * @param playerAction remark if player did action in this turn,and prevent him to make another action.
 * @param playStackOne represent the one of the central stack, which player put the cards on it.
 * @param playStackTwo represent the another central stack, which player put the cards on it.
 */
data class GameUpAndDown(
    val playerOne: Player ,
    val playerTwo: Player ,
    var currentPlayer: Boolean = false,
    var lastSkip: Boolean  = false,
    var playerAction: Boolean = false,
    val playStackOne: ArrayDeque<Card> = ArrayDeque(),
    val playStackTwo: ArrayDeque<Card> = ArrayDeque()
)
