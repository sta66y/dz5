import ru.tinkoff.kora.application.graph.KoraApplication
import ru.tinkoff.kora.application.graph.Node
import ru.tinkoff.kora.common.KoraApp

@KoraApp
interface Application

fun main() {
    val draw = ApplicationGraph.graph()
    val graph = KoraApplication.run { draw }
    val root = draw.findNodeByType(ExampleService::class.java)
    @Suppress("UNCHECKED_CAST")
    val service = graph.get(root as Node<ExampleService>)

    println(service.greet())
}
