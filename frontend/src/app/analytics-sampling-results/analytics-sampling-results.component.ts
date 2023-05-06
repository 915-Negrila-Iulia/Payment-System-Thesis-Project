import { Component, OnInit, ViewChild  } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { Papa } from 'ngx-papaparse';

@Component({
  selector: 'app-analytics-sampling-results',
  templateUrl: './analytics-sampling-results.component.html',
  styleUrls: ['./analytics-sampling-results.component.scss']
})
export class AnalyticsSamplingResultsComponent implements OnInit {

  @ViewChild(MatSort)
  sort!:MatSort;

  displayedColumns: string[] = 
  ["Classifier",
  "Confusion Matrix",
  "Precision",
  "Recall",
  "Accuracy",
  "F1",
  "AUROC",
  "AUPRC",
  "Training times (s)",
  "Testing times (s)"];

  dataSource = new MatTableDataSource<any>();
  csvFilePath: string = '../../assets/basics_and_sampling.csv';

  constructor(private papa: Papa) {}

  ngOnInit(): void {
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
                console.log(results)
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
