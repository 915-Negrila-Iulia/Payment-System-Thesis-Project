package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.service.IAccountHistoryService;
import com.example.backend.service.IAccountService;
import com.example.backend.service.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    @Autowired
    IAccountService accountService;

    @Autowired
    IAccountHistoryService accountHistoryService;

    @Autowired
    IAuditService auditService;

    @Autowired
    AuthController authController;

    @GetMapping()
    public List<Account> getAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/history")
    public List<AccountHistory> getHistoryOfAccounts() {
        return accountHistoryService.getHistoryOfAccounts();
    }

    @GetMapping("/history/{accountId}")
    public List<AccountHistory> getHistoryOfAccount(@PathVariable Long accountId){
        return accountHistoryService.getHistoryByAccountId(accountId);
    }

    @PostMapping()
    public Account createAccount(@RequestBody Account accountDetails){
        accountDetails.setStatus(StatusEnum.APPROVE);
        accountDetails.setNextStatus(StatusEnum.ACTIVE);
        accountDetails.setAccountStatus(AccountStatusEnum.CLOSED);
        Account account = accountService.saveAccount(accountDetails);
        accountHistoryService.saveAccountHistory(accountDetails);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(account.getId(),ObjectTypeEnum.ACCOUNT,OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return account;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails){
        Account account = accountService.findAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
        accountHistoryService.saveAccountHistory(account);
        account.setIban(accountDetails.getIban());
        account.setCountryCode(accountDetails.getCountryCode());
        account.setBankCode(accountDetails.getBankCode());
        account.setCurrency(accountDetails.getCurrency());
        account.setAccountStatus(accountDetails.getAccountStatus());
        account.setPersonID(accountDetails.getPersonID());
        account.setStatus(StatusEnum.APPROVE);
        account.setNextStatus(StatusEnum.ACTIVE);
        Account updatedAccount = accountService.saveAccount(account);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(account.getId(), ObjectTypeEnum.ACCOUNT,OperationEnum.UPDATE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Account> approveAccount(@PathVariable Long id){
        Account account = accountService.findAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
        accountHistoryService.saveAccountHistory(account);
        account.setStatus(StatusEnum.ACTIVE);
        account.setNextStatus(StatusEnum.ACTIVE);
        Account activeAccount = accountService.saveAccount(account);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(account.getId(),ObjectTypeEnum.ACCOUNT,OperationEnum.APPROVE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(activeAccount);
    }

    @GetMapping("/{id}")
    public Optional<Account> GetAccountById(@PathVariable Long id){
        return accountService.findAccountById(id);
    }

}
