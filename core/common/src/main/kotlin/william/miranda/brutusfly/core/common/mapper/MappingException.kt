package william.miranda.brutusfly.core.common.mapper

/** An input could not be converted without losing or misrepresenting its data. */
class MappingException(
    message: String,
    cause: Throwable? = null
) : IllegalArgumentException(message, cause)
