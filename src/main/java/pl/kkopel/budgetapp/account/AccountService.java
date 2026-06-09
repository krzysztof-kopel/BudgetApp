package pl.kkopel.budgetapp.account;

import org.springframework.stereotype.Service;
import pl.kkopel.budgetapp.common.exception.AccountHasTransactionsException;
import pl.kkopel.budgetapp.common.exception.ResourceNotFoundException;
import pl.kkopel.budgetapp.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    List<Account> getAllAccounts() {
        return (List<Account>) this.accountRepository.findAll();
    }

    Account getAccountById(UUID id) {
        return this.accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    Account createAccount(Account account) {
        account.setBalance(BigDecimal.ZERO);
        account.setId(null);
        return this.accountRepository.save(account);
    }

    void deleteAccount(UUID id) {
        Account account = this.accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        if (this.transactionRepository.existsByAccountId(id)) {
            throw new AccountHasTransactionsException("Transactions exist for account with id: " + id);
        }
        this.accountRepository.deleteById(id);
    }
}
