package pl.kkopel.budgetapp.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="\"Categories\"")
public class Category {
    @Id
    @Column(name="id")
    private UUID id;

    @Column(name="name", nullable=false, unique=true)
    private String name;

    @Column(name="budget_limit")
    private BigDecimal budgetLimit;

    public Category() {
        this.id = UUID.randomUUID();
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

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", budget_limit=" + budgetLimit +
                '}';
    }
}
