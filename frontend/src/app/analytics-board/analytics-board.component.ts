import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-analytics-board',
  templateUrl: './analytics-board.component.html',
  styleUrls: ['./analytics-board.component.scss']
})
export class AnalyticsBoardComponent implements OnInit {

  show: string = "analyticsBoard";

  constructor() { }

  ngOnInit(): void {
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

}
