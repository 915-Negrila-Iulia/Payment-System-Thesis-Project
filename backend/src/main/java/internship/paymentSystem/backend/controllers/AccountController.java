package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.models.Account;
import internship.paymentSystem.backend.models.AccountHistory;
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
public class AccountController {

    @Autowired
    IAccountService accountService;

    @GetMapping()
    public List<Account> getAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Optional<Account> GetAccountById(@PathVariable Long id){
        return accountService.findAccountById(id);
    }

    @GetMapping("/history")
    public List<AccountHistory> getHistoryOfAccounts() {
        return accountService.getHistoryOfAccounts();
    }

    @GetMapping("/history/{accountId}")
    public List<AccountHistory> getHistoryOfAccount(@PathVariable Long accountId){
        return accountService.getHistoryByAccountId(accountId);
    }

    @GetMapping("/valid")
    public Set<Account> getValidAccounts(){
        return accountService.getValidAccounts();
    }

    @GetMapping("/by-iban/{iban}")
    public Account getAccountByIban(@PathVariable String iban){
        return accountService.getAccountByIban(iban);
    }

    @PostMapping("/{currentUserId}")
    public Account createAccount(@RequestBody Account accountDetails, @PathVariable Long currentUserId){
        return accountService.createAccount(accountDetails,currentUserId);
    }

    @PutMapping("/{id}/{currentUserId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @PathVariable Long currentUserId, @RequestBody Account accountDetails){
        Account updatedAccount = accountService.updateAccount(id,currentUserId,accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/approve/{id}/{currentUserId}")
    public ResponseEntity<?> approveAccount(@PathVariable Long id, @PathVariable Long currentUserId){
        try{
            Account activeAccount = accountService.approveAccount(id,currentUserId);
            return ResponseEntity.ok(activeAccount);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject/{id}/{currentUserId}")
    public ResponseEntity<?> rejectAccount(@PathVariable Long id, @PathVariable Long currentUserId){
        try{
            Account rejectedAccount = accountService.rejectAccount(id,currentUserId);
            return ResponseEntity.ok(rejectedAccount);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete/{id}/{currentUserId}")
    public ResponseEntity<Account> deleteAccount(@PathVariable Long id, @PathVariable Long currentUserId){
        Account deletedAccount = accountService.deleteAccount(id,currentUserId);
        return ResponseEntity.ok(deletedAccount);
    }
}
