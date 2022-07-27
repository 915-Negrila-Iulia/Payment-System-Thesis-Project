import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { UserHistory } from '../user-history';
import { UserService } from '../user.service';

@Component({
  selector: 'app-users-history',
  templateUrl: './users-history.component.html',
  styleUrls: ['./users-history.component.scss']
})
export class UsersHistoryComponent implements OnInit {

  usersHistory: UserHistory[] = [];
  displayedColumns: string[] = ['#', 'username', 'email', 'status', 'next status', 'timestamp'];
  dataSource!: MatTableDataSource<UserHistory>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getUsersHistory();
    this.userService.getHistoryOfUsers().subscribe(data => {
      this.usersHistory = data;
      this.dataSource = new MatTableDataSource(this.usersHistory);
      this.dataSource.paginator = this.paginator;
    })
  }

  getUsersHistory(){
    this.userService.getHistoryOfUsers().subscribe(data => {
      this.usersHistory = data;
    })
  }
}
