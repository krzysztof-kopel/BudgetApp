package pl.kkopel.budgetapp.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionDTO(UUID id, UUID accountId, BigDecimal amount, String typeId, UUID categoryId, String description, LocalDate createdAt){
    public TransactionDTO(Transaction transaction) {
        this(transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getAmount(),
                transaction.getType().getId(),
                transaction.getCategory().getId(),
                transaction.getDescription(),
                transaction.getCreatedAt());
    }
}
