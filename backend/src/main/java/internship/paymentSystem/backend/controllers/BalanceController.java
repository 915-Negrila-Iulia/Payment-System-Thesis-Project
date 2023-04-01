package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.services.interfaces.IAccountService;
import internship.paymentSystem.backend.services.interfaces.IBalanceService;
import internship.paymentSystem.backend.models.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class BalanceController {

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    IBalanceService balanceService;

    @Autowired
    IAccountService accountService;

    @GetMapping("/balances")
    List<Balance> getAllBalances(){
        LOGGER.logInfo("HTTP Request -- Get Balances");
        return this.balanceService.getAllBalances();
    }

    @GetMapping("/balances/{id}")
    List<Balance> getAllBalancesByAccountId(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Balances of Account");
        return this.balanceService.getAllBalancesByAccountId(id);
    }

    @GetMapping("/balances/user/{id}")
    List<Balance> getAllBalancesByUserId(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Balances of Users Accounts");
        return this.accountService.getBalancesOfUser(id);
    }

    @GetMapping("/balances/filter/{startDate}/{endDate}")
    List<Balance> getBalancesInDateRange(@PathVariable String startDate, @PathVariable String endDate){
        LOGGER.logInfo("HTTP Request -- Get Balances in Date Range");
        List<Balance> balances = this.balanceService.getAllBalances();
        return this.balanceService.filterByDates(startDate, endDate, balances);
    }

    @GetMapping("/balances/user/filter/{id}/{startDate}/{endDate}")
    List<Balance> getBalancesOfUserInDateRange(@PathVariable Long id, @PathVariable String startDate, @PathVariable String endDate){
        LOGGER.logInfo("HTTP Request -- Get Balances in Date Range");
        List<Balance> balances = this.accountService.getBalancesOfUser(id);
        return this.balanceService.filterByDates(startDate, endDate, balances);
    }

    @GetMapping("/current-balance/{id}")
    Balance getCurrentBalance(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Current Balance");
        return this.balanceService.getCurrentBalance(id);
    }

    @PostMapping("/balances")
    public Balance createBalance(@RequestBody Balance balanceDetails){
        LOGGER.logInfo("HTTP Request -- Post Create Balance");
        return balanceService.saveBalance(balanceDetails);
    }
}
