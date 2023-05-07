import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../transaction.service';

@Component({
  selector: 'app-analytics-board',
  templateUrl: './analytics-board.component.html',
  styleUrls: ['./analytics-board.component.scss']
})
export class AnalyticsBoardComponent implements OnInit {

  show: string = "analyticsBoard";
  selectedClassifier: string = "overall";

  constructor(private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.transactionService.getFraudSystemClassifier().subscribe(data => {
      this.selectedClassifier = data.classifierType;
      console.log(data);
    })
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
