import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-transactions-home',
  templateUrl: './transactions-home.component.html',
  styleUrls: ['./transactions-home.component.scss']
})
export class TransactionsHomeComponent implements OnInit {

  show: string = "transactionsList";

  constructor() { }

  ngOnInit(): void {
  }

  viewTransactions(){
    this.show = "transactionsList";
    window.location.reload();
  }

  viewHistory(){
    this.show = "transactionsHistory";
  }

}
