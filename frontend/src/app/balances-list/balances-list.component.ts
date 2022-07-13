import { Component, OnInit } from '@angular/core';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';

@Component({
  selector: 'app-balances-list',
  templateUrl: './balances-list.component.html',
  styleUrls: ['./balances-list.component.scss']
})
export class BalancesListComponent implements OnInit {

  balances: Balance[] = [];

  constructor(private balanceService: BalanceService) { }

  ngOnInit(): void {
    this.getBalances();
  }

  getBalances(){
    this.balanceService.getAllBalances().subscribe(data => {
      this.balances = data;
    },
    err => console.log(err))
  }


}
