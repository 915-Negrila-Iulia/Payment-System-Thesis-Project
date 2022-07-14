import { Component, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';

@Component({
  selector: 'app-balances-list',
  templateUrl: './balances-list.component.html',
  styleUrls: ['./balances-list.component.scss']
})
export class BalancesListComponent implements OnInit {

  balances: Balance[] = [];
  accounts: Account[] = [];

  constructor(private balanceService: BalanceService, private accountService: AccountService) { }

  ngOnInit(): void {
    this.getBalances();
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
    })
  }

  getBalances(){
    this.balanceService.getAllBalances().subscribe(data => {
      this.balances = data;
    },
    err => console.log(err))
  }

  getIbanAccount(id: number | undefined){
    if(this.accounts){
      let account = this.accounts.filter(acc => acc.id === id)[0];
      if(account){
        return account.iban;
      }
      return '';
    }
    else{
      return '';
    }
  }
}
