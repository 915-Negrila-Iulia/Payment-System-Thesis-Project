import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Balance } from '../balance';
import { BalanceService } from '../balance.service';

@Component({
  selector: 'app-balances-list',
  templateUrl: './balances-list.component.html',
  styleUrls: ['./balances-list.component.scss']
})
export class BalancesListComponent implements OnInit {

  balances: Balance[] = [];
  accounts: Account[] = [];
  displayedColumns: string[] = ['#', 'account', 'available', 'total', 'timestamp'];
  dataSource!: MatTableDataSource<Balance>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private balanceService: BalanceService, private accountService: AccountService) { }

  ngOnInit(): void {
    this.getBalances();
    this.accountService.getAllAccounts().subscribe(data => {
      this.accounts = data;
      this.dataSource = new MatTableDataSource(this.balances);
      this.dataSource.paginator = this.paginator;
    })
  }

  getBalances(){
    this.balanceService.getAllBalances().subscribe(data => {
      this.balances = data;
    },
    err => console.log(err))
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
