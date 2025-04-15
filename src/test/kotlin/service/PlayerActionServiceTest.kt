package service

import entity.Card
import entity.CardSuit
import entity.CardValue
import entity.GameUpAndDown
import entity.Player

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.math.min
import kotlin.random.Random.Default.nextBoolean

/**
 * Test class for PlayerActionService.
 */
class PlayerActionServiceTest {

    private lateinit var rootService: RootService
    private lateinit var playerActionService: PlayerActionService
    private lateinit var game: GameUpAndDown

    /**
     * prepare the game
     */
    @BeforeTest
    fun setup() {
        // Create two players.
        val playerOne = Player("MAX")
        val playerTwo = Player("ALEX")

        // Create the central play stacks.
        val playStackOne = ArrayDeque<Card>().apply { add(Card(CardSuit.CLUBS, CardValue.FOUR)) }
        val playStackTwo = ArrayDeque<Card>().apply { add(Card(CardSuit.SPADES, CardValue.SEVEN)) }

        // Initialize the game. Here, currentPlayer == false means playerOne is active.
        game = GameUpAndDown(
            playerOne = playerOne,
            playerTwo = playerTwo,
            currentPlayer = nextBoolean(),
            lastSkip = false,
            playerAction = false,
            playStackOne = playStackOne,
            playStackTwo = playStackTwo
        )

        // Setup player's hand and draw stack for testing.
        // For playerOne (the current player) add one valid card (Clubs, THREE) for playStackOne and one invalid card.
        playerOne.handCards.clear()
        playerOne.drawStack.clear()
        playerOne.handCards.add(Card(CardSuit.CLUBS, CardValue.THREE))     // Valid move (3 is one less than 4)
        playerOne.handCards.add(Card(CardSuit.DIAMONDS, CardValue.ACE))      // Likely invalid move
        // Add a couple of cards to the draw stack.
        playerOne.drawStack.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        playerOne.drawStack.add(Card(CardSuit.HEARTS, CardValue.SIX))

        // Ensure playerTwo's stacks are not interfering.
        playerTwo.handCards.clear()
        playerTwo.drawStack.clear()

        rootService = RootService()
        playerActionService = PlayerActionService(rootService)
        rootService.gameService.startNewGame("ALEX", "MAX")
    }

    /**
     * test playCard
     */
    @Test
    fun playCardTest() {
        // Test valid move on playStackOne (targetStack = 1)
        val validCard = Card(CardSuit.CLUBS, CardValue.THREE)

        val currentPlayer = rootService.gameService.currentPlayer()
        currentPlayer?.handCards?.addFirst(validCard)

        rootService.currentGame?.playStackOne?.addFirst(Card(cardSuit = CardSuit.HEARTS, cardValue = CardValue.TWO))
        rootService.playerActionService.playCard(validCard, 1)

        assertEquals(5, currentPlayer?.handCards?.size)

        rootService.currentGame?.playerAction = false
        rootService.currentGame?.playStackTwo?.addFirst(Card(cardSuit = CardSuit.HEARTS, cardValue = CardValue.TWO))
        playerActionService.playCard(validCard, 2)
        assertEquals(5, currentPlayer?.handCards?.size)
    }

    /**
     * playCard test when it is not valid
     */
    @Test
    fun playCardInvalidTest() {
        // Test invalid move on playStackOne (targetStack = 1) using an invalid card.
        val currentPlayer = rootService.gameService.currentPlayer()
        val invalidCard = Card(CardSuit.DIAMONDS, CardValue.ACE)
        // Ensure the invalid card is in hand.
        currentPlayer?.handCards?.add(invalidCard)
        val initialHandSize = currentPlayer?.handCards?.size

        playerActionService.playCard(invalidCard, 1)

        // Since the move is invalid, the card should remain in hand and not be added to the play stack.
        assertNotEquals(invalidCard, game.playStackOne.first(), "Invalid card should not be added to playStackOne")
    }

    /**
     * test if player can drawCard
     */
    @Test
    fun drawCardTest() {
        // Test drawing a card for the current player.
        val currentPlayer = rootService.gameService.currentPlayer()
        val initialHandSize = currentPlayer?.handCards?.size
        val initialDrawSize = currentPlayer?.drawStack?.size

        // Ensure conditions: playerAction is false.
        game.playerAction = false

        playerActionService.drawCard()

        // Verify that hand size increases by 1 and draw stack decreases by 1.
        if (initialHandSize != null) {
            assertEquals(initialHandSize + 1,
                currentPlayer.handCards.size,
                "Hand size should increase by 1 after drawing")
        }
        if (initialDrawSize != null) {
            assertEquals(initialDrawSize - 1,
                currentPlayer.drawStack.size,
                "Draw stack should decrease by 1 after drawing")
        }
        assertFalse(game.lastSkip, "lastSkip should be false after drawing a card")
    }

    /**
     * test if player can swap card
     */
    @Test
    fun swapHandTest() {
        // Test swapping hand for the current player.
        val currentPlayer = rootService.gameService.currentPlayer()
        // Setup: ensure hand size >= 8 and drawStack is not empty.
        currentPlayer?.handCards?.clear()
        currentPlayer?.drawStack?.clear()
        repeat(8) {
            currentPlayer?.handCards?.add(Card(CardSuit.SPADES, CardValue.FOUR))
        }
        repeat(10) {
            currentPlayer?.drawStack?.add(Card(CardSuit.HEARTS, CardValue.FIVE))
        }
        game.playerAction = false

        playerActionService.swapHand()

        // After swapping, the hand should have min(5, available cards) cards.
        val availableCards = currentPlayer?.drawStack?.size // hand was cleared before drawing new cards
        val expectedHandSize = availableCards?.let { min(5, it) } // 5 cards drawn
        assertEquals(expectedHandSize, currentPlayer?.handCards?.size, "Hand should have new drawn cards after swap")
        assertFalse(game.lastSkip, "lastSkip should be false after swapping hand")
    }

    /**
     * test if player can skip his turn.
     */
    @Test
    fun skipTurnTest() {
        // Test skipTurn in two scenarios.
        val currentPlayer = rootService.gameService.currentPlayer()

        // Scenario 1: lastSkip is true and no action was taken -> skipTurn should call endGame.
        game.playerAction = false
        game.lastSkip = true
        // Ensure canSkip returns true. Make sure draw and swap are not possible.
        currentPlayer?.handCards?.clear()
        currentPlayer?.drawStack?.clear()
        // Add one card to avoid a hand size of 0.
        currentPlayer?.handCards?.add(Card(CardSuit.CLUBS, CardValue.TWO))
        playerActionService.skipTurn()

        game.playerAction = false
        game.lastSkip = false
        // Ensure canSkip returns true again.
        currentPlayer?.handCards?.clear()
        currentPlayer?.drawStack?.clear()
        currentPlayer?.handCards?.add(Card(CardSuit.CLUBS, CardValue.TWO))
        playerActionService.skipTurn()
    }

    /**
     * test canSkip if the methode return valid value.
     */
    @Test
    fun canSkipTest() {
        // Test that canSkip returns true when no other action is possible.
        val currentPlayer = rootService.gameService.currentPlayer()!!
        currentPlayer.handCards.clear()
        currentPlayer.drawStack.clear()
        // Add one card to avoid hand size 0.
        currentPlayer.handCards.add(Card(CardSuit.CLUBS, CardValue.TWO))
        val result = playerActionService.canSkip()
        assertTrue(result, "canSkip should return true when no other action is possible")
    }

    /**
     * test if canDraw methode will return valid value.
     */
    @Test
    fun canDrawCardTest() {
        // Test canDrawCard for the current player.
        val currentPlayer = rootService.gameService.currentPlayer()!!
        currentPlayer.handCards.clear()
        currentPlayer.drawStack.clear()
        currentPlayer.handCards.add(Card(CardSuit.CLUBS, CardValue.TWO))
        currentPlayer.drawStack.add(Card(CardSuit.DIAMONDS, CardValue.THREE))
        val result = playerActionService.canDrawCard()
        assertTrue(result, "canDrawCard should return true when conditions are met")

        // Test condition when hand size is 10.
        currentPlayer.handCards.clear()
        repeat(10) {
            currentPlayer.handCards.add(Card(CardSuit.HEARTS, CardValue.FOUR))
        }
        val resultFalse = playerActionService.canDrawCard()
        assertFalse(resultFalse, "canDrawCard should return false when hand size is 10")
    }

    /**
     * test if swapHand methode will return valid methode.
     */
    @Test
    fun canSwapHandTest() {
        // Test canSwapHand for the current player.
        val currentPlayer = rootService.gameService.currentPlayer()!!
        currentPlayer.handCards.clear()
        currentPlayer.drawStack.clear()
        // Condition: hand size >= 8 and drawStack non-empty.
        repeat(8) {
            currentPlayer.handCards.add(Card(CardSuit.DIAMONDS, CardValue.FIVE))
        }
        currentPlayer.drawStack.add(Card(CardSuit.SPADES, CardValue.SIX))
        val result = playerActionService.canSwapHand()
        assertTrue(result, "canSwapHand should return true when conditions are met")

        // Condition: hand size < 8.
        currentPlayer.handCards.clear()
        repeat(7) {
            currentPlayer.handCards.add(Card(CardSuit.DIAMONDS, CardValue.FIVE))
        }
        val resultFalse = playerActionService.canSwapHand()
        assertFalse(resultFalse, "canSwapHand should return false when hand size is less than 8")
    }
}
