package jfx.example.controls.buttons

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
import javafx.scene.layout.GridPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.Label
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Toggle

trait Form {
    val label: Label
}

class FormContext: Builder<Form> {
    var label = DeferredInit<Label>()

    override fun form() = object: Form {
        override val label = this@FormContext.label.value
    }
}

open class ToggleButtonSample: KotlinApp<Form, FormContext>() {
    override fun createContext(): FormContext = FormContext()

    override fun configure(primaryStage: Stage, form: FormContext) {
        fun createRGBString(color: Color) = "-fx-base: rgb(${color.getRed() * 255}, ${color.getGreen() * 255}, ${color.getBlue() * 255});"

        init(primaryStage) {
            scene = scene {
                group {
                    getChildren().addAll(
                        init(GridPane()) {
                            setVgap(20.0)
                            setHgap(10.0)

                            val group = init(ToggleGroup()) {
                                changeListner(selectedToggleProperty(), null, true) { form, new ->
                                    if (new != null) {
                                        form.label.setText((new as ToggleButton).getText())
                                    } else {
                                        form.label.setText("...")
                                    }
                                }
                            }

                            getChildren().addAll(
                                init(ToggleButton("Cat")) {
                                    GridPane.setConstraints(this, 0, 0)
                                    setToggleGroup(group)
                                },

                                init(ToggleButton("Dog")) {
                                    GridPane.setConstraints(this, 1, 0)
                                    setToggleGroup(group)
                                },

                                init(ToggleButton("Horse")) {
                                    GridPane.setConstraints(this, 2, 0)
                                    setToggleGroup(group)
                                },

                                init(Label()) {
                                    setStyle("-fx-font-size: 2em;")
                                } store form.label
                            )
                        }
                    )
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(javaClass<ToggleButtonSample>(), *args)
}