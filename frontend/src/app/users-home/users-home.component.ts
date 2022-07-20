import { Component, OnChanges, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-users-home',
  templateUrl: './users-home.component.html',
  styleUrls: ['./users-home.component.scss']
})
export class UsersHomeComponent implements OnInit {

  show: string = "usersList";

  constructor(private router: Router) { }

  ngOnInit(): void {
    
  }

  viewUsers(){
    this.show = "usersList";
    window.location.reload();
  }

  registerUser(){
    this.show = "registerUser";
  }

  viewHistory(){
    this.show = "usersHistory";
  }

}
