import { Component, OnInit } from '@angular/core';
import { UserHistory } from '../user-history';
import { UserService } from '../user.service';

@Component({
  selector: 'app-users-history',
  templateUrl: './users-history.component.html',
  styleUrls: ['./users-history.component.scss']
})
export class UsersHistoryComponent implements OnInit {

  usersHistory: UserHistory[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getUsersHistory();
  }

  getUsersHistory(){
    this.userService.getHistoryOfUsers().subscribe(data => {
      this.usersHistory = data;
    })
  }
}
