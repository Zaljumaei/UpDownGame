package entity

/**
 * Entity class for the card, which can be CardSuite or CardValue.

 * @param cardSuit represent a enum value of [CardSuit]
 * @param cardValue represent a enum value of [cardValue]
 */
data class Card(val cardSuit: CardSuit, val cardValue: CardValue)
