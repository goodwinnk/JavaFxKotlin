package jfx.example.controls

import jfx.framework.*
import javafx.stage.Stage
import javafx.scene.paint.Color
import javafx.application.Application
import javafx.scene.layout.VBox
import javafx.geometry.Pos
import javafx.geometry.Insets
import javafx.scene.control.ToolBar
import javafx.scene.control.ColorPicker
import javafx.scene.text.Text
import javafx.scene.text.Font
import javafx.scene.control.Button

private class DeferedInit<T: Any>() {
    private var pv: T? = null

    public val value: T
        get() { return pv ?: throw IllegalStateException("Property should be initialized before get") }

    public fun store(v: T): T {
        if (pv != null) throw IllegalStateException("Already initialized")
        pv = v
        return v
    }
}

open class KotlinApp: Application() {
    override fun start(primaryStage: Stage) {
        configure(primaryStage)
        primaryStage.show()
    }

    open fun configure(primaryStage: Stage) {
        val colorPicker = DeferedInit<ColorPicker>()
        var text = DeferedInit<Text>()
        var button = DeferedInit<Button>()

        fun createRGBString(color: Color) = "-fx-base: rgb(${color.getRed() * 255}, ${color.getGreen() * 255}, ${color.getBlue() * 255});"

        init(primaryStage) {
            scene = scene {
                group {
                    getChildren().addAll(
                        init(VBox()) {
                            setSpacing(150.0)
                            setAlignment(Pos.CENTER)
                            setPadding(Insets(0.0, 0.0, 120.0, 0.0))

                            getChildren().addAll(
                                init(ToolBar()) {
                                    getChildren().addAll(
                                        colorPicker.store(ColorPicker(Color.GRAY))
                                    )
                                },
                                init(VBox()) {
                                    setAlignment(Pos.CENTER)
                                    setSpacing(20.0)
                                    getChildren().addAll(
                                        init(text.store(Text("Colors"))) {
                                            setFont(Font(53.0))
                                        },
                                        button.store(Button("Colored Control"))
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }

        fun onColorUpdate() {
            val color = colorPicker.value.getValue()!!
            text.value.setFill(color)
            button.value.setStyle(createRGBString(color))
        }


        colorPicker.value.setOnAction { onColorUpdate() }
        onColorUpdate()
    }
}

fun main(args: Array<String>) {
    Application.launch(javaClass<KotlinApp>(), *args)
}