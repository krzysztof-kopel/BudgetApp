package pl.kkopel.budgetapp.transaction;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.kkopel.budgetapp.account.Account;
import pl.kkopel.budgetapp.account.AccountOperation;
import pl.kkopel.budgetapp.account.AccountRepository;
import pl.kkopel.budgetapp.category.Category;
import pl.kkopel.budgetapp.category.CategoryRepository;
import pl.kkopel.budgetapp.common.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    List<Transaction> getAllTransactions(LocalDate from, LocalDate to, Category category) {
        List<Transaction> transactions = (List<Transaction>) this.transactionRepository.findAll();

        if (from != null) {
            transactions = transactions.stream()
                    .filter(x -> !x.getCreatedAt().isBefore(from))
                    .toList();
        }

        if (to != null) {
            transactions = transactions.stream()
                    .filter(x -> !x.getCreatedAt().isAfter(to))
                    .toList();
        }

        if (category != null) {
            transactions = transactions.stream()
                    .filter(x -> x.getCategory().equals(category))
                    .toList();
        }

        return transactions;
    }

    Transaction getTransactionById(UUID id) {
        return this.transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    @Transactional
    Transaction createTransaction(Transaction transaction) {
        Account account = this.accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: "+ transaction.getAccount().getId()));
        Category category = this.categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + transaction.getCategory().getId()));

        if (transaction.getType().getId().equals("EXPENSE")) {
            changeAccountBalance(account, transaction.getAmount(), AccountOperation.SUBTRACT);
        } else if (transaction.getType().getId().equals("INCOME")) {
            changeAccountBalance(account, transaction.getAmount(), AccountOperation.ADD);
        }

        transaction.setAccount(account);
        transaction.setCategory(category);

        this.accountRepository.save(account);
        return this.transactionRepository.save(transaction);
    }

    @Transactional
    void deleteTransaction(UUID id) {
        Transaction transaction = this.transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        Account account = this.accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + transaction.getAccount().getId()));

        if (transaction.getType().getId().equals("EXPENSE")) {
            changeAccountBalance(account, transaction.getAmount(), AccountOperation.ADD);
        } else if (transaction.getType().getId().equals("INCOME")) {
            changeAccountBalance(account, transaction.getAmount(), AccountOperation.SUBTRACT);
        }

        this.accountRepository.save(account);
        this.transactionRepository.delete(transaction);
    }

    BigDecimal getTotalIncomeExpense(TransactionType transactionType, Category category) {
        List<Transaction> transactions = this.getAllTransactions(null, null, category);

        return transactions.stream()
                .filter(transaction -> transaction.getType() != null && transaction.getType().getId().equals(transactionType.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void changeAccountBalance(Account account, BigDecimal amount, AccountOperation operation) {
        if (operation.equals(AccountOperation.SUBTRACT)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }

        this.accountRepository.save(account);
    }
}
