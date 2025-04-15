package gui

import entity.Card
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * central class which controll the other classes, from here it will go to the menuScene and endGameScene
 */
class UpAndDownApplication : BoardGameApplication("UpAndDownGame"), Refreshable{

    // to access the layer service and this hold the currently running game.
    val rootService: RootService = RootService()

    val upAndDownGameScene =  UpAndDownGameScene(rootService)
    //val newGameMenuScene =  NewGameMenuScene(rootService)
    val endGameMenuScene =  EndGameMenuScene(rootService)

    //when the exit button is clicked the whole program will close.
    val exitEndgame = endGameMenuScene.exitButton.apply {
        onMouseClicked = {this@UpAndDownApplication.exit()}
    }

    // This menu scene is shown after application start and if the "play again" button
    // is clicked in the gameFinishedMenuScene
    private val newGameMenuScene = NewGameMenuScene(rootService).apply {
        endGameButton.onMouseClicked = {
            exit()
        }
    }


    //this will be initialized when the class UpAndDown initialized.
    init {
        rootService.addRefreshables(
            this,
            upAndDownGameScene,
            newGameMenuScene,
            endGameMenuScene
        )

        //newGemaScene will be initialized, so player can give their names and start the game.
        this.showMenuScene(newGameMenuScene)

    }

    /**
     *the menuScene(first scene) will be hidden, so the player can start to play
     */
    override fun refreshAfterStartNewGame() {
        this.hideMenuScene()
        this.showGameScene(upAndDownGameScene)

    }

    /**
     * refresh the startTurn in gameScene
     */
    override fun refreshAfterStartTurn() {
        this.upAndDownGameScene.refreshAfterStartTurn()
    }

    /**
     * refresh after endTurn in gameScene
     */
    override fun refreshAfterEndTurn() {
        this.upAndDownGameScene.refreshAfterEndTurn()
    }

    /**
     * refresh after drawCard in gameScene
     */
    override fun refreshAfterDraw(card: Card) {
        this.upAndDownGameScene.refreshAfterDraw(card)
    }

    /**
     * refresh after EndGame, so the EndGameScene will be shown
     */
    override fun refreshAfterEndGame(gameWinner: String) {
        this.showMenuScene(endGameMenuScene)
    }

    /**
     * refresh after swapHand in gameScene
     */
    override fun refreshAfterSwapHand() {
        this.upAndDownGameScene.refreshAfterSwapHand()
    }

    /**
     * refresh after playCard in gameScene
     */
    override fun refreshAfterPlayCard(card: Card, stackNumber: Int) {
        upAndDownGameScene.refreshALl()
    }
}
