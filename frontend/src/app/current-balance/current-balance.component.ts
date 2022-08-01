import { Component, Input, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';
import { Person } from '../person';
import { PersonService } from '../person.service';
import { StatisticDto } from '../statistic-dto';
import { TransactionService } from '../transaction.service';

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
  persons: Person[] = [];
  statistics: StatisticDto[] = [];

  constructor(private balanceService: BalanceService, private accountService: AccountService, private personService: PersonService,
    private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.getCurrentBalance();
    this.getAccount();
    this.personService.getAllPersons().subscribe(data => {
      this.persons = data;
    })
  }

  getCurrentBalance(){
    console.log(this.accountId);
    this.balanceService.getCurrentBalanceOfAccount(this.accountId!).subscribe(data => {
      this.currentBalance = data;
      console.log(data);
    },
    err => console.log(err))
    this.transactionService.getStatisticsOfAccount(this.accountId).subscribe(data => {
      this.statistics = data;
      console.log(data);
    })
  }

  getAccount(){
    this.accountService.getAccountById(this.accountId).subscribe(data => {
      this.account = data;
    },
    err => console.log(err))
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
