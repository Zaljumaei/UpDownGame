package service

import entity.*
import kotlin.random.Random.Default.nextBoolean

/**
 *this class control the game.
 * @param rootService to access the entity layer.
 */
class GameService(private var rootService: RootService) : AbstractRefreshingService() {

    /**
     *the game will start from here and the cards will be shuffled
     * this methode use the createCards methode two create the cards
     * and distribute the 52 cards in the right stack and hands of player
     * @param playerOne name of first player.
     * @param playerTwo name of second player.
     * @throws IllegalArgumentException if one or both of the names is empty.
     * @throws IllegalStateException if game is already running.
     */
    fun startNewGame(playerOne : String, playerTwo : String){

        //check if a game is running.
        if(this.rootService.currentGame != null){
            throw IllegalStateException("Game already running")
        }

        if (playerOne.isEmpty() || playerTwo.isEmpty()){
            throw IllegalArgumentException("player have to give a name!")
        }

        val allCards : MutableList<ArrayDeque<Card>> = creatCards()

        val drawStackOne = allCards[0]
        val drawStackTwo = allCards[1]

        val handCardsOne = allCards[2]
        val handCardsTwo = allCards[3]

        //take one card from stack and put it in the playStackOne
        val playStackOne = ArrayDeque<Card>()
        playStackOne.add(drawStackOne[0])
        //remove the card, so it does not exist twice.
        drawStackOne.removeAt(0)

        //do the same as for playStackTwo
        val playStackTwo = ArrayDeque<Card>()
        playStackTwo.add(drawStackTwo[0])
        drawStackTwo.removeAt(0)

        //the currentPlayer will be selected randomly through nextBoolean()
        val gameUpAndDown = GameUpAndDown(
            playerOne = Player(playerOne, handCardsOne, drawStackOne),
            playerTwo = Player(playerTwo, handCardsTwo, drawStackTwo),
            currentPlayer = nextBoolean(),
            lastSkip = false,
            playerAction = false,
            playStackOne = playStackOne,
            playStackTwo = playStackTwo
        )
        rootService.currentGame = gameUpAndDown

        onAllRefreshables { refreshAfterStartNewGame() }
    }

    /**
     * start new turn after the [GameUpAndDown] is started.
     * @throws IllegalStateException when the game is not running.
     */
    fun startTurn(){

        if(rootService.currentGame != null){
            //determine which player play
            val currentPlayer : Player? = rootService.gameService.currentPlayer()

            if (currentPlayer  == rootService.currentGame?.playerOne){
                //playerTwo can start his turn.
                rootService.currentGame?.currentPlayer = false
                onAllRefreshables { refreshAfterStartTurn() }
            }else{
                //in this case the currentPlayer is playerTwo, so playerOne can start his  turn.
                rootService.currentGame?.currentPlayer = true
                onAllRefreshables { refreshAfterStartTurn() }
            }
        }else{
            throw IllegalStateException("The game is not started!.")
        }

    }

    /**
     * Ends the current turn.
     * set the playerAction to false, so the next player can make action
     * false indicate that player did not make any action.
     * when player make action like:  playCard , swapHand, drawCard, the playerAction should be then set to true
     * to prevent the player making another action in the same turn.
     * @throws IllegalStateException when the game is not running.
     */
    fun endTurn() {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not started!")

        if(rootService.currentGame?.playerAction == false){
            return
        }

        // Check if a player has won by emptying both hand and draw stacks.
        val currentPlayer = currentPlayer()
        if ((currentPlayer?.handCards?.isEmpty() == true) && currentPlayer.drawStack.isEmpty()){
            endGame()
            return
        }
        rootService.currentGame?.playerAction = false

        //cards of currentPlayer could be covered so another player can not see them.
        onAllRefreshables { refreshAfterEndTurn() }
        // Start the next turn.
        //startTurn()
    }

    /**
     * Ends the game and determines the winner.
     * The game ends either when a player has no cards left or when both players pass consecutively.
     * @throws IllegalStateException when the game is not running.
     */
    fun endGame() : String {
        val game = rootService.currentGame ?: throw IllegalStateException("Game not started!")

        val playerOne = game.playerOne
        val playerTwo = game.playerTwo
        var winnerName : String

        // Check if a player has emptied both hand and draw stack.
        if (playerOne.handCards.isEmpty() && playerOne.drawStack.isEmpty()) {
            winnerName = playerOne.name
            return "${playerOne.name} wins"
        }
        if (playerTwo.handCards.isEmpty() && playerTwo.drawStack.isEmpty()) {
            winnerName = playerTwo.name
            return "${playerTwo.name} wins"
        }

        // If both players passed consecutively, determine the winner by the fewest hand cards.
        val handCountOne = playerOne.handCards.size
        val handCountTwo = playerTwo.handCards.size
        when {
            handCountOne < handCountTwo -> winnerName = playerOne.name
            handCountTwo < handCountOne -> winnerName = playerTwo.name
            else -> winnerName = "The game is a draw!"
        }
        onAllRefreshables { refreshAfterEndGame(winnerName) }
        return winnerName
    }

    /****************************************Help Methode************************************************/

    /**
     *alle the [Card] will be created and shuffled und then returned as list of  [ArrayDeque] in List
     * @return MutableList<ArrayDeque<Card>> which have all cards separated in two drawStack and two hands.
     */
    fun creatCards(): MutableList<ArrayDeque<Card>> {
        val allCards : MutableList<Card> = mutableListOf()

        val cardSuites = setOf(CardSuit.CLUBS, CardSuit.DIAMONDS, CardSuit.HEARTS, CardSuit.SPADES)

        val cardValues = setOf(
            CardValue.TWO, CardValue.THREE, CardValue.FOUR, CardValue.FIVE,
            CardValue.SIX, CardValue.SEVEN, CardValue.EIGHT, CardValue.NINE,
            CardValue.TEN, CardValue.JACK, CardValue.QUEEN, CardValue.KING, CardValue.ACE
        )

         // for each iteration will creat one CardValue with four suite.
        for (card in cardSuites) {
            for (value in cardValues) {
                allCards.add(Card(card, value))
            }
        }
        //the cards will be shuffled.
        allCards.shuffle()

        /**list that hold the cardsList
         * List[0] hold DrawStack for the first Player
         * List[1] hold DrawStack for the second Player
         * List[2] hold 5 handCards for the firstPlayer
         * List[3] hold 5 handCards for the secondPlayer
         */
        val listOfCards = mutableListOf(
            ArrayDeque(allCards.subList(0, 21)),
            ArrayDeque(allCards.subList(21, 42)),
            ArrayDeque(allCards.subList(42, 47)),
            ArrayDeque(allCards.subList(47, 52))
        )
        return listOfCards
    }

    /**
     * determine which player is playing now.
     * playerOne when currentPlayer true, playerTwo when currentPlayer false.
     * @return currentPlayer
     */
    fun currentPlayer() : Player?{
        return if (rootService.currentGame?.currentPlayer == true){
            rootService.currentGame?.playerOne
        }else{
            rootService.currentGame?.playerTwo
        }
    }




}
