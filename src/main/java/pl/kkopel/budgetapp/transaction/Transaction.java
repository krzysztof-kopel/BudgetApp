package pl.kkopel.budgetapp.transaction;


import jakarta.persistence.*;
import pl.kkopel.budgetapp.account.Account;
import pl.kkopel.budgetapp.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="\"Transactions\"")
public class Transaction {
    @Id
    @Column(name="id")
    private UUID id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="account_id", nullable=false)
    private Account account;

    @Column(name="amount", nullable=false)
    private BigDecimal amount;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="type_id", nullable=false)
    private TransactionType type;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="category_id", nullable=false)
    private Category category;

    @Column(name="description")
    private String description;

    @Column(name="created_at", nullable=false)
    private LocalDate createdAt;

    public Transaction() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {this.id = id;}

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
