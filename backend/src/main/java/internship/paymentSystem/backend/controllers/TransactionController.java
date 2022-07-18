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

    @GetMapping()
    public List<Transaction> getTransactions(){
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {return transactionService.findTransactionById(id).get();}

    @PostMapping("/{currentUserId}")
    public Transaction createTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        return transactionService.createTransaction(transactionDetails,currentUserId);
    }

    @PutMapping("/approve/{id}/{currentUserId}")
    public ResponseEntity<Transaction> approveTransaction(@PathVariable Long id, @PathVariable Long currentUserId){
        Transaction activeTransaction = transactionService.approveTransaction(id,currentUserId);
        return ResponseEntity.ok(activeTransaction);
    }

    @PutMapping("/reject/{id}/{currentUserId}")
    public ResponseEntity<Transaction> rejectTransaction(@PathVariable Long id, @PathVariable Long currentUserId){
        Transaction rejectedTransaction = transactionService.rejectTransaction(id,currentUserId);
        return ResponseEntity.ok(rejectedTransaction);
    }

    @PutMapping("/deposit/{currentUserId}")
    public ResponseEntity<Transaction> depositTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        Transaction transaction = transactionService.depositTransaction(transactionDetails, currentUserId);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/withdrawal/{currentUserId}")
    public ResponseEntity<Transaction> withdrawalTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        Transaction transaction = transactionService.withdrawalTransaction(transactionDetails,currentUserId);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/transfer/{currentUserId}")
    public ResponseEntity<Transaction> transferTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        Transaction transaction = transactionService.transferTransaction(transactionDetails,currentUserId);
        return ResponseEntity.ok(transaction);
    }

}
