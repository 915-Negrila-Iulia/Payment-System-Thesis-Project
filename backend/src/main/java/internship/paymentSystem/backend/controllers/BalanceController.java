package com.example.backend.controllers;

import com.example.backend.models.*;
import com.example.backend.services.interfaces.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class BalanceController {

    @Autowired
    IBalanceService balanceService;

    @GetMapping("/balances")
    List<Balance> getAllBalances(){return this.balanceService.getAllBalances();}

    @GetMapping("/balances/{id}")
    List<Balance> getAllBalancesByAccountId(@PathVariable Long id){
        return this.balanceService.getAllBalancesByAccountId(id);
    }

    @GetMapping("/current-balance/{id}")
    Balance getCurrentBalance(@PathVariable Long id){
        return this.balanceService.getCurrentBalance(id);
    }

    @PostMapping("/balances")
    public Balance createBalance(@RequestBody Balance balanceDetails){
        return balanceService.saveBalance(balanceDetails);
    }
}
