import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';
import { Person } from '../person';
import { PersonService } from '../person.service';

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
  persons: Person[] = [];
  objectType= 'ACCOUNT';
  errorMessage='';
  role = sessionStorage.getItem('role');
  chooseAccountStatusFormGroup = new FormGroup({
    accountStatus: new FormControl()
  });
  statusList = ['OPEN','CLOSED','BLOCKED','BLOCK_CREDIT','BLOCK_DEBIT'];

  constructor(private accountService: AccountService, private personService: PersonService, private router: Router) { }

  ngOnInit(): void {
    this.getAccounts();
    this.personService.getAllPersons().subscribe(data => {
      this.persons = data;
    })
  }

  ngOnChanges() {
  }

  getAccounts(){
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
    })
  }

  getPersonDetails(id: number | undefined){
    if(this.persons){
      let person = this.persons.filter(pers => pers.id === id)[0];
      let personString = '';
      if(person){
        personString = person.firstName + " " + person.lastName;
      }
      return personString;
    }
    else{
      return '';
    }
  }

  getMyAccounts(){
    this.accountService.getAccountsOfUser().subscribe(data => {
      this.accounts = data;
    })
  }

  getApproveAccounts(){
    this.accountService.getAccountsByStatus('APPROVE').subscribe(data => {
      this.accounts = data;
    })
  }

  getActiveAccounts(){
    this.accountService.getAccountsByStatus('ACTIVE').subscribe(data => {
      this.accounts = data;
    })
  }

  getDeletedAccounts(){
    this.accountService.getAccountsByStatus('DELETE').subscribe(data => {
      this.accounts = data;
    })
  }

  updateAccount(id: any, iban: any, countryCode: any, bankCode: any, currency: any, personID: any, status: any){
    this.account.id = id;
    this.account.iban = iban;
    this.account.countryCode = countryCode;
    this.account.bankCode = bankCode;
    this.account.currency = currency;
    this.account.accountStatus = this.chooseAccountStatusFormGroup.value.accountStatus;
    this.account.personID = personID;
    this.account.status = status;
    this.accountService.updateAccount(id,this.account).subscribe(data => {
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
    }
    );
    
  }

  deleteAccount(id: any){
    this.accountService.deleteAccount(id).subscribe(data => {
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
    }
    );
    
  }

  approveAccount(id: any){
    this.accountService.approveAccount(id).subscribe(data => {
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
    }
    );
    
  }

  rejectAccount(id: number | undefined){
    this.accountService.rejectAccount(id).subscribe(data => {
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
    }
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

  filterItemsOfStatus(itemStatus: any){
    return this.statusList.filter(x => x != itemStatus);
  }

}
