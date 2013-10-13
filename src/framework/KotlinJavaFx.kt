package jfx.framework

import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.application.Application
import javafx.scene.shape.Rectangle
import javafx.scene.Group
import javafx.beans.value.ChangeListener
import javafx.beans.InvalidationListener
import javafx.beans.property.StringProperty
import javafx.stage.Window
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.scene.shape.Shape
import javafx.scene.paint.Paint
import javafx.beans.property.ObjectProperty
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.Property
import javafx.beans.binding.ObjectBinding
import javafx.beans.Observable
import javafx.scene.Parent
import javafx.scene.control.Control

fun <T> init(obj: T, f: T.() -> Unit) = with(obj) { f(); this }

var Stage.title: String?
    get() = this.getTitle()
    set(value) = this.setTitle(value)

var Stage.scene: Scene?
    get() = this.getScene()
    set(v) = this.setScene(v)

class SceneBuilder {
    var fill: Paint? = null
}

fun Stage.scene(sceneInit: SceneBuilder.() -> Parent): Scene {
    val builder = SceneBuilder()
    val parent = builder.sceneInit()

    val scene = Scene(parent)
    scene.setFill(builder.fill)

    return scene
}

fun group(init: Group.() -> Unit): Group {
    val group = Group()
    group.init()
    return group
}

fun Parent.rectangle(x: Double, y: Double, width: Double, height: Double, init: Rectangle.() -> Unit): Rectangle {
    val rectangle = Rectangle(x, y, width, height)
    rectangle.init()

    when (this) {
        is Group -> this.getChildren().add(rectangle)
        else -> throw UnsupportedOperationException("Only Groups are supported")
    }

    return rectangle
}

val Stage.titleProperty: StringProperty
    get() = this.titleProperty()

var Window.width: Double
    get() = this.getWidth()
    set(value) = this.setWidth(value)

val Window.widthProperty: ReadOnlyDoubleProperty
    get() = this.widthProperty()

var Window.height: Double
    get() = this.getHeight()
    set(value) = this.setHeight(value)

val Window.heightProperty: ReadOnlyDoubleProperty
    get() = this.heightProperty()

val Node.hover: Boolean
    get() = this.isHover()

val Node.hoverProperty: ReadOnlyBooleanProperty
    get() = this.hoverProperty()

var Shape.fill: Paint?
    get() = this.getFill()
    set(value) = this.setFill(value)

val Shape.fillProperty: ObjectProperty<Paint>
    get() = this.fillProperty()

fun <T: Any> ObservableValue<T>.onChange(action: (ObservableValue<out T>, previous: T?, newValue: T?) -> Unit) {
    this.addListener(ChangeListener<T>(action))
}

fun <T: Any> Property<T>.bind(vararg dependencies: Observable, f: () -> T?) {
    class FunObjectBinding(val f: () -> T?) : ObjectBinding<T>() {
        {
            bind(*dependencies)
        }

        override fun computeValue(): T? = f()
    }

    this.bind(FunObjectBinding(f))
}