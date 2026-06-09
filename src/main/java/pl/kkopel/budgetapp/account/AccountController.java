package pl.kkopel.budgetapp.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Endpoints for managing personal accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Provides the list of all accounts.")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accountList = this.accountService.getAllAccounts();
        return ResponseEntity.ok(accountList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account with specified id", description = "Provides the account with id passed in the URL")
    public ResponseEntity<Account> getAccountById(@PathVariable UUID id) {
        Account account = this.accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping
    @Operation(summary = "Create account", description = "Creates account with default balance of 0.")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account newAccount = this.accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Deletes account with given id if that account has no transactions.")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        this.accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
