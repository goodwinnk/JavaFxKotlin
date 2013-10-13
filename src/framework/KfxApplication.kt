package jfx.framework

import javafx.scene.Node
import javafx.event.Event
import javafx.event.ActionEvent
import javafx.scene.control.Control
import javafx.scene.control.ComboBoxBase
import javafx.application.Application
import javafx.stage.Stage

class DeferredInit<T: Any>() {
    private var pv: T? = null

    public val value: T
        get() { return pv ?: throw IllegalStateException("Property should be initialized before get") }

    public fun store(v: T): T {
        if (pv != null) throw IllegalStateException("Already initialized")
        pv = v
        return v
    }
}

fun <T: Node> T.store(storeField: DeferredInit<T>): T {
    storeField.store(this)
    return this
}


trait ActionParamDescriptor<T: Event>
object Actions {
    object ON_ACTION: ActionParamDescriptor<ActionEvent>
}

class ActionBinding<out T: Control, out P: Event, in F>(
        val control: T,
        val descriptor: ActionParamDescriptor<P>,
        val callAfterInit: Boolean,
        val f: (F) -> Unit) {
    fun bind(form: F) {
        if (descriptor == Actions.ON_ACTION) {
            (control as ComboBoxBase<Any>).setOnAction {
                f(form)
            }

            if (callAfterInit) {
                f(form)
            }
        }
    }
}

trait Builder<Form> {
    fun form(): Form
}

public abstract class KotlinApp<Form, FormBuilder: Builder<Form>>: Application() {
    private val deferredActions: MutableList<ActionBinding<Control, Event, Form>> = arrayListOf()

    public fun <V, T: Event> action(comboboxBase: ComboBoxBase<V>, descriptor: ActionParamDescriptor<T>, callAfterInit: Boolean = true, f: (Form) -> Unit) {
        deferredActions.add(ActionBinding(comboboxBase, descriptor, callAfterInit, f))
    }

    override fun start(primaryStage: Stage) {
        val context = createContext()
        configure(primaryStage, context)

        val form = context.form()
        for (binding in deferredActions) {
            binding.bind(form)
        }

        primaryStage.show()
    }

    public abstract fun configure(primaryStage: Stage, form: FormBuilder)
    public abstract fun createContext(): FormBuilder
}