package william.miranda.brutusfly.core.common.usecase

interface SuspendUseCase<in INPUT, out OUTPUT> {
    suspend operator fun invoke(input: INPUT): OUTPUT
}
