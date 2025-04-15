package entity

import kotlin.test.*

/**
 * test class for [GameUpAndDown]
 */
class GameUpAndDownTest{

    val playStackOne : ArrayDeque<Card> = ArrayDeque<Card>()
    val playStackTwo : ArrayDeque<Card> = ArrayDeque<Card>()

    //for the players
    val drawSatck : ArrayDeque<Card> = ArrayDeque<Card>()
    val handCards : ArrayDeque<Card> = ArrayDeque<Card>()

    /**
     * this method will initailse the cards
     */
    @BeforeTest
    fun initGameElements(){
        val card2 = Card( CardSuit.CLUBS, CardValue.TWO)
        drawSatck.add(card2)
        handCards.add(card2)

        val card3 = Card( CardSuit.HEARTS, CardValue.THREE)
        drawSatck.add(card3)
        handCards.add(card3)

        val card4 = Card( CardSuit.SPADES, CardValue.FOUR)
        drawSatck.add(card4)
        handCards.add(card4)

        val card5 = Card( CardSuit.DIAMONDS, CardValue.FIVE)
        drawSatck.add(card5)
        handCards.add(card5)

        val card6 = Card( CardSuit.CLUBS, CardValue.SIX)
        drawSatck.add(card6)
        handCards.add(card6)

        val card7 = Card( CardSuit.SPADES, CardValue.SEVEN)
        drawSatck.add(card7)
    }

    /**
     * test if it is possible to create gameUpAndDown
     * player should be created firstly
     */
    @Test
    fun gameUpAndDownTest(){
        //first Player
        val playerOne = Player("PlayerOne", handCards, drawSatck)

        //second Player
        val playerTwo = Player("PlayerTwo", handCards, drawSatck)

        val gameUpAndDown = GameUpAndDown(playerOne,
                                          playerTwo,
                                          playStackOne = playStackOne,
                                          playStackTwo = playStackTwo
        )

        assertNotNull(gameUpAndDown)
        //At the start the playStackOn and playStackTwo have no cards.
        assertEquals(gameUpAndDown.playStackOne.size, 0)
        assertEquals(gameUpAndDown.playStackTwo.size, 0)

        assertEquals(gameUpAndDown.currentPlayer , false)

        //change the currentPlayer and check it.
        gameUpAndDown.currentPlayer = true
        assertEquals(gameUpAndDown.currentPlayer , true)

        //add one card to playStackOne and check it.
        gameUpAndDown.playStackOne.add(Card(CardSuit.CLUBS, CardValue.TWO))
        assertNotNull(gameUpAndDown.playStackOne)
        assertEquals(gameUpAndDown.playStackOne.size, 1)
        assertEquals(gameUpAndDown.playStackOne.get(0).toString(), Card(CardSuit.CLUBS, CardValue.TWO).toString())

        //add one card to playStackTwo and check it.
        gameUpAndDown.playStackTwo.add(Card(CardSuit.SPADES, CardValue.THREE))
        assertNotNull(gameUpAndDown.playStackTwo)
        assertEquals(gameUpAndDown.playStackTwo.size, 1)
        assertEquals(gameUpAndDown.playStackTwo.get(0).toString(), Card(CardSuit.SPADES, CardValue.THREE).toString())

    }
}
