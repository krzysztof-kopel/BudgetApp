package pl.kkopel.budgetapp.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(
		UUID accountId,
		BigDecimal amount,
		String type,
		UUID categoryId,
		String description
) {
}


