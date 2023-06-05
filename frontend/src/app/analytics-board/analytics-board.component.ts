import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../transaction.service';
import { LegendPosition } from '@swimlane/ngx-charts';

@Component({
  selector: 'app-analytics-board',
  templateUrl: './analytics-board.component.html',
  styleUrls: ['./analytics-board.component.scss']
})
export class AnalyticsBoardComponent implements OnInit {

  show: string = "analyticsBoard";
  selectedClassifier: string = "overall";

  systemFraudulentTransactions = 0;
  systemGenuineTransactions = 0;

  pieChartColors = [{name: 'Fraud', value: '#eb7777'}, {name: 'Not Fraud', value: '#c9e7db'}]

  testingTimes: any[] = [
    { name: 'Best Overall', value: 0.696 },
    { name: 'Best Recall', value: 0.156 },
    { name: 'Fastest', value: 0.044 }
  ];

  data: any[] = [
    {
      name: 'Best Overall',
      series: [
        //{ name: 'Testing Time', value: 0.696 },
        { name: 'Precision', value: 0.864 },
        { name: 'Recall', value: 0.933 },
        { name: 'F1-score', value: 0.897 },
        { name: 'Accuracy', value: 0.999 },
        { name: 'AUPRC', value: 0.972 },
        { name: 'AUROC', value: 0.998 },
      ]
    },
    {
      name: 'Best Recall',
      series: [
        //{ name: 'Testing Time', value: 0.156 },
        { name: 'Precision', value: 0.534 },
        { name: 'Recall', value: 0.988 },
        { name: 'F1-score', value: 0.694 },
        { name: 'Accuracy', value: 0.997 },
        { name: 'AUPRC', value: 0.967 },
        { name: 'AUROC', value: 0.999 },
      ]
    },
    {
      name: 'Fastest',
      series: [
        //{ name: 'Testing Time', value: 0.044 },
        { name: 'Precision', value: 0.878 },
        { name: 'Recall', value: 0.898 },
        { name: 'F1-score', value: 0.888 },
        { name: 'Accuracy', value: 0.999 },
        { name: 'AUPRC', value: 0.767 },
        { name: 'AUROC', value: 0.948 },
      ]
    }
  ];

  performanceColorScheme = [
    { name: 'Best Overall', value: '#264b96' },
    { name: 'Best Recall', value: '#006f3c' } ,
    { name: 'Fastest', value: '#bf212f' }
  ];

  constructor(private transactionService: TransactionService) { 
  }

  ngOnInit(): void {
    this.transactionService.getFraudSystemClassifier().subscribe(data => {
      this.selectedClassifier = data.classifierType;
      console.log(data);
    })
    this.transactionService.getSystemTransactions().subscribe(data => {
      this.systemFraudulentTransactions = data.frauds;
      this.systemGenuineTransactions = data.genuine;
      console.log(data);
    });
  }

  viewAnalyticsBoard(){
    this.show = "analyticsBoard";
  }

  viewBasicResults(){
    this.show = "basicTable";
  }

  viewSamplingResults(){
    this.show = "samplingTable";
  }

  viewTuningResults(){
    this.show = "tuningTable";
  }

  onSelectClassifier(type:any){
    this.selectedClassifier = type;
    this.transactionService.setFraudSystemClassifier(this.selectedClassifier).subscribe(data => {
      console.log(data);
    });
  }

}
