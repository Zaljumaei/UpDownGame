package gui

import entity.Card
import service.RootService

import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.util.Font

/**
 * class that represent the gameScene, where the player can play.
 */
class UpAndDownGameScene(val rootService: RootService) : BoardGameScene(1920, 1080, ColorVisual.BLUE), Refreshable {

    private val disPlayCurrentPlayerName = Label(
        posX = 300, posY = 500,
        width = 250, height = 100,
        font = Font(40)
    ).apply {
        onMouseClicked={
            text = rootService.gameService.currentPlayer()?.name.toString()
        }
    }
    /*****************************Stacks that holds the playersCard*****************************/

    //handsCards of currentPlayer
    private val currentPlayerHandCards: LinearLayout<CardView> = LinearLayout(
        posX = 560,
        posY = 750,
        height = 220,
        width = 800,
        spacing = -50,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 255, 255, 50)
    )

    //first central playStack, that cards put on it
    private val playStackOne = LabeledStackView(800, 340, "Play Stack One", false)
        .apply {
        dropAcceptor = { event -> event.draggedComponent in currentPlayerHandCards}
        onDragDropped = {
            checkNotNull(rootService.currentGame)
            val player = rootService.gameService.currentPlayer()
            player?.handCards?.get(currentPlayerHandCards.indexOf(it.draggedComponent))
                ?.let { card -> rootService.playerActionService.playCard(card, 1) }
        }
    }
    //second playStackTwo
    private val playStackTwo = LabeledStackView(1000, 340, "Play Stack Two", false)
        .apply {
        dropAcceptor = { event -> event.draggedComponent in currentPlayerHandCards}
        onDragDropped = {
            checkNotNull(rootService.currentGame)
            val player = rootService.gameService.currentPlayer()
            player?.handCards?.get(currentPlayerHandCards.indexOf(it.draggedComponent))
                ?.let { card -> rootService.playerActionService.playCard(card, 2) }
        }
    }

    //the hand cards of otherPlayer will be shown in top of the screen.
    private val otherPlayerHandCards: LinearLayout<CardView> = LinearLayout(
        posX = 560,
        posY = 50,
        height = 220,
        width = 800,
        spacing = -50,
        alignment = Alignment.CENTER,
        visual = ColorVisual(255, 255, 255, 50)
    )

    //drawStack of currentPlayer will be shown down right of the screen above the swap and endTurn button.
    private val drawstackCurrentPlayer : CardStack<CardView> = CardStack(
        posX = 1700,
        posY = 600,
        height = 200,
        width = 300,
        visual = ColorVisual(255, 255, 255, 50)
    )

    //drawStack of other player will be shown in top left.
    private val drawStackOtherplayer : CardStack<CardView> = CardStack<CardView>(
        posX = 100,
        posY = 0,
        height = 200,
        width = 300,
        visual = ColorVisual(255, 255, 255, 50)
    ).apply {
        rotation = 180.0
    }

    /************************Buttons in GameScene************************************/
    private val startTurnButton = Button(
        posX = 1700, posY = 900,
        width = 200, height = 80,
        text = "Start Turn",
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            startTurnGUI()
        }
    }

    //button to end turn of the currentPlayer.
    private val endTurnButton = Button(
        posX = 1700, posY = 1000,
        width = 200, height = 80,
        text = "End Turn",
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            endTurnGUI()
        }
    }

    //button to end turn of the currentPlayer.
    private val skipTurnButton = Button(
        posX = 1100, posY = 1000,
        width = 200, height = 80,
        text = "Skip Turn",
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            skipTurnGUI()
        }
    }

    //Button to swap the handCards with the drawStackCard.
    private val swapHandButton = Button(
        posX = 1500, posY = 1000,
        width = 200, height = 80,
        text = "Swap Hand",
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            swapHandGUI()
        }
    }

    //Button to draw card
    private val drawCardButton = Button(
        posX = 1300, posY = 1000,
        width = 200, height = 80,
        text = "Draw Card",
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            rootService.playerActionService.drawCard()
            drawCardGUI()
        }
    }


    // Initialize all components
    init {
        addComponents(
            startTurnButton, endTurnButton,skipTurnButton,
            drawCardButton,swapHandButton,
            playStackOne, playStackTwo,
            currentPlayerHandCards,otherPlayerHandCards,
            drawstackCurrentPlayer, drawStackOtherplayer,
            disPlayCurrentPlayerName
        )
        //show the cards on plyStackOne and two from the beginning.
        showPlayStack()
    }

    /***********************function in GUI*********************************/

    /**
     * show the playStackOne- and Two and refresh them after any playCard action or startTurn.
     */
    fun showPlayStack(){

        //PlayStackOne, the data will be taken from the entity layer.
        val playeStackOneEntity = rootService.currentGame?.playStackOne
        if (playeStackOneEntity != null) {
            playStackOne.add(
                CardView(
                    front = CardImageLoader().frontImageFor(playeStackOneEntity.first().cardSuit,
                        playeStackOneEntity.first().cardValue),
                ).apply { showFront() }
            )
        }

        val playeStackTwoEntity = rootService.currentGame?.playStackTwo
        if (playeStackTwoEntity != null) {
            playStackTwo.add(
                CardView(
                    front = CardImageLoader().frontImageFor(playeStackTwoEntity.first().cardSuit,
                        playeStackTwoEntity.first().cardValue),
                ).apply { showFront() }
            )
        }
    }

    /**
     * start turn in GUI, so the card of the currentPlayer should be refreshed
     * and only the cards of currentPlayer will be shown.
     */
    fun startTurnGUI(){
        checkNotNull(rootService.currentGame)
        //make the endTurnButton for the nextPlayer visible and startTurn invisible.
        startTurnButton.isVisible = false
        endTurnButton.isVisible = true
        skipTurnButton.isVisible = true
        swapHandButton.isVisible = true
        drawCardButton.isVisible = true

        //start turn in sl
        rootService.gameService.startTurn()
        val currentPlayer = rootService.gameService.currentPlayer()

        showPlayerHand()

        drawstackCurrentPlayer.apply {
            currentPlayer?.drawStack?.map { card ->
                CardView(
                    front = CardImageLoader().frontImageFor(card.cardSuit, card.cardValue),
                    back = CardImageLoader().backImage
                ).apply { showFront() ;  isDraggable = true }
            }?.forEach(::add)
        }

        disPlayCurrentPlayerName.text = currentPlayer?.name.toString()
    }

    /**
     * show the HandCards and the top card of drawStack of currentPlayer.
     */
    private fun showPlayerHand(){
        checkNotNull(rootService.currentGame)
        val player =rootService.gameService.currentPlayer()

        currentPlayerHandCards.clear()
        currentPlayerHandCards.apply {
            player?.handCards?.map { card ->
                CardView(
                    front = CardImageLoader().frontImageFor(card.cardSuit, card.cardValue),
                    back = CardImageLoader().backImage
                ).apply { showFront() ; isDraggable = true}
            }?.forEach(::add)
        }

        //show the drawStack(only the top card or first card)
        drawstackCurrentPlayer.clear()
        drawstackCurrentPlayer.apply {
            player?.drawStack?.map { card ->
                CardView(
                    front = CardImageLoader().frontImageFor(card.cardSuit, card.cardValue),
                    back = CardImageLoader().backImage
                ).apply { showFront() ;  isDraggable = true }
            }?.forEach(::add)
        }

    }

    /**
     * end turn in GUI
     */
    fun endTurnGUI(){
        checkNotNull(rootService.currentGame)
        endTurnButton.isVisible = false
        startTurnButton.isVisible = true

        showBackCard()
        rootService.gameService.endTurn()
        showPlayStack()
    }

    /**
     * between the endTurn and startTurn of next player,
     * the back of cards of currentPlayer will be shown,
     * so when currentPlayer click [endTurnButton] the back of his Card will be shown,
     * so the next player can not see them.
     * and the card will be not draggable.
     */
    fun showBackCard(){
        checkNotNull(rootService.currentGame)
        val player =rootService.gameService.currentPlayer()

        currentPlayerHandCards.clear()
        currentPlayerHandCards.apply {
            player?.handCards?.map { card ->
                CardView(
                    front = CardImageLoader().frontImageFor(card.cardSuit, card.cardValue),
                    back = CardImageLoader().backImage
                ).apply { showBack() ; isDraggable = false }
            }?.forEach(::add)
        }

        drawstackCurrentPlayer.clear()
        drawstackCurrentPlayer.apply {
            player?.drawStack?.map { card ->
                CardView(
                    front = CardImageLoader().frontImageFor(card.cardSuit, card.cardValue),
                    back = CardImageLoader().backImage
                ).apply { showBack() ;  isDraggable = false }
            }?.forEach(::add)
        }
    }

    /**
     * swap hand in GUI
     */
    fun swapHandGUI(){
        checkNotNull(rootService.currentGame)
        //make the swapHandButton inVisible, so player can not see it .
        swapHandButton.isVisible = false
        rootService.playerActionService.swapHand()
        showPlayerHand()
    }

    /**
     * draw Card in GUi
     */
    fun drawCardGUI(){
        checkNotNull(rootService.currentGame)
        drawCardButton.isVisible = false
        //draw card in sl and then show the changes in GUI.

        showPlayerHand()
    }

    /**
     * skipTurn in GUI
     */
    fun skipTurnGUI(){
        checkNotNull(rootService.currentGame)
        skipTurnButton.isVisible = false
        showBackCard()
        rootService.playerActionService.skipTurn()
        startTurnButton.isVisible = true
    }

    /**
     * refresh the view of cards in the playStack
     */
    fun refreshALl(){
        showPlayStack()
    }

    /**
     * after startGame it should the first cards on the playStack be shown
     */
    override fun refreshAfterStartNewGame() {
        showPlayStack()
    }

    /**
     * both handCards and playStack should be updated to display the card,
     * which player move it from handCards to playStack
     */
    override fun refreshAfterPlayCard(card: Card, stackNumber: Int) {
        showPlayerHand()
        showPlayStack()
    }

    /**
     * after swapHand the 5 new cards should be shown in the handsCads.
     */
    override fun refreshAfterSwapHand() {
        showPlayerHand()
    }

    /**
     * after startTurn the playStack and handCards should be displayed.
     */
    override fun refreshAfterStartTurn() {
        showPlayerHand()
        showPlayStack()
    }

    /**
     * refresh afterEndTurn
     */
    override fun refreshAfterEndTurn() {
        showPlayStack()
    }

    /**
     * the card, that player draw it from drawStack to handCard should be display.
     */
    override fun refreshAfterDraw(card: Card) {
        showPlayerHand()
    }
}
