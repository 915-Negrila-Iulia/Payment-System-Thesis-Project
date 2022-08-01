import { JsonPipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
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

  range = new FormGroup({
    start: new FormControl(),
    end: new FormControl(),
  });

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
      this.dataSource = new MatTableDataSource(this.balances);
      this.dataSource.paginator = this.paginator;
    },
    err => console.log(err))
  }

  filterByDateRange(){
    if(this.range.value != null){
      let startDateSplit = this.range.value['start'].toLocaleDateString().split('/');
      let endDateSplit = this.range.value['end'].toLocaleDateString().split('/');

      let startYear = startDateSplit[2], startMonth = startDateSplit[0], startDay = startDateSplit[1];
      let endYear = endDateSplit[2], endMonth = endDateSplit[0], endDay = endDateSplit[1];

      if(startMonth<10){
        startMonth = "0" + startMonth;
      }
      if(startDay<10){
        startDay = "0" + startDay;
      }
      if(endMonth<10){
        endMonth = "0" + endMonth;
      }
      if(endDay<10){
        endDay = "0" + endDay;
      }

      let startDate = startYear + "-" + startMonth + "-" + startDay;
      let endDate = endYear + "-" + endMonth + "-" + endDay;

      this.balanceService.getBalancesInDateRange(startDate,endDate).subscribe(data => {
        this.balances = data;
        this.dataSource = new MatTableDataSource(this.balances);
        this.dataSource.paginator = this.paginator;
      })
    }
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
