package com.example.backend.controllers;

import com.example.backend.models.*;
import com.example.backend.models.enumerations.ObjectTypeEnum;
import com.example.backend.models.enumerations.OperationEnum;
import com.example.backend.models.enumerations.StatusEnum;
import com.example.backend.services.interfaces.IAuditService;
import com.example.backend.services.interfaces.IBalanceService;
import com.example.backend.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IBalanceService balanceService;

    @Autowired
    private IAuditService auditService;

    @Autowired
    private AuthController authController;

    @GetMapping()
    public List<Transaction> getTransactions(){
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {return transactionService.findTransactionById(id).get();}

    @PostMapping()
    public Transaction createTransaction(@RequestBody Transaction transactionDetails){
        transactionDetails.setStatus(StatusEnum.APPROVE);
        transactionDetails.setNextStatus(StatusEnum.ACTIVE);
        Transaction transaction = transactionService.saveTransaction(transactionDetails);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(transaction.getId(), ObjectTypeEnum.TRANSACTION, OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return transaction;
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Transaction> approveTransaction(@PathVariable Long id){
        Transaction transaction = transactionService.findTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
        transaction.setStatus(transaction.getNextStatus());
        Transaction activeTransaction = transactionService.saveTransaction(transaction);
        balanceService.updateTotalAmount(transaction.getId());
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(transaction.getId(),ObjectTypeEnum.TRANSACTION,OperationEnum.APPROVE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(activeTransaction);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Transaction> rejectTransaction(@PathVariable Long id){
        Transaction transaction = transactionService.findTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found"));
        transaction.setStatus(StatusEnum.DELETE);
        transaction.setNextStatus(StatusEnum.DELETE);
        Transaction activeTransaction = transactionService.saveTransaction(transaction);
        balanceService.cancelAmountChanges(transaction.getId());
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(transaction.getId(),ObjectTypeEnum.TRANSACTION,OperationEnum.REJECT,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(activeTransaction);
    }

    @PutMapping("/deposit")
    public ResponseEntity<Transaction> depositTransaction(@RequestBody Transaction transactionDetails){
        Long accountId = transactionDetails.getAccountID();
        Double amount = transactionDetails.getAmount();
        Transaction transaction = transactionService.depositTransaction(accountId, amount);
        balanceService.updateAvailableAmount(accountId, transaction.getId());
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(transaction.getId(),ObjectTypeEnum.TRANSACTION,OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<Transaction> withdrawalTransaction(@RequestBody Transaction transactionDetails){
        Long accountId = transactionDetails.getAccountID();
        Double amount = transactionDetails.getAmount();
        Transaction transaction = transactionService.withdrawalTransaction(accountId, amount);
        balanceService.updateAvailableAmount(accountId, transaction.getId());
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(transaction.getId(),ObjectTypeEnum.TRANSACTION,OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/transfer")
    public ResponseEntity<Transaction> transferTransaction(@RequestBody Transaction transactionDetails){
        Long accountId = transactionDetails.getAccountID();
        Long targetId = transactionDetails.getTargetAccountID();
        Double amount = transactionDetails.getAmount();
        Transaction transaction = transactionService.transferTransaction(accountId, targetId, amount);
        balanceService.updateAvailableAmount(accountId, transaction.getId());
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(transaction.getId(),ObjectTypeEnum.TRANSACTION,OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(transaction);
    }
}
