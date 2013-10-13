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
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.scene.control.ComboBoxBase
import javafx.scene.control.Control
import javafx.scene.Node

trait Form {
    val text : Text
    val button : Button
}

class FormContext: Builder<Form> {
    var text = DeferredInit<Text>()
    var button = DeferredInit<Button>()

    override fun form() = object: Form {
        override val text: Text = this@FormContext.text.value
        override val button: Button = this@FormContext.button.value
    }
}

open class ColorPickerSample: KotlinApp<Form, FormContext>() {
    override fun createContext() = FormContext()
    override fun configure(primaryStage: Stage, form: FormContext) {
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
                                        init(ColorPicker(Color.GREEN)) {
                                            action(this, Actions.ON_ACTION) { form ->
                                                val color = getValue()!!
                                                form.text.setFill(color)
                                                form.button.setStyle(createRGBString(color))
                                            }
                                        }
                                    )
                                },
                                init(VBox()) {
                                    setAlignment(Pos.CENTER)
                                    setSpacing(20.0)
                                    getChildren().addAll(
                                        init(Text("Colors")) {
                                            setFont(Font(53.0))
                                        } store form.text,
                                        Button("Colored Control") store form.button
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(javaClass<ColorPickerSample>(), *args)
}