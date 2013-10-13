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
import jfx.example.controls.KotlinApp.ActionBinding

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
    trait Form {
        val text : Text
        val button : Button
    }

    class FormContext {
        var text = DeferedInit<Text>()
        var button = DeferedInit<Button>()

        fun form() = object: Form {
            override val text: Text = this@FormContext.text.value
            override val button: Button = this@FormContext.button.value
        }
    }

    trait ActionParamDescriptor<T: Event>
    object ON_ACTION: ActionParamDescriptor<ActionEvent>

    inner class ActionBinding<out T: Control, out P: Event, in F>(
            val control: T,
            val descriptor: ActionParamDescriptor<P>,
            val callAfterInit: Boolean,
            val f: (F) -> Unit) {
        fun bind(form: F) {
            if (descriptor == ON_ACTION) {
                (control as ComboBoxBase<Any>).setOnAction {
                    f(form)
                }

                if (callAfterInit) {
                    f(form)
                }
            }
        }
    }

    val deferredActions: MutableList<ActionBinding<Control, Event, Form>> = arrayListOf()

    fun <V, T: Event> action(comboboxBase: ComboBoxBase<V>, descriptor: ActionParamDescriptor<T>, callAfterInit: Boolean = true, f: (Form) -> Unit) {
        deferredActions.add(ActionBinding(comboboxBase, descriptor, callAfterInit, f))
    }

    override fun start(primaryStage: Stage) {
        val context = FormContext()
        configure(primaryStage, context)

        val form = context.form()
        for (binding in deferredActions) {
            binding.bind(form)
        }

        primaryStage.show()
    }

    open fun configure(primaryStage: Stage, form: FormContext) {
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
                                            action(this, ON_ACTION) { form ->
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
                                        form.text.store(init(Text("Colors")) {
                                            setFont(Font(53.0))
                                        }),
                                        form.button.store(Button("Colored Control"))
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
    Application.launch(javaClass<KotlinApp>(), *args)
}