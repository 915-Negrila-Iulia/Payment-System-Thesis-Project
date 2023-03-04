package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.services.interfaces.IBalanceService;
import internship.paymentSystem.backend.models.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class BalanceController {

    @Autowired
    IBalanceService balanceService;

    @GetMapping("/balances")
    List<Balance> getAllBalances(){return this.balanceService.getAllBalances();}

    @GetMapping("/balances/{id}")
    List<Balance> getAllBalancesByAccountId(@PathVariable Long id){
        return this.balanceService.getAllBalancesByAccountId(id);
    }

    @GetMapping("/balances/{startDate}/{endDate}")
    List<Balance> getBalancesInDateRange(@PathVariable String startDate, @PathVariable String endDate){
        return this.balanceService.filterByDates(startDate, endDate);
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
