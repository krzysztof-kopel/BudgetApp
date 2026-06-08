package pl.kkopel.budgetapp.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="\"TransactionTypes\"")
public class TransactionType {
    @Id
    @Column(name = "id")
    private String id;

    public TransactionType() {
    }

    public String getId() {
        return id;
    }
}
