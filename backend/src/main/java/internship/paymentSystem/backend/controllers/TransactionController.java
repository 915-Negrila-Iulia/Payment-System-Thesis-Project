package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.services.interfaces.ITransactionService;
import internship.paymentSystem.backend.models.Transaction;
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

    @PostMapping("/{currentUserId}")
    public Transaction createTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        return transactionService.createTransaction(transactionDetails,currentUserId);
    }

    @PutMapping("/approve/{id}/{currentUserId}")
    public ResponseEntity<?> approveTransaction(@PathVariable Long id, @PathVariable Long currentUserId){
        try{
            Transaction activeTransaction = transactionService.approveTransaction(id,currentUserId);
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject/{id}/{currentUserId}")
    public ResponseEntity<?> rejectTransaction(@PathVariable Long id, @PathVariable Long currentUserId){
        try{
            Transaction rejectedTransaction = transactionService.rejectTransaction(id,currentUserId);
            return ResponseEntity.ok(rejectedTransaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/deposit/{currentUserId}")
    public ResponseEntity<?> depositTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        try{
            Transaction transaction = transactionService.depositTransaction(transactionDetails, currentUserId);
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/withdrawal/{currentUserId}")
    public ResponseEntity<?> withdrawalTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        try{
            Transaction transaction = transactionService.withdrawalTransaction(transactionDetails,currentUserId);
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transfer/{currentUserId}")
    public ResponseEntity<?> transferTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        try{
            Transaction transaction = transactionService.transferTransaction(transactionDetails,currentUserId);
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
