import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-accounts-home',
  templateUrl: './accounts-home.component.html',
  styleUrls: ['./accounts-home.component.scss']
})
export class AccountsHomeComponent implements OnInit {

  show: string | undefined;

  constructor() { }

  ngOnInit(): void {
  }

  viewAccounts(){
    this.show = "accountsList";
  }

  addAccount(){
    this.show = "addAccount";
  }

  viewHistory(){
    this.show = "accountsHistory";
  }

}
