import { Component, OnInit } from '@angular/core';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Transaction } from '../transaction';
import { TransactionService } from '../transaction.service';

@Component({
  selector: 'app-transactions-list',
  templateUrl: './transactions-list.component.html',
  styleUrls: ['./transactions-list.component.scss']
})
export class TransactionsListComponent implements OnInit {

  transactions: Transaction[] = [];
  transaction: Transaction = new Transaction();
  accounts: Account[] = [];
  objectType = 'TRANSACTION';

  constructor(private transactionService: TransactionService, private accountService: AccountService) { }

  ngOnInit(): void {
    this.getTransactions();
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
    })
  }

  getTransactions(){
    this.transactionService.getAllTransactions().subscribe(data => {
      this.transactions = data;
    })
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

  approveTransaction(id: number | undefined){
    this.transactionService.approveTransaction(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

  rejectTransaction(id: number | undefined){
    this.transactionService.rejectTransaction(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

}
