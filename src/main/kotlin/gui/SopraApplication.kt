package gui

import tools.aqua.bgw.core.BoardGameApplication

/**
 * example class to show the game
 */
class SopraApplication : BoardGameApplication("SoPra Game") {

   private val helloScene = HelloScene()

   init {
        this.showGameScene(helloScene)
    }

}

