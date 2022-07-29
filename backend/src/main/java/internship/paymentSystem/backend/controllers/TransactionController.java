package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.models.*;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.services.interfaces.ITransactionHistoryService;
import internship.paymentSystem.backend.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/history")
    public List<TransactionHistory> getHistoryOfTransactions() {
        return transactionService.getHistoryOfTransactions();
    }

    @GetMapping("/history/{transactionId}")
    public List<TransactionHistory> getHistoryOfTransaction(@PathVariable Long transactionId){
        return transactionService.getHistoryByTransactionId(transactionId);
    }

    @GetMapping("/status/{filterStatus}")
    public List<Transaction> getTransactionsByStatus(@PathVariable StatusEnum filterStatus){
        return transactionService.getTransactionsByStatus(filterStatus);
    }

    @GetMapping("/account/{id}")
    public List<Transaction> getTransactionsByAccountId(@PathVariable Long id){
        return transactionService.getTransactionsByAccountId(id);
    }

    @PostMapping("/{currentUserId}")
    public Transaction createTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        return transactionService.createTransaction(transactionDetails,currentUserId);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveTransaction(@RequestBody CurrentUserDto currentUserDto){
        try{
            Transaction activeTransaction = transactionService.approveTransaction(currentUserDto.getObjectId(),
                    currentUserDto.getCurrentUserId());
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectTransaction(@RequestBody CurrentUserDto currentUserDto){
        try{
            Transaction rejectedTransaction = transactionService.rejectTransaction(currentUserDto.getObjectId(),
                    currentUserDto.getCurrentUserId());
            return ResponseEntity.ok(rejectedTransaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/AC/{id}")
    public ResponseEntity<?> authorizeTransaction(@PathVariable Long id){
        try{
            Transaction activeTransaction = transactionService.authorizeTransaction(id);
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/NAC/{id}")
    public ResponseEntity<?> notAuthorizeTransaction(@PathVariable Long id){
        try{
            Transaction activeTransaction = transactionService.notAuthorizeTransaction(id);
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/deposit")
    public ResponseEntity<?> depositTransaction(@RequestBody BaseObjectDto<Transaction> transactionDto){
        try{
            Transaction transaction = transactionService.depositTransaction(
                    transactionDto.getObject(), transactionDto.getCurrentUserDto().getCurrentUserId());
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<?> withdrawalTransaction(@RequestBody BaseObjectDto<Transaction> transactionDto){
        try{
            Transaction transaction = transactionService.withdrawalTransaction(
                    transactionDto.getObject(), transactionDto.getCurrentUserDto().getCurrentUserId());
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transferTransaction(@RequestBody BaseObjectDto<Transaction> transactionDto){
        try{
            Transaction transaction = transactionService.transferTransaction(
                    transactionDto.getObject(), transactionDto.getCurrentUserDto().getCurrentUserId());
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
