package pl.kkopel.budgetapp.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="\"Accounts\"")
public class Account {

    @Id
    @Column(name="id")
    private UUID id;

    @Column(name="name", nullable=false, unique=true)
    private String name;

    @Column(name="balance", nullable=false)
    private BigDecimal balance;

    public Account() {
        this.id = UUID.randomUUID();
        this.balance = BigDecimal.ZERO;
    }

    public Account(AccountCreationDTO dto) {
        this.name = dto.name();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
