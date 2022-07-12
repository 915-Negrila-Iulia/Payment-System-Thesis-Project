import { Component, Input, OnInit } from '@angular/core';
import { AccountHistory } from '../account-history';
import { AccountService } from '../account.service';

@Component({
  selector: 'app-accounts-history',
  templateUrl: './accounts-history.component.html',
  styleUrls: ['./accounts-history.component.scss']
})
export class AccountsHistoryComponent implements OnInit {

  accountsHistory: AccountHistory[] = [];

  constructor(private accountService: AccountService) { }

  ngOnInit(): void {
    this.getAccountsHistory();
  }

  getAccountsHistory(){
    this.accountService.getHistoryOfAccounts().subscribe(data => {
      this.accountsHistory = data;
    })
  }

}
