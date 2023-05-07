import { Component, OnInit, ViewChild, Input  } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { Papa } from 'ngx-papaparse';

@Component({
  selector: 'app-analytics-table-results',
  templateUrl: './analytics-table-results.component.html',
  styleUrls: ['./analytics-table-results.component.scss']
})
export class AnalyticsTableResultsComponent implements OnInit {

  @ViewChild(MatSort)
  sort!:MatSort;

  @Input()
  csvFilePath: string = "";

  @Input()
  type: string = "";

  displayedColumns: string[] = [];

  dataSource = new MatTableDataSource<any>();

  constructor(private papa: Papa) {}

  ngOnInit(): void {
    switch(this.type){
      case "BASICS":
        this.displayedColumns = ["Classifier","Precision","Recall","Accuracy","F1","AUROC","AUPRC","Training time (s)","Testing time (s)"];
        break;
      case "SAMPLING":
        this.displayedColumns = ["Classifier","Confusion Matrix","Precision","Recall","Accuracy","F1","AUROC","AUPRC","Training times (s)","Testing times (s)"];
        break;
      case "TUNING":
        this.displayedColumns = ["XGBoost + SMOTETomek params","Precision","Recall","Accuracy","F1","AUROC","AUPRC"];
        break;
    }
    this.loadCsvData();
  }

  loadCsvData() {
    fetch(this.csvFilePath)
      .then(response => response.blob())
      .then(blob => {
        const fileReader = new FileReader();
        fileReader.readAsText(blob);
        fileReader.onload = (e) => {
          const csvData = fileReader.result?.toString();
          if (csvData !== undefined) {
            console.log(csvData);
            this.papa.parse(csvData, {
              header: true,
              delimiter: ',',
              complete: (results) => {
                console.log(results.data.length)
                this.dataSource = new MatTableDataSource(results.data);
                this.dataSource.sort = this.sort;
              }
            });
          }
        };
        fileReader.onerror = (error) => {
          console.error(error);
        };
      });
  }
}
