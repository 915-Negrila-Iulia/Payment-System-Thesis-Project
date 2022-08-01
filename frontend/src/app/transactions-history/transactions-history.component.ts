import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { TransactionHistory } from '../transaction-history';
import { TransactionService } from '../transaction.service';

@Component({
  selector: 'app-transactions-history',
  templateUrl: './transactions-history.component.html',
  styleUrls: ['./transactions-history.component.scss']
})
export class TransactionsHistoryComponent implements OnInit {

  transactionsHistory: TransactionHistory[] = [];
  accounts: Account[] = [];
  displayedColumns: string[] = ['#', 'account', 'targetAccount', 'type', 'amount', 'action', 'status', 'next status', 'historyTimestamp'];
  dataSource!: MatTableDataSource<TransactionHistory>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private transactionService: TransactionService, private accountService: AccountService) { }

  ngOnInit(): void {
    this.getTransactionsHistory();
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
      this.dataSource = new MatTableDataSource(this.transactionsHistory);
      this.dataSource.paginator = this.paginator;
    })
  }

  getTransactionsHistory(){
    this.transactionService.getHistoryOfTransactions().subscribe(data => {
      this.transactionsHistory = data;
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

}
