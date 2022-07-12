import { Component, Input, OnInit } from '@angular/core';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';

@Component({
  selector: 'app-balance-history',
  templateUrl: './balance-history.component.html',
  styleUrls: ['./balance-history.component.scss']
})
export class BalanceHistoryComponent implements OnInit {

  @Input()
  accountId: number | undefined;
  balanceHistory: Balance[] = [];

  constructor(private balanceService: BalanceService) { }

  ngOnInit(): void {
    this.getBalanceHistory();
  }

  getBalanceHistory(){
    this.balanceService.getBalancesOfAccount(this.accountId!).subscribe(data => {
      this.balanceHistory = data;
    })
  }

}
