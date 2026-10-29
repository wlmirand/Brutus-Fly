package william.miranda.brutusfly.core.common.mapper

/** Synchronous conversion contract. Implementations define validation and failure behavior. */
interface Mapper<in INPUT, out OUTPUT> {
    fun map(input: INPUT): OUTPUT
}
