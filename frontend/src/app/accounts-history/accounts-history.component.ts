import { Component, Input, OnInit } from '@angular/core';
import { AccountHistory } from '../account-history';
import { AccountService } from '../account.service';
import { Person } from '../person';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-accounts-history',
  templateUrl: './accounts-history.component.html',
  styleUrls: ['./accounts-history.component.scss']
})
export class AccountsHistoryComponent implements OnInit {

  accountsHistory: AccountHistory[] = [];
  persons: Person[] = [];

  constructor(private accountService: AccountService, private personService: PersonService) { }

  ngOnInit(): void {
    this.getAccountsHistory();
    this.personService.getAllPersons().subscribe(data => {
      this.persons = data;
    })
  }

  getAccountsHistory(){
    this.accountService.getHistoryOfAccounts().subscribe(data => {
      this.accountsHistory = data;
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

}
