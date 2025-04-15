package entity

import kotlin.test.*

/**
 * Test class for [Player].
 * */

class PlayerTest{

    val drawSatck : ArrayDeque<Card> = ArrayDeque<Card>()
    val handCards : ArrayDeque<Card> = ArrayDeque<Card>()


    val name: String = "PlayerTest"

    /**
     * drawstack and handCards should be filled with cards
     * handCards will have 5 cards.
     * drawSatck will have 6 cards.
     */
    @BeforeTest
    fun initCards(){
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
     * method to test the creation of player.
     * */
    @Test
    fun createPlayerTest(){

        //craete player with cards
        val player = Player(name, handCards , drawSatck)

        assertNotNull(player)

        assertEquals(player.handCards.first().toString() , "Card(cardSuit=♣, cardValue=2)")
        assertEquals(player.handCards.size , 5)

    }
}
