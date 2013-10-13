package jfx.example

import javafx.stage.Stage
import javafx.scene.paint.Color
import javafx.application.Application
import jfx.framework.*

open class KotlinApp: Application() {
    override fun start(primaryStage: Stage) {
        configure(primaryStage)
        primaryStage.show()
    }

    open fun configure(primaryStage: Stage) {
        init(primaryStage) {
            title = "Hello JavaFx"
            width = 300.0
            height = 300.0
            scene = scene {
                group {
                    rectangle(100.0, 100.0, 100.0, 100.0) {
                        fillProperty.bind(hoverProperty) { if (isHover()) Color.BLUE else Color.ORANGE }
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(javaClass<KotlinApp>(), *args)
}