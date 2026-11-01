import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.common.annotation.Root

@Root
@Component
class ExampleService(private val repository: ExampleRepository) {
    fun greet(): String = repository.greet()
}