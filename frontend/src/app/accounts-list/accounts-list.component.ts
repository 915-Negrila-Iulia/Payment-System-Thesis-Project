import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';

@Component({
  selector: 'app-accounts-list',
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.scss']
})
export class AccountsListComponent implements OnInit, OnChanges {

  accounts: Account[] = [];
  account: Account = new Account();
  currentBalance: Balance = new Balance();
  selectedAccountId: number | undefined;
  showDetails: boolean = false;
  doTransaction: boolean = false;

  constructor(private accountService: AccountService, private router: Router) { }

  ngOnInit(): void {
    this.getAccounts();
  }

  ngOnChanges() {
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

  selectAccount(id: any){
    this.selectedAccountId = id;
  }

  createTransaction(){
    this.doTransaction = true;
  }

  openPopup(){
    this.showDetails = true;
    document.getElementById('container-accounts')!.style.opacity = '20%';
  }

  closePopup(){
    this.showDetails = false;
    document.getElementById('container-accounts')!.style.opacity = '100%';
  }

}
