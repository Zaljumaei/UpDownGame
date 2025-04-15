package gui

import entity.Card
import service.RootService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.util.Font

/**
 * class for the new game scene, where the names can be given, and then start the game.
 * @param rootService to access the service layer classes.
 */
class NewGameMenuScene(val rootService: RootService) : MenuScene(1000, 1000, ColorVisual.GREEN), Refreshable {

    private val headlineLabel = Label(
        posX = 50, posY = 50,
        width = 300, height = 50,
        text = "Start New UpAndDownGame.",
        font = Font(size = 22),
        alignment = Alignment.TOP_CENTER
    )

    private val labelFirstPlayer = Label(
        posX = 300, posY = 300,
        width = 150, height = 150,
        text = "First Player Name!",
        font = Font(16)
    )


    //the name of the second player
    private val inputSecondtName : TextField = TextField(
        posX = 500, posY = 400,
        width = 300, height = 100,
        text = "",
        font = Font(16)
    )


    //the name of the first player
    private val inputFirstName : TextField = TextField(
        posX = 500, posY = 300,
        width = 300, height = 100,
        text = "",
        font = Font(16)
    )

    //button to start the game with the given Names.
    private val startButton = Button(
        posX = 600, posY = 500,
        width = 170, height = 100,
        text = "Start the Game.",
        visual = ColorVisual.GREEN
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            rootService.gameService.startNewGame(
                playerOne = inputFirstName.text,
                playerTwo = inputSecondtName.text
            )
        }
    }

    private val labelSecondPlayer = Label(
        posX = 300, posY = 400,
        width = 100, height = 35,
        text = "Second Players Name",
        font = Font(16)
    )

    //End Game button.
    val endGameButton = Button(
        posX = 600, posY = 700,
        width = 140, height = 35,
        text = "Quit"
    ).apply {
        visual = ColorVisual(221, 136, 136)
        onMouseClicked = {
            rootService.gameService.endGame()
        }
    }

    //initialize all components
    init {
        opacity = 0.05
        addComponents(
            headlineLabel,
            labelFirstPlayer, inputFirstName,
            labelSecondPlayer, inputSecondtName,
            startButton, endGameButton
        )

    }
}
