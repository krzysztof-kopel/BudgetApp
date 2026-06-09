package pl.kkopel.budgetapp.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.kkopel.budgetapp.common.exception.AccountHasTransactionsException;
import pl.kkopel.budgetapp.common.exception.ResourceNotFoundException;
import pl.kkopel.budgetapp.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private UUID testAccountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAccountId = UUID.randomUUID();
        testAccount = new Account();
        testAccount.setId(testAccountId);
        testAccount.setName("Test Account");
        testAccount.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Should get all accounts successfully")
    void testGetAllAccountsSuccess() {
        // Arrange
        Account account1 = new Account();
        account1.setId(UUID.randomUUID());
        account1.setName("Account 1");
        account1.setBalance(BigDecimal.valueOf(500));

        Account account2 = new Account();
        account2.setId(UUID.randomUUID());
        account2.setName("Account 2");
        account2.setBalance(BigDecimal.valueOf(1500));

        List<Account> accounts = Arrays.asList(account1, account2);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Account 1", result.get(0).getName());
        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("Should get empty list when no accounts exist")
    void testGetAllAccountsEmpty() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(List.of());

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("Should get account by ID successfully")
    void testGetAccountByIdSuccess() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));

        // Act
        Account result = accountService.getAccountById(testAccountId);

        // Assert
        assertNotNull(result);
        assertEquals(testAccountId, result.getId());
        assertEquals("Test Account", result.getName());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());
        verify(accountRepository).findById(testAccountId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account not found")
    void testGetAccountByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(nonExistentId));
        verify(accountRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should create account with default balance of ZERO")
    void testCreateAccountSuccess() {
        // Arrange
        Account newAccount = new Account();
        newAccount.setName("New Account");
        newAccount.setBalance(BigDecimal.valueOf(5000)); // This should be ignored

        Account savedAccount = new Account();
        savedAccount.setId(UUID.randomUUID());
        savedAccount.setName("New Account");
        savedAccount.setBalance(BigDecimal.ZERO); // Service should set this

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // Act
        Account result = accountService.createAccount(newAccount);

        // Assert
        assertNotNull(result);
        assertEquals("New Account", result.getName());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should delete account successfully when no transactions exist")
    void testDeleteAccountSuccess() {
        // Arrange
        when(transactionRepository.existsByAccountId(testAccountId)).thenReturn(false);
        doNothing().when(accountRepository).deleteById(testAccountId);

        // Act
        assertDoesNotThrow(() -> accountService.deleteAccount(testAccountId));

        // Assert
        verify(transactionRepository).existsByAccountId(testAccountId);
        verify(accountRepository).deleteById(testAccountId);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete account with transactions")
    void testDeleteAccountWithTransactionsFails() {
        // Arrange
        when(transactionRepository.existsByAccountId(testAccountId)).thenReturn(true);

        // Act & Assert
        assertThrows(AccountHasTransactionsException.class, () -> accountService.deleteAccount(testAccountId));
        verify(transactionRepository).existsByAccountId(testAccountId);
        verify(accountRepository, never()).deleteById(testAccountId);
    }
}

