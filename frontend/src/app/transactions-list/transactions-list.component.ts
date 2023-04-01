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
  errorMessage='';
  selectedIban: string | undefined;
  role = sessionStorage.getItem('role');

  constructor(private transactionService: TransactionService, private accountService: AccountService) { }

  ngOnInit(): void {
    this.getTransactions();
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
      console.log(data);
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
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
    }
    );
  }

  rejectTransaction(id: number | undefined){
    this.transactionService.rejectTransaction(id).subscribe(data => {
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
    }
    );
  }

  filterByIban(){
    if(this.selectedIban){
      this.accountService.getAccountByIban(this.selectedIban).subscribe(data => {
        let id = data.id;
        this.transactionService.getTransactionsByAccountId(id).subscribe(trans => {
          this.transactions = trans;
        })
      })
      this.selectedIban = '';
    }
  }

  getApproveTransactions(){
    this.transactionService.getTransactionsByStatus('APPROVE').subscribe(data => {
      this.transactions = data;
    })
  }

  getActiveTransactions(){
    this.transactionService.getTransactionsByStatus('ACTIVE').subscribe(data => {
      this.transactions = data;
    })
  }

  getAuthorizeTransactions(){
    this.transactionService.getTransactionsByStatus('AUTHORIZE').subscribe(data => {
      this.transactions = data;
    })
  }

  getDeletedTransactions(){
    this.transactionService.getTransactionsByStatus('DELETE').subscribe(data => {
      this.transactions = data;
    })
  }

}
