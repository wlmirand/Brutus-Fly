package william.miranda.brutusfly.core.common.usecase

interface UseCase<in INPUT, out OUTPUT> {
    operator fun invoke(
        input: INPUT
    ): OUTPUT
}
