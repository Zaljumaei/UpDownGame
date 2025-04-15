package gui

import entity.Card
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * class for the endGameScene to show the winner.
 */
class EndGameMenuScene(val rootService: RootService) : MenuScene(1000, 1000, ColorVisual.CYAN), Refreshable{

    private val headlineLabel = Label(
        width = 300, height = 50, posX = 50, posY = 50,
        text = "Game Over",
        font = Font(size = 22)
    )

    /**
     * Button to restart the game.
     */
    val startButton = Button(
        width = 140, height = 35,
        posX = 210, posY = 240,
        text = "Play again"
    ).apply {
        visual = ColorVisual.LIGHT_GRAY
        onMouseClicked = {

        }
    }

    /**
     * Text field to display the name of the winning player.
     */
    private val gameWinnerResult = Label(
        width = 200, height = 35,
        posX = 110, posY = 125,
        font = Font(size = 22)
    ).apply {
        onKeyPressed = {
            startButton.isDisabled = this.text.isBlank()
        }
    }

    /**
     * Button to exit the game.
     */
    val exitButton = Button(
        width = 140, height = 35,
        posX = 50, posY = 240,
        text = "Exit"
    ).apply {
        visual = ColorVisual.RED
    }


    init {
        opacity = .5
        addComponents(
            startButton, exitButton,
            gameWinnerResult, headlineLabel
        )
    }

    /**
     * refresh after endGame so the name of winner can be seen in the display.
     */
    override fun refreshAfterEndGame(gameWinner: String) {
        gameWinnerResult.text = gameWinner
        gameWinnerResult.isVisible = true
    }
}
