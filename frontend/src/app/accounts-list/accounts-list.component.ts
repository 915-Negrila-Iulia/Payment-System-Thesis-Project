import { Component, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account.service';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.scss']
})
export class AccountsListComponent implements OnInit {

  accounts: Account[] = [];
  account: Account = new Account();

  constructor(private accountService: AccountService) { }

  ngOnInit(): void {
    this.getAccounts();
  }

  getAccounts(){
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
    })
  }

  updateAccount(id: any, iban: any, countryCode: any, bankCode: any, currency: any, accountStatus: any, personID: any, status: any){
    this.account.id = id;
    this.account.iban = iban.value;
    this.account.countryCode = countryCode.value;
    this.account.bankCode = bankCode.value;
    this.account.currency = currency.value;
    this.account.accountStatus = accountStatus.value;
    this.account.personID = personID;
    this.account.status = status;
    this.accountService.updateAccount(id,this.account).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

  approveAccount(id: any){
    this.accountService.approveAccount(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

}
