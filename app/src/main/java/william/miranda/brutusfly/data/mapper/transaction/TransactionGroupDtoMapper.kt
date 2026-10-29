package william.miranda.brutusfly.data.mapper.transaction

import william.miranda.brutusfly.core.common.mapper.Mapper
import william.miranda.brutusfly.data.remote.dto.transaction.TransactionGroupDto
import william.miranda.brutusfly.domain.model.transaction.TransactionGroup
import javax.inject.Inject

/** Converts an API transaction group and its nested splits into domain models. */
class TransactionGroupDtoMapper @Inject constructor(
    private val splitMapper: TransactionSplitDtoMapper
) : Mapper<TransactionGroupDto, TransactionGroup> {
    /**
     * Copies the group identity and title, delegating every split conversion to splitMapper.
     * A split mapping failure aborts the whole group rather than returning incomplete financial data.
     */
    override fun map(
        input: TransactionGroupDto
    ): TransactionGroup = TransactionGroup(
        id = input.id,
        groupTitle = input.attributes.groupTitle,
        splits = input.attributes.transactions.map(
            transform = splitMapper::map
        )
    )
}
