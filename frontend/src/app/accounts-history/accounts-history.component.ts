import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { Account } from '../account';
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
  accounts: Account[] = [];
  selectedIban: any;

  displayedColumns: string[] = ['#', 'iban', 'countryCode', 'bankCode', 'currency', 'account status', 'person', 'status', 'next status', 'timestamp'];
  dataSource!: MatTableDataSource<AccountHistory>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  private eventsSubscription!: Subscription;
  @Input() 
  events!: Observable<void>;

  constructor(private accountService: AccountService, private personService: PersonService) { }

  ngOnInit(): void {
    this.getAccountsHistory();
    this.getAccountsToSelect();
    this.personService.getAllPersons().subscribe(data => {
      this.persons = data;
      this.dataSource = new MatTableDataSource(this.accountsHistory);
      this.dataSource.paginator = this.paginator;
    })
    this.eventsSubscription = this.events.subscribe(() => this.getAccountsHistory());
  }

  getAccountsHistory(){
    this.accountService.getHistoryOfAccounts().subscribe(data => {
      this.accountsHistory = data;
      this.dataSource = new MatTableDataSource(this.accountsHistory);
      this.dataSource.paginator = this.paginator;
    })
  }

  getMyAccountsHistory(){
    this.accountService.getAccountsHistoryOfUser().subscribe(data => {
      this.accountsHistory = data;
      this.dataSource = new MatTableDataSource(this.accountsHistory);
      this.dataSource.paginator = this.paginator;
    })
  }

  filterByIban(){
    if(this.selectedIban){
      this.accountService.getAccountByIban(this.selectedIban).subscribe(data => {
        let id = data.id;
        this.accountService.getAccountsHistoryByAccountId(id).subscribe(history => {
          this.accountsHistory = history;
          this.dataSource = new MatTableDataSource(this.accountsHistory);
          this.dataSource.paginator = this.paginator;
        })
      })
      this.selectedIban = '';
    }
  }

  getAccountsToSelect(){
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

}
