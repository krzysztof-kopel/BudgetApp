package pl.kkopel.budgetapp.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.kkopel.budgetapp.account.Account;
import pl.kkopel.budgetapp.account.AccountRepository;
import pl.kkopel.budgetapp.category.Category;
import pl.kkopel.budgetapp.category.CategoryRepository;
import pl.kkopel.budgetapp.common.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;
    private Category testCategory;
    private TransactionType testTransactionType;
    private Transaction testTransaction;
    private UUID accountId;
    private UUID categoryId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        // Setup test account
        testAccount = new Account();
        testAccount.setId(accountId);
        testAccount.setName("Test Account");
        testAccount.setBalance(BigDecimal.valueOf(5000));

        // Setup test category
        testCategory = new Category();
        testCategory.setId(categoryId);
        testCategory.setName("Groceries");
        testCategory.setBudgetLimit(BigDecimal.valueOf(2000));

        // Setup test transaction type
        testTransactionType = new TransactionType();
        testTransactionType.setId("EXPENSE");

        // Setup test transaction
        testTransaction = new Transaction();
        testTransaction.setId(transactionId);
        testTransaction.setAccount(testAccount);
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setType(testTransactionType);
        testTransaction.setCategory(testCategory);
        testTransaction.setDescription("Grocery shopping");
        testTransaction.setCreatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("Should get transaction by ID successfully")
    void testGetTransactionByIdSuccess() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        // Act
        Transaction result = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(transactionId, result.getId());
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        assertEquals("Grocery shopping", result.getDescription());
        verify(transactionRepository).findById(transactionId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when transaction not found")
    void testGetTransactionByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(transactionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(nonExistentId));
        verify(transactionRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get all transactions successfully")
    void testGetAllTransactionsSuccess() {
        // Arrange
        Transaction transaction2 = new Transaction();
        transaction2.setId(UUID.randomUUID());
        transaction2.setAmount(BigDecimal.valueOf(50));
        transaction2.setType(testTransactionType);

        List<Transaction> transactions = Arrays.asList(testTransaction, transaction2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions(null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }

    @Test
    @DisplayName("Should filter transactions by date range")
    void testGetAllTransactionsWithDateFilter() {
        // Arrange
        Transaction transaction1 = new Transaction();
        transaction1.setId(UUID.randomUUID());
        transaction1.setAmount(BigDecimal.valueOf(100));
        transaction1.setCreatedAt(LocalDate.of(2024, 6, 5));

        Transaction transaction2 = new Transaction();
        transaction2.setId(UUID.randomUUID());
        transaction2.setAmount(BigDecimal.valueOf(50));
        transaction2.setCreatedAt(LocalDate.of(2024, 6, 10));

        Transaction transaction3 = new Transaction();
        transaction3.setId(UUID.randomUUID());
        transaction3.setAmount(BigDecimal.valueOf(75));
        transaction3.setCreatedAt(LocalDate.of(2024, 6, 15));

        List<Transaction> allTransactions = Arrays.asList(transaction1, transaction2, transaction3);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        // Act
        LocalDate from = LocalDate.of(2024, 6, 7);
        LocalDate to = LocalDate.of(2024, 6, 12);
        List<Transaction> result = transactionService.getAllTransactions(from, to, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(50), result.get(0).getAmount());
    }

    @Test
    @DisplayName("Should filter transactions by category")
    void testGetAllTransactionsByCategory() {
        // Arrange
        Category category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("Transport");

        Transaction transaction1 = new Transaction();
        transaction1.setId(UUID.randomUUID());
        transaction1.setAmount(BigDecimal.valueOf(100));
        transaction1.setCategory(testCategory);

        Transaction transaction2 = new Transaction();
        transaction2.setId(UUID.randomUUID());
        transaction2.setAmount(BigDecimal.valueOf(50));
        transaction2.setCategory(category2);

        List<Transaction> allTransactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions(null, null, testCategory);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory.getId(), result.get(0).getCategory().getId());
    }

    @Test
    @DisplayName("Should create EXPENSE transaction and reduce account balance")
    void testCreateExpenseTransactionSuccess() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(testAccount);
        newTransaction.setAmount(BigDecimal.valueOf(100));
        newTransaction.setType(testTransactionType);
        newTransaction.setCategory(testCategory);
        newTransaction.setDescription("Grocery shopping");

        Account updatedAccount = new Account();
        updatedAccount.setId(accountId);
        updatedAccount.setName("Test Account");
        updatedAccount.setBalance(testAccount.getBalance().subtract(BigDecimal.valueOf(100)));

        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(newTransaction);

        // Act
        Transaction result = transactionService.createTransaction(newTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository, atLeast(2)).save(any(Account.class)); // changeAccountBalance and final save
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create INCOME transaction and increase account balance")
    void testCreateIncomeTransactionSuccess() {
        // Arrange
        TransactionType incomeType = new TransactionType();
        incomeType.setId("INCOME");

        Account testAccountForIncome = new Account();
        testAccountForIncome.setId(accountId);
        testAccountForIncome.setName("Test Account");
        testAccountForIncome.setBalance(BigDecimal.valueOf(1000));

        Transaction incomeTransaction = new Transaction();
        incomeTransaction.setAccount(testAccountForIncome);
        incomeTransaction.setAmount(BigDecimal.valueOf(500));
        incomeTransaction.setType(incomeType);
        incomeTransaction.setCategory(testCategory);
        incomeTransaction.setDescription("Salary");

        Account updatedAccount = new Account();
        updatedAccount.setId(accountId);
        updatedAccount.setBalance(testAccountForIncome.getBalance().add(BigDecimal.valueOf(500)));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccountForIncome));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(incomeTransaction);

        // Act
        Transaction result = transactionService.createTransaction(incomeTransaction);

        // Assert
        assertNotNull(result);
        verify(accountRepository, atLeast(2)).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when account not found on transaction creation")
    void testCreateTransactionAccountNotFound() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(testTransaction));
        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("Should throw exception when category not found on transaction creation")
    void testCreateTransactionCategoryNotFound() {
        // Arrange
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(testTransaction));
        verify(accountRepository).findById(accountId);
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("Should delete EXPENSE transaction and restore account balance")
    void testDeleteExpenseTransactionSuccess() {
        // Arrange
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        doNothing().when(transactionRepository).delete(testTransaction);

        // Act
        assertDoesNotThrow(() -> transactionService.deleteTransaction(transactionId));

        // Assert
        verify(transactionRepository).findById(transactionId);
        verify(accountRepository).findById(accountId);
        verify(accountRepository, atLeast(1)).save(any(Account.class));
        verify(transactionRepository).delete(testTransaction);
    }

    @Test
    @DisplayName("Should throw exception when transaction not found on delete")
    void testDeleteTransactionNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(transactionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(nonExistentId));
        verify(transactionRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should calculate total income correctly")
    void testGetTotalIncome() {
        // Arrange
        TransactionType incomeType = new TransactionType();
        incomeType.setId("INCOME");

        Transaction income1 = new Transaction();
        income1.setId(UUID.randomUUID());
        income1.setAmount(BigDecimal.valueOf(1000));
        income1.setType(incomeType);
        income1.setCreatedAt(LocalDate.now());

        Transaction income2 = new Transaction();
        income2.setId(UUID.randomUUID());
        income2.setAmount(BigDecimal.valueOf(500));
        income2.setType(incomeType);
        income2.setCreatedAt(LocalDate.now());

        Transaction expense = new Transaction();
        expense.setId(UUID.randomUUID());
        expense.setAmount(BigDecimal.valueOf(200));
        expense.setType(testTransactionType);
        expense.setCreatedAt(LocalDate.now());

        List<Transaction> allTransactions = Arrays.asList(income1, income2, expense);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        // Act
        BigDecimal result = transactionService.getTotalIncomeExpense(incomeType, null);

        // Assert
        assertEquals(BigDecimal.valueOf(1500), result);
    }

    @Test
    @DisplayName("Should calculate total expense correctly")
    void testGetTotalExpense() {
        // Arrange
        TransactionType expenseType = new TransactionType();
        expenseType.setId("EXPENSE");

        Transaction expense1 = new Transaction();
        expense1.setId(UUID.randomUUID());
        expense1.setAmount(BigDecimal.valueOf(200));
        expense1.setType(expenseType);
        expense1.setCreatedAt(LocalDate.now());

        Transaction expense2 = new Transaction();
        expense2.setId(UUID.randomUUID());
        expense2.setAmount(BigDecimal.valueOf(150));
        expense2.setType(expenseType);
        expense2.setCreatedAt(LocalDate.now());

        List<Transaction> allTransactions = Arrays.asList(expense1, expense2);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        // Act
        BigDecimal result = transactionService.getTotalIncomeExpense(expenseType, null);

        // Assert
        assertEquals(BigDecimal.valueOf(350), result);
    }

    @Test
    @DisplayName("Should calculate total expense by category correctly")
    void testGetTotalExpenseByCategory() {
        // Arrange
        Category otherCategory = new Category();
        otherCategory.setId(UUID.randomUUID());
        otherCategory.setName("Transport");

        TransactionType expenseType = new TransactionType();
        expenseType.setId("EXPENSE");

        Transaction groceryExpense = new Transaction();
        groceryExpense.setId(UUID.randomUUID());
        groceryExpense.setAmount(BigDecimal.valueOf(100));
        groceryExpense.setType(expenseType);
        groceryExpense.setCategory(testCategory);
        groceryExpense.setCreatedAt(LocalDate.now());

        Transaction transportExpense = new Transaction();
        transportExpense.setId(UUID.randomUUID());
        transportExpense.setAmount(BigDecimal.valueOf(50));
        transportExpense.setType(expenseType);
        transportExpense.setCategory(otherCategory);
        transportExpense.setCreatedAt(LocalDate.now());

        List<Transaction> allTransactions = Arrays.asList(groceryExpense, transportExpense);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        // Act
        BigDecimal result = transactionService.getTotalIncomeExpense(expenseType, testCategory);

        // Assert
        assertEquals(BigDecimal.valueOf(100), result);
    }
}

