import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../transaction.service';
import { colorSets } from '@swimlane/ngx-charts';

@Component({
  selector: 'app-analytics-board',
  templateUrl: './analytics-board.component.html',
  styleUrls: ['./analytics-board.component.scss']
})
export class AnalyticsBoardComponent implements OnInit {

  show: string = "analyticsBoard";
  selectedClassifier: string = "overall";
  performanceValues = [{name: 'Overall', value: 95.623}, {name: 'Recall', value: 89.02}, {name: 'Fast', value: 72.564}]
  colorScheme = colorSets.find(s => s.name === 'cool');
  showLegend = true;
  legendTitle = 'Performance';

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
