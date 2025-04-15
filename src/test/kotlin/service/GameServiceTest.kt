package service

import entity.Card
import kotlin.test.*

/**
 * test class for the [GameService]
 */
class GameServiceTest {

    var rootService = RootService()

    /**
     * test if the game can be started.
     */
    @Test
    fun startNewGameTest() {
        //first test if the card can be created correctly
        creatCardsTest()
        rootService.gameService.startNewGame("Max", "Alex")
        assertNotNull(rootService.currentGame)

        //after the game started, the drawStack should be one card less (e.i 20 Cards),
        assertEquals(20, rootService.currentGame?.playerOne?.drawStack?.size)
        assertEquals(20, rootService.currentGame?.playerTwo?.drawStack?.size)

        //and the playStackOne and two should have one Card
        assertEquals(1, rootService.currentGame?.playStackOne?.size)
        assertEquals(1, rootService.currentGame?.playStackTwo?.size)

        //call startNewGame while another game is already running.
        assertFailsWith<IllegalStateException>{rootService.gameService.startNewGame("Max", "Alex")}

        //game with empty names should throw IllegalArgumentException.
        val rootServiceTwo = RootService()
        assertFailsWith<IllegalArgumentException>{rootServiceTwo.gameService.startNewGame("", "")}
    }

    /**
     * test if the player change after start the turn
     */
    @Test
    fun startTurnTest() {

        rootService.gameService.startNewGame("Max", "Alex")
        //determine which player play.
        val currentPlayer = rootService.currentGame?.currentPlayer
        rootService.gameService.startTurn()
        //after startTurn the player should be changed.
        assertNotEquals(currentPlayer, rootService.currentGame?.currentPlayer )

        rootService.gameService.startTurn()

        assertEquals(currentPlayer, rootService.currentGame?.currentPlayer )

        //set the game to null and check if the startTurn methode will throw exception.
        rootService.currentGame = null
        assertFailsWith<IllegalStateException> { rootService.gameService.startTurn()}
    }

    /**
     * test if the turn can be ended.
     */
    @Test
    fun endTurnTest() {
        rootService.gameService.startNewGame("Max", "Alex")
        rootService.gameService.endTurn()

        assertTrue(rootService.currentGame?.playerAction == false, "player action should be set to false")

        rootService.currentGame?.playerAction = true
        rootService.gameService.endTurn()

        //the case where handCards and drawCards are empty, so the game will end.
        var currentPlayer = rootService.gameService.currentPlayer()
        currentPlayer?.handCards?.removeAll(currentPlayer.handCards.drop(0))
        currentPlayer?.drawStack?.removeAll(currentPlayer.drawStack.drop(0))

        rootService.currentGame?.playerAction = true
        rootService.gameService.endTurn()

        rootService.currentGame = null
        assertFailsWith<IllegalStateException> { rootService.gameService.endTurn()}
    }


    /**
     *
     */
    @Test
    fun endGameTest() {
        rootService.gameService.startNewGame("Max", "Alex")

        //the game is draw, because both player have the same number of cards.
        assertEquals("The game is a draw!" , rootService.gameService.endGame())

        //var currentPlayer = rootService.gameService.currentPlayer()
        //remove one card from playerOne, so he has fewer card than playerTwo,and he wins.
        rootService.currentGame?.playerOne?.handCards?.drop(4)
            ?.let { rootService.currentGame?.playerOne?.handCards?.removeAll(it) }
        assertEquals("Max" , rootService.gameService.endGame())

        rootService.currentGame?.playerTwo?.handCards?.drop(3)
            ?.let { rootService.currentGame?.playerTwo?.handCards?.removeAll(it) }
        assertEquals("Alex" , rootService.gameService.endGame())


        //remove all handCards and the drawStackCards, so playerOne win.
        rootService.currentGame?.playerOne?.handCards?.drop(0)
            ?.let { rootService.currentGame?.playerOne?.handCards?.removeAll(it) }
        rootService.currentGame?.playerOne?.drawStack?.drop(0)
            ?.let { rootService.currentGame?.playerOne?.drawStack?.removeAll(it) }

        //playerOne should win.
        assertEquals("Max wins" , rootService.gameService.endGame())
    }

    /**
     * the case when player two doesnot have any card in the hand or in the drawStack, so he should win.
     */
    @Test
    fun endGamePlayerTwoWinTest(){
        rootService.gameService.startNewGame("Max", "Alex")


        //remove all handCards and the drawStackCards, so playerTwo win.
        rootService.currentGame?.playerTwo?.handCards?.drop(0)
            ?.let { rootService.currentGame?.playerTwo?.handCards?.removeAll(it) }
        rootService.currentGame?.playerTwo?.drawStack?.drop(0)
            ?.let { rootService.currentGame?.playerTwo?.drawStack?.removeAll(it) }

        //playerTwo should win.
        assertEquals("Alex wins" , rootService.gameService.endGame())
    }
    /**
     * test if the 52 cards will be created,where
     * List[0] hold DrawStack for the first Player
     * List[1] hold DrawStack for the second Player
     * List[2] hold 5 handCards for the firstPlayer
     * List[3] hold 5 handCards for the secondPlayer
     */
    @Test
    fun creatCardsTest() {

        //allCard is list of lists
        val allCard: MutableList<ArrayDeque<Card>> = rootService.gameService.creatCards()

        //allcard should have 4 element of type ArrayDeque
        assertEquals(4, allCard.size)
        //all cards should be 52 cards.
        assertEquals(52, allCard[0].size + allCard[1].size + allCard[2].size + allCard[3].size)
        //drawStackOne and two should have 21 cards for each ether.
        assertEquals(21, allCard[0].size)
        assertEquals(21, allCard[1].size)

        //handsCards should be 5
        assertEquals(5, allCard[2].size)
        assertEquals(5, allCard[3].size)
    }
}
