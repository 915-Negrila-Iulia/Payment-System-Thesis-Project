package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.services.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class AccountController {

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    IAccountService accountService;

    @GetMapping()
    public List<Account> getAccounts(){
        LOGGER.logInfo("HTTP Request -- Get Accounts");
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Optional<Account> GetAccountById(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Account By Id");
        return accountService.findAccountById(id);
    }

    @GetMapping("/history")
    public List<AccountHistory> getHistoryOfAccounts() {
        LOGGER.logInfo("HTTP Request -- Get History of Accounts");
        return accountService.getHistoryOfAccounts();
    }

    @GetMapping("/history/user/{currentUserId}")
    public List<AccountHistory> getAccountsHistoryOfUser(@PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Get Accounts History of User");
        return accountService.getAccountsHistoryOfUser(currentUserId);
    }

    @GetMapping("/history/{accountId}")
    public List<AccountHistory> getHistoryOfAccount(@PathVariable Long accountId){
        LOGGER.logInfo("HTTP Request -- Get History of given Account");
        return accountService.getHistoryByAccountId(accountId);
    }

    @GetMapping("/status/{filterStatus}")
    public List<Account> getAccountsByStatus(@PathVariable StatusEnum filterStatus){
        LOGGER.logInfo("HTTP Request -- Get Accounts By Status");
        return accountService.getAccountsByStatus(filterStatus);
    }

    @GetMapping("/valid")
    public Set<Account> getValidAccounts(){
        LOGGER.logInfo("HTTP Request -- Get Valid Accounts");
        return accountService.getValidAccounts();
    }

    @GetMapping("/by-iban/{iban}")
    public Account getAccountByIban(@PathVariable String iban){
        LOGGER.logInfo("HTTP Request -- Get Accounts By Iban");
        return accountService.getAccountByIban(iban);
    }

    @GetMapping("/user/{currentUserId}")
    public List<Account> getAccountsOfUser(@PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Get Accounts of User");
        return accountService.getAccountsOfUser(currentUserId);
    }

    @PostMapping("/{currentUserId}")
    public Account createAccount(@RequestBody Account accountDetails, @PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Post Create Account");
        return accountService.createAccount(accountDetails,currentUserId);
    }

    @PutMapping()
    public ResponseEntity<Account> updateAccount(@RequestBody BaseObjectDto<Account> accountDto){
        LOGGER.logInfo("HTTP Request -- Put Update Account");
        Account updatedAccount = accountService.updateAccount(accountDto.getCurrentUserDto().getObjectId(),
                accountDto.getCurrentUserDto().getCurrentUserId(), accountDto.getObject());
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveAccount(@RequestBody CurrentUserDto currentUserDto){
        try{
            Account activeAccount = accountService.approveAccount(currentUserDto.getObjectId(),
                    currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Approve Account");
            return ResponseEntity.ok(activeAccount);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Approve Account Failed: "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectAccount(@RequestBody CurrentUserDto currentUserDto){
        try{
            Account rejectedAccount = accountService.rejectAccount(currentUserDto.getObjectId(),
                    currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Reject Account");
            return ResponseEntity.ok(rejectedAccount);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Reject Account Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<Account> deleteAccount(@RequestBody CurrentUserDto currentUserDto){
        LOGGER.logInfo("HTTP Request -- Put Delete Account");
        Account deletedAccount = accountService.deleteAccount(currentUserDto.getObjectId(),
                currentUserDto.getCurrentUserId());
        return ResponseEntity.ok(deletedAccount);
    }
}
