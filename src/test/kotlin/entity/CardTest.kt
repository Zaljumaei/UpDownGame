package entity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Test class for [Card]
 */
class CardTest{

    /**
     * test if it possible to create [Card] from [CardSuit] and [CardValue].
     */
    @Test
    fun testCard(){

        val card2 = Card( CardSuit.CLUBS, CardValue.TWO)
        val card3 = Card( CardSuit.HEARTS, CardValue.THREE)

        assertNotNull(card2)
        assertEquals(CardSuit.CLUBS, card2.cardSuit)
        assertNotNull(card3.toString() , "CardSuit: ♥, CardValue:3")

    }
}
