import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LegendPosition } from '@swimlane/ngx-charts';

@Component({
  selector: 'app-transaction-error',
  templateUrl: './transaction-error.component.html',
  styleUrls: ['./transaction-error.component.scss']
})
export class TransactionErrorComponent implements OnInit {

  errorMessage: string = '';
  fraudProbability = 0;
  pieChartColors = [{name: 'Fraud', value: '#eb7777'}, {name: 'Not Fraud', value: '#c9e7db'}]

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    this.errorMessage = data.errorMessage;
    this.fraudProbability = data.fraudProbability;
   }

  ngOnInit(): void {
  }

}
