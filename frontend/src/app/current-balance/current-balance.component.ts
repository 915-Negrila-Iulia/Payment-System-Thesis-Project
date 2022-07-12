import { Component, Input, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';

@Component({
  selector: 'app-current-balance',
  templateUrl: './current-balance.component.html',
  styleUrls: ['./current-balance.component.scss']
})
export class CurrentBalanceComponent implements OnInit {

  @Input()
  accountId: number | undefined;
  currentBalance: Balance = new Balance();
  account: Account = new Account();

  constructor(private balanceService: BalanceService, private accountService: AccountService) { }

  ngOnInit(): void {
    this.getCurrentBalance();
    this.getAccount();
  }

  getCurrentBalance(){
    console.log(this.accountId);
    this.balanceService.getCurrentBalanceOfAccount(this.accountId!).subscribe(data => {
      this.currentBalance = data;
      console.log(data);
    },
    err => console.log(err))
  }

  getAccount(){
    this.accountService.getAccountById(this.accountId).subscribe(data => {
      this.account = data;
    },
    err => console.log(err))
  }
}
