import { Component, OnInit } from '@angular/core';
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

  constructor(private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.getTransactions();
  }

  getTransactions(){
    this.transactionService.getAllTransactions().subscribe(data => {
      this.transactions = data;
    })
  }

  approveTransaction(id: number | undefined){
    this.transactionService.approveTransaction(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

  rejectTransaction(id: number | undefined){
    
  }

}
