package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.DTOs.StatisticDto;
import internship.paymentSystem.backend.client.Client;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.*;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.services.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class TransactionController {

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    private ITransactionService transactionService;

    @GetMapping()
    public List<Transaction> getTransactions(){
        LOGGER.logInfo("HTTP Request -- Get Transactions");
        return transactionService.getAllTransactions();
    }

    @GetMapping("/user/{id}")
    public List<Transaction> getTransactionsOfUser(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Transactions");
        return transactionService.getTransactionsOfUser(id);
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Long id) {
        LOGGER.logInfo("HTTP Request -- Get Transaction by Id");
        return transactionService.findTransactionById(id).isPresent() ?
                transactionService.findTransactionById(id).get() : null;
    }

    @GetMapping("/history")
    public List<TransactionHistory> getHistoryOfTransactions() {
        LOGGER.logInfo("HTTP Request -- Get History of Transactions");
        return transactionService.getHistoryOfTransactions();
    }

    @GetMapping("/history/{transactionId}")
    public List<TransactionHistory> getHistoryOfTransaction(@PathVariable Long transactionId){
        LOGGER.logInfo("HTTP Request -- Get History of Given Transaction");
        return transactionService.getHistoryByTransactionId(transactionId);
    }

    @GetMapping("/history/user/{currentUserId}")
    public List<TransactionHistory> getTransactionsHistoryOfUser(@PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Get Accounts History of User");
        return transactionService.getTransactionsHistoryOfUser(currentUserId);
    }

    @GetMapping("/status/{filterStatus}")
    public List<Transaction> getTransactionsByStatus(@PathVariable StatusEnum filterStatus){
        LOGGER.logInfo("HTTP Request -- Get Transactions by Status");
        return transactionService.getTransactionsByStatus(filterStatus);
    }

    @GetMapping("/account/{id}")
    public List<Transaction> getTransactionsByAccountId(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Transactions of Account");
        return transactionService.getTransactionsByAccountId(id);
    }

    @GetMapping("/statistics/{accountId}")
    public List<StatisticDto> getStatisticsOfAccount(@PathVariable Long accountId){
        LOGGER.logInfo("HTTP Request -- Get Statistics of Account");
        return transactionService.getStatisticsOfAccount(accountId);
    }

    @PostMapping("/{currentUserId}")
    public Transaction createTransaction(@RequestBody Transaction transactionDetails, @PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Post Create Transaction");
        return transactionService.createTransaction(transactionDetails,currentUserId);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveTransaction(@RequestBody CurrentUserDto currentUserDto){
        try{
            Transaction activeTransaction = transactionService.approveTransaction(currentUserDto.getObjectId(),
                    currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Approve Transaction");
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Approve Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectTransaction(@RequestBody CurrentUserDto currentUserDto){
        try{
            Transaction rejectedTransaction = transactionService.rejectTransaction(currentUserDto.getObjectId(),
                    currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Reject Transaction");
            return ResponseEntity.ok(rejectedTransaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Reject Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/AC/{id}")
    public ResponseEntity<?> authorizeTransaction(@PathVariable Long id){
        try{
            Transaction activeTransaction = transactionService.authorizeTransaction(id);
            LOGGER.logInfo("HTTP Request -- Put AC Transaction");
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put AC Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/NAC/{id}")
    public ResponseEntity<?> notAuthorizeTransaction(@PathVariable Long id){
        try{
            Transaction activeTransaction = transactionService.notAuthorizeTransaction(id);
            LOGGER.logInfo("HTTP Request -- Put NAC Transaction");
            return ResponseEntity.ok(activeTransaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put NAC Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/deposit")
    public ResponseEntity<?> depositTransaction(@RequestBody BaseObjectDto<Transaction> transactionDto){
        try{
            Transaction transaction = transactionService.depositTransaction(
                    transactionDto.getObject(), transactionDto.getCurrentUserDto().getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Deposit Transaction");
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Deposit Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<?> withdrawalTransaction(@RequestBody BaseObjectDto<Transaction> transactionDto){
        try{
            Transaction transaction = transactionService.withdrawalTransaction(
                    transactionDto.getObject(), transactionDto.getCurrentUserDto().getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Withdrawal Transaction");
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Withdrawal Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transferTransaction(@RequestBody BaseObjectDto<Transaction> transactionDto){
        try{
            Transaction transaction = transactionService.transferTransaction(
                    transactionDto.getObject(), transactionDto.getCurrentUserDto().getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Transfer Transaction");
            return ResponseEntity.ok(transaction);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Transfer Transaction Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
