package dev.felipe9999.truthtablecalculator

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("truth-table-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 500.0, 600.0)
        stage.title = "Boolean Algebra Truth Table Calculator"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}