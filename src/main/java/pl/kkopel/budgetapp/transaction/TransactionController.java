package pl.kkopel.budgetapp.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kkopel.budgetapp.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Endpoints for managing transactions.")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Provides transaction with given ID.")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID id) {
        Transaction transaction = this.transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    @Operation(summary = "Get list of transacation", description = "Provides list of transactions, with optional filtering by completion date and category")
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID categoryId
    ) {
        Category category = null;

        if (categoryId != null) {
            category = new Category();
            category.setId(categoryId);
        }

        List<Transaction> transaction = this.transactionService.getAllTransactions(from, to, category);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    @Operation(summary = "Create transaction", description = "Creates transaction with given parameters.")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction newTransaction = this.transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTransaction);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Deletes transaction with given ID")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        this.transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/total-income")
    @Operation(summary = "Get total income", description = "Provides total income from all accounts")
    public ResponseEntity<BigDecimal> getTotalIncome() {
        TransactionType transactionType = new TransactionType();
        transactionType.setId("INCOME");

        BigDecimal results = this.transactionService.getTotalIncomeExpense(transactionType, null);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/summary/total-expense")
    @Operation(summary = "Get total expense", description = "Provides total expense from all accounts")
    public ResponseEntity<BigDecimal> getTotalExpense() {
        TransactionType transactionType = new TransactionType();
        transactionType.setId("EXPENSE");

        BigDecimal results = this.transactionService.getTotalIncomeExpense(transactionType, null);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/summary/category/{categoryId}")
    @Operation(summary = "Get total expense for category", description = "Provides total expense from all acounts for the given category")
    public ResponseEntity<BigDecimal> getCategoryExpense(@PathVariable UUID categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        TransactionType transactionType = new TransactionType();
        transactionType.setId("EXPENSE");


        BigDecimal results = this.transactionService.getTotalIncomeExpense(transactionType, category);
        return ResponseEntity.ok(results);
    }
}
